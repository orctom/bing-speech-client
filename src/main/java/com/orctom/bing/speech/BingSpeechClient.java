package com.orctom.bing.speech;

import com.orctom.bing.speech.model.BingSpeechConfig;
import com.orctom.bing.speech.model.BingSpeechError;
import com.orctom.bing.speech.model.BingSpeechMessage;
import com.orctom.bing.speech.model.BingSpeechTranscript;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static com.orctom.bing.speech.BingSpeechConstants.*;
import static com.orctom.bing.speech.BingSpeechHeaders.*;
import static com.orctom.bing.speech.model.BingSpeechMessageType.CLOSE;

public class BingSpeechClient {

  private static final Logger LOGGER = LoggerFactory.getLogger(BingSpeechClient.class);

  private static final Pattern PATTERN_HYPHEN = Pattern.compile("-");
  private static final String EMPTY_STRING = "";

  private static final byte[] POISON_PILL = new byte[0];

  private BingSpeechAuthenticator authenticator;

  private BingSpeechConfig config;
  private BingSpeechHandler handler;
  private String uuid;

  private Map<String, String> headers = new HashMap<>();

  private Session session;

  private AtomicBoolean sendingCompleted = new AtomicBoolean(false);

  public BingSpeechClient(BingSpeechConfig config, BingSpeechHandler handler) {
    this.config = config;
    this.handler = handler;
    this.uuid = PATTERN_HYPHEN.matcher(UUID.randomUUID().toString()).replaceAll(EMPTY_STRING);

    headers.put(PATH, PATH_AUDIO);
    headers.put(CONTENT_TYPE, CONTENT_TYPE_PCM);
    headers.put(X_REQUESTID, uuid);

    authenticator = BingSpeechAuthenticator.getInstance(config.getSubscriptionKey(), getAuthenticationEndpoint());
  }

  public BingSpeechClient connect() throws IOException, DeploymentException {
    BingSpeechClientEndpointConfigurator configurator = buildConfigurator();
    ClientEndpointConfig cfg = ClientEndpointConfig.Builder.create().configurator(configurator).build();
    URI uri = getURI();
    ClientManager client = ClientManager.createClient();
    client.getProperties().put(ClientProperties.RECONNECT_HANDLER, new ReconnectionHandler());
    connectSync(client, cfg, uri);

    return this;
  }

  private BingSpeechClientEndpointConfigurator buildConfigurator() {
    Map<String, List<String>> connectionHeaders = new HashMap<>();
    connectionHeaders.put(X_CONNECTIONID, Collections.singletonList(uuid));
    connectionHeaders.put(AUTHORIZATION, Collections.singletonList(authenticator.getAccessToken()));
    return new BingSpeechClientEndpointConfigurator(connectionHeaders);
  }

  public void recognize(byte[] pcm, boolean isLast) {
    recognize(pcm);

    if (isLast) {
      complete();
    }
  }

  public void recognize(byte[] pcm) {
    if (null == pcm || 0 == pcm.length) {
      return;
    }

    LOGGER.info("recognize uuid: {}", uuid);
    RemoteEndpoint.Async asyncRemote = session.getAsyncRemote();
    asyncRemote.sendBinary(wrapMessage(pcm));
  }

  public void complete() {
    if (sendingCompleted.getAndSet(true)) {
      return;
    }

    LOGGER.info("complete: {}", uuid);
    RemoteEndpoint.Async asyncRemote = session.getAsyncRemote();
    asyncRemote.sendBinary(wrapMessage(POISON_PILL));
  }

  private URI getURI() {
    String bingSpeechEndpoint = getBingSpeechEndpoint();
    String path = String.format(ENDPOINT_PATH, config.getMode(), config.getLanguage(), config.getFormat());
    return URI.create(bingSpeechEndpoint + path);
  }

  private String getAuthenticationEndpoint() {
    String crisKey = config.getCrisKey();
    if (null == crisKey || 0 == crisKey.trim().length()) {
      return TOKEN_ENDPOINT_DEFAULT;
    } else {
      return TOKEN_ENDPOINT_CRIS;
    }
  }

  private String getBingSpeechEndpoint() {
    String crisKey = config.getCrisKey();
    if (null == crisKey || 0 == crisKey.trim().length()) {
      return ENDPOINT_DEFAULT;
    } else {
      return String.format(ENDPOINT_CRIS, crisKey);
    }
  }

  private void connectSync(ClientManager client, ClientEndpointConfig config, URI uri)
      throws IOException, DeploymentException {
    LOGGER.info("Connecting to {}", uri);
    session = client.connectToServer(new BingSpeechEndpoint(), config, uri);
  }

  private ByteBuffer wrapMessage(byte[] payload) {
    headers.put(X_TIMESTAMP, generateTimestamp());
    String header = toHeader(headers);
    byte[] headerBytes = header.getBytes();
    byte[] message = new byte[headerBytes.length + payload.length + 2];
    byte[] headerSizePrefix = new byte[2];
    headerSizePrefix[0] = (byte) ((headerBytes.length >> 8) & 0xFF);
    headerSizePrefix[1] = (byte) (headerBytes.length & 0xFF);
    System.arraycopy(headerSizePrefix, 0, message, 0, 2);
    System.arraycopy(headerBytes, 0, message, 2, headerBytes.length);
    System.arraycopy(payload, 0, message, 2 + headerBytes.length, payload.length);

    return ByteBuffer.wrap(message);
  }

  class BingSpeechEndpoint extends Endpoint {
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
      LOGGER.debug("onOpen, uuid: {}", uuid);
      session.addMessageHandler(String.class, text -> {
        LOGGER.trace("onMessage: {}", text);
        BingSpeechMessage msg = BingSpeechMessageParser.parse(text);
        if (null == msg) {
          return;
        }

        try {
          handler.onMessage(msg);
        } catch (Throwable t) {
          handler.onMessage(new BingSpeechError(t.getMessage(), t));
        }
      });

      RemoteEndpoint.Async asyncRemote = session.getAsyncRemote();
      asyncRemote.sendText(generateSpeechConfigMessage());
    }

    @Override
    public void onError(Session session, Throwable t) {
      LOGGER.debug("onError: {}", t.getMessage());
      handler.onMessage(new BingSpeechError(t.getMessage(), t));
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
      LOGGER.debug("onClose: {}", closeReason);
      handler.onMessage(new BingSpeechMessage(CLOSE));
    }
  }

  class ReconnectionHandler extends ClientManager.ReconnectHandler {
    private int counter = 0;

    @Override
    public boolean onDisconnect(CloseReason closeReason) {
      counter++;
      if (counter <= 3) {
        LOGGER.debug("Reconnecting... count: {}", counter);
        return true;
      } else {
        return false;
      }
    }

    @Override
    public boolean onConnectFailure(Exception exception) {
      counter++;
      if (counter <= 3) {
        LOGGER.warn("Reconnecting... count: {}, msg: {}", counter, exception.getMessage());
        try {
          TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException ignored) {
        }
        return true;
      } else {
        return false;
      }
    }

    @Override
    public long getDelay() {
      return 1;
    }
  }

}
