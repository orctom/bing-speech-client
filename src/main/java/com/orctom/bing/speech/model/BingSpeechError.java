package com.orctom.bing.speech.model;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BingSpeechError extends BingSpeechMessage {

  private String message;
  private Throwable exception;

  public BingSpeechError(String message) {
    this(message, null);
  }

  public BingSpeechError(String message, Throwable exception) {
    this.type = BingSpeechMessageType.ERROR;
    this.message = message;
    this.exception = exception;
  }

  public String getMessage() {
    return message;
  }

  public Throwable getException() {
    return exception;
  }

  @Override
  public String toString() {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    exception.printStackTrace(pw);
    return message + ", " +sw.toString();
  }
}
