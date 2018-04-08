package com.orctom.bing.speech;

import com.orctom.bing.speech.model.BingSpeechMessage;

@FunctionalInterface
public interface BingSpeechHandler {

  void onMessage(BingSpeechMessage message);

}
