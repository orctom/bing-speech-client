package com.orctom.bing.speech;

import com.orctom.bing.speech.exception.BingSpeechException;
import com.orctom.bing.speech.model.BingSpeechConfig;
import com.orctom.bing.speech.model.BingSpeechError;
import com.orctom.bing.speech.model.BingSpeechMessage;
import com.orctom.bing.speech.model.BingSpeechResult;
import com.orctom.bing.speech.model.BingSpeechTranscript;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SimpleBingSpeechClient {

  private BingSpeechClient client;
  private CompletableFuture<BingSpeechTranscript> future;

  public SimpleBingSpeechClient(BingSpeechConfig config) {
    client = new BingSpeechClient(config, new SimpleBingSpeechHandler());
  }

  public synchronized BingSpeechTranscript recognize(byte[] pcm, long waitFor, TimeUnit unit) {
    try {
      client.connect();
      future = new CompletableFuture<>();
      client.recognize(pcm);
      client.complete();
      return future.get(waitFor, unit);
    } catch (Exception e) {
      throw new BingSpeechException(e.getMessage(), e);
    }
  }

  class SimpleBingSpeechHandler extends AbstractBingSpeechHandler {

    private BingSpeechTranscript transcript;

    @Override
    public void onPhrase(BingSpeechResult result) {
      List<BingSpeechTranscript> results = result.getResults();
      if (null == results || results.isEmpty()) {
        return;
      }

      BingSpeechTranscript best = results.get(0);
      if (null == transcript) {
        transcript = best;
        return;
      }

      transcript.setConfidence(best.getConfidence());
      transcript.setLexical(transcript.getLexical() + " " + best.getLexical());
      transcript.setItn(transcript.getItn() + " " + best.getItn());
      transcript.setMasked(transcript.getMasked() + " " + best.getMasked());
      transcript.setDisplay(transcript.getDisplay() + " " + best.getDisplay());
    }

    @Override
    public void onEnd(BingSpeechMessage message) {
      future.complete(transcript);
    }

    @Override
    public void onError(BingSpeechError error) {
      future.completeExceptionally(error.getException());
    }
  }
}
