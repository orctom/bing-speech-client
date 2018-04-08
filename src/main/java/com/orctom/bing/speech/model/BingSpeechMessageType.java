package com.orctom.bing.speech.model;

public enum BingSpeechMessageType {

  BOS("speech.startDetected"),
  EOS("speech.endDetected"),
  HYPOTHESIS("speech.hypothesis"),
  FRAGMENT("speech.fragment"),
  PHRASE("speech.phrase"),
  END(null),
  CLOSE(null),
  ERROR(null);

  private String path;

  BingSpeechMessageType(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
