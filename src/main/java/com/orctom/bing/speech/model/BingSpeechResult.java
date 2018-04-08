package com.orctom.bing.speech.model;

import java.util.Collections;
import java.util.List;

import static com.orctom.bing.speech.model.BingSpeechMessageType.FRAGMENT;
import static com.orctom.bing.speech.model.BingSpeechMessageType.PHRASE;

public class BingSpeechResult extends BingSpeechMessage {

  private String status = "success";
  private List<BingSpeechTranscript> results;
  private boolean success = true;
  private boolean isFinal = false;

  public BingSpeechResult() {}

  public BingSpeechResult(String status, List<BingSpeechTranscript> results, boolean success, boolean isFinal) {
    this.type = isFinal ? PHRASE : FRAGMENT;
    this.status = status;
    this.results = results;
    this.success = success;
    this.isFinal = isFinal;
  }

  public static BingSpeechResult createInterimResult(String transcript, int offset, int duration) {
    BingSpeechResult result = new BingSpeechResult();
    result.type = FRAGMENT;
    result.offset = offset;
    result.duration = duration;
    result.results = Collections.singletonList(new BingSpeechTranscript(transcript));
    return result;
  }

  public static BingSpeechResult createFinalResult(String status, String transcript, int offset, int duration) {
    BingSpeechResult result = new BingSpeechResult();
    result.type = PHRASE;
    result.offset = offset;
    result.duration = duration;
    result.results = Collections.singletonList(new BingSpeechTranscript(transcript));
    return result;
  }

  public static BingSpeechResult createFinalResult(String status, List<BingSpeechTranscript> results, int offset, int duration) {
    BingSpeechResult result = new BingSpeechResult();
    result.type = PHRASE;
    result.status = status;
    result.success = "success".equals(status);
    result.isFinal = true;
    result.offset = offset;
    result.duration = duration;
    result.results = results;
    return result;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<BingSpeechTranscript> getResults() {
    return results;
  }

  public void setResults(List<BingSpeechTranscript> results) {
    this.results = results;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public boolean isFinal() {
    return isFinal;
  }

  @Override
  public String toString() {
    return "type=" + type +
        ", status=" + status +
        ", results=" + results +
        ", isFinal=" + isFinal +
        '}';
  }
}
