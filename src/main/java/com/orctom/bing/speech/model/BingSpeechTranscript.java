package com.orctom.bing.speech.model;

public class BingSpeechTranscript {

  private String lexical;
  private String itn;
  private String masked;
  private String display;
  private float confidence;

  public BingSpeechTranscript(String display) {
    this.display = display;
  }

  public BingSpeechTranscript(String lexical, String itn, String masked, String display, float confidence) {
    this.lexical = lexical;
    this.itn = itn;
    this.masked = masked;
    this.display = display;
    this.confidence = confidence;
  }

  public String getLexical() {
    return lexical;
  }

  public void setLexical(String lexical) {
    this.lexical = lexical;
  }

  public String getItn() {
    return itn;
  }

  public void setItn(String itn) {
    this.itn = itn;
  }

  public String getMasked() {
    return masked;
  }

  public void setMasked(String masked) {
    this.masked = masked;
  }

  public String getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  public float getConfidence() {
    return confidence;
  }

  public void setConfidence(float confidence) {
    this.confidence = confidence;
  }

  @Override
  public String toString() {
    return "BingSpeechTranscript{" +
        "display='" + display + '\'' +
        ", confidence=" + confidence +
        '}';
  }
}
