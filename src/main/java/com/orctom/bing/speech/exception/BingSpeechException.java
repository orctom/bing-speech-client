package com.orctom.bing.speech.exception;

public class BingSpeechException extends RuntimeException {

  public BingSpeechException(String message) {
    super(message);
  }

  public BingSpeechException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public Throwable fillInStackTrace() {
    return null;
  }
}
