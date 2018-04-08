package com.orctom.bing.speech;

import com.google.common.io.ByteStreams;
import com.orctom.bing.speech.model.BingSpeechConfig;
import com.orctom.bing.speech.model.BingSpeechTranscript;
import com.orctom.speex4j.SpeexUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SimpleBingSpeechClientIT {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleBingSpeechClientIT.class);

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
    SimpleBingSpeechClient client = new SimpleBingSpeechClient(config);
    BingSpeechTranscript transcript = client.recognize(pcm, 3, TimeUnit.SECONDS);
    LOGGER.info("result: {}", transcript);
  }
}