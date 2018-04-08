package com.orctom.bing.speech.model;

public class BingSpeechMessage {

  protected BingSpeechMessageType type;

  protected int offset;
  protected int duration;

  public BingSpeechMessage() {
  }

  public BingSpeechMessage(BingSpeechMessageType type) {
    this(type, 0, 0);
  }

  public BingSpeechMessage(BingSpeechMessageType type, int offset, int duration) {
    this.type = type;
    this.offset = offset;
    this.duration = duration;
  }

  public BingSpeechMessageType getType() {
    return type;
  }

  public void setType(BingSpeechMessageType type) {
    this.type = type;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  @Override
  public String toString() {
    return "type=" + type;
  }
}
