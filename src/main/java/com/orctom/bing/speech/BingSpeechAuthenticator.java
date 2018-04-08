package com.orctom.bing.speech;

import com.orctom.bing.speech.exception.BingSpeechException;
import com.orctom.bing.speech.model.BingSpeechError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BingSpeechAuthenticator {

  private static final Logger LOGGER = LoggerFactory.getLogger(BingSpeechAuthenticator.class);

  private static final Map<String, WeakReference<BingSpeechAuthenticator>> REGISTRY = new HashMap<>();

  private static final String OCP_APIM_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";
  private static final int INTERVAL = 8;

  private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor(r -> {
    Thread t = Executors.defaultThreadFactory().newThread(r);
    t.setName("bing-speech-authenticator");
    t.setDaemon(true);
    return t;
  });

  private String subscriptionKey;
  private String endpoint;
  private String token;

  private BingSpeechAuthenticator(String subscriptionKey, String endpoint) {
    this.subscriptionKey = subscriptionKey;
    this.endpoint = endpoint;

    refreshToken();
    ses.scheduleAtFixedRate(this::refreshToken, INTERVAL, INTERVAL, TimeUnit.MINUTES);
  }

  public synchronized static BingSpeechAuthenticator getInstance(String subscriptionKey, String endpoint) {
    return REGISTRY.computeIfAbsent(subscriptionKey + "-" + endpoint, key ->
        new WeakReference<>(new BingSpeechAuthenticator(subscriptionKey, endpoint))
    ).get();
  }

  @Override
  protected void finalize() {
    try {
      ses.shutdownNow();
    } catch (Exception ignored) {
    }
  }

  private void refreshToken() {
    LOGGER.debug("Refreshing token");
    HttpsURLConnection connection = null;
    try {
      String charset = StandardCharsets.UTF_8.name();
      URL url = new URL(endpoint);

      connection = (HttpsURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty(OCP_APIM_SUBSCRIPTION_KEY, this.subscriptionKey);
      connection.setDoOutput(true);
      OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
      out.close();

      int responseCode = connection.getResponseCode();
      try (BufferedReader reader = new BufferedReader(
          new InputStreamReader(
              connection.getInputStream(), charset))) {
        StringBuilder res = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          res.append(line);
        }

        if (responseCode != HttpURLConnection.HTTP_OK) {
          LOGGER.error("Failed to refresh bing speech token, {} due to: {}", responseCode, res.toString());
          return;
        }

        this.token = res.toString();
      } catch (Throwable t) {
        LOGGER.error(t.getMessage(), t);
      }

    } catch (Throwable t) {
      LOGGER.error(t.getMessage(), t);

    } finally {
      if (null != connection) {
        connection.disconnect();
      }
    }
    LOGGER.debug("Token refreshed.");
  }

  public String getAccessToken() {
    if (null == token) {
      try {
        LOGGER.warn("waiting for 2 seconds to get token been refreshed.");
        TimeUnit.MILLISECONDS.sleep(2000);
      } catch (InterruptedException ignored) {
      }

      if (null == token) {
        throw new BingSpeechException("Token is NOT refreshed.");
      }
    }
    return token;
  }
}
