package com.orctom.bing.speech;

import com.google.common.io.ByteStreams;
import com.orctom.bing.speech.model.BingSpeechConfig;
import com.orctom.bing.speech.model.BingSpeechMessage;
import com.orctom.speex4j.SpeexUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.DeploymentException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.orctom.bing.speech.model.BingSpeechMessageType.*;

public class BingSpeechClientIT {

  private static final Logger LOGGER = LoggerFactory.getLogger(BingSpeechClientIT.class);

  @Test
  public void recognize() throws Exception {
    String subscriptionKey = System.getProperty("bing.speech.key");
    if (null == subscriptionKey) {
      throw new RuntimeException("not found 'bing.speech.key'");
    }
    InputStream in = new FileInputStream(new File("/Users/orctom/data/voice/sample.spx"));
    byte[] spx = ByteStreams.toByteArray(in);
    byte[] pcm = SpeexUtils.spx2pcm(spx);
    BingSpeechConfig config = BingSpeechConfig.builder()
        .subscriptionKey(subscriptionKey)
        .build();
    ExecutorService es = Executors.newFixedThreadPool(5);
    CountDownLatch mainLatch = new CountDownLatch(5);
    for (int i = 0; i < 5; i++) {
      es.submit(() -> {
        try {
          CountDownLatch latch = new CountDownLatch(1);
          BingSpeechClient client = new BingSpeechClient(config, message -> {
            LOGGER.info("message: {}", message);
            if (null == message || null == message.getType()) {
              return;
            }

            if (PHRASE == message.getType() || CLOSE == message.getType() || ERROR == message.getType()) {
              latch.countDown();
            }

          });
          client.connect();
          client.recognize(pcm, true);
          latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
          LOGGER.error(e.getMessage(), e);
        } finally {
          mainLatch.countDown();
        }
      });
    }

    mainLatch.await(20, TimeUnit.SECONDS);
    LOGGER.info("existing.");
  }
}