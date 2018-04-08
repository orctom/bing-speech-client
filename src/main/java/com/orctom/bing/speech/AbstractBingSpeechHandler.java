package com.orctom.bing.speech;

import com.orctom.bing.speech.model.BingSpeechError;
import com.orctom.bing.speech.model.BingSpeechMessage;
import com.orctom.bing.speech.model.BingSpeechResult;

public abstract class AbstractBingSpeechHandler implements BingSpeechHandler {

  @Override
  public void onMessage(BingSpeechMessage message) {
    if (null == message) {
      return;
    }

    if (message instanceof BingSpeechResult) {
      BingSpeechResult result = (BingSpeechResult) message;
      switch (result.getType()) {
        case FRAGMENT: {onFragment(result); break;}
        case PHRASE: {onPhrase(result); break;}
      }

    } else if (message instanceof BingSpeechError) {
      onError((BingSpeechError) message);

    } else {
      switch (message.getType()) {
        case BOS: {onBOS(message); break;}
        case EOS: {onEOS(message); break;}
        case END: {onEnd(message); break;}
      }
    }
  }

  public void onBOS(BingSpeechMessage message) {}
  public void onEOS(BingSpeechMessage message) {}
  public void onFragment(BingSpeechResult result) {}
  public abstract void onPhrase(BingSpeechResult result);
  public abstract void onEnd(BingSpeechMessage message);
  public abstract void onError(BingSpeechError error);
}
