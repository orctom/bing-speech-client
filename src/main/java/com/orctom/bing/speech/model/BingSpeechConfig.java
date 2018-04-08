package com.orctom.bing.speech.model;

public class BingSpeechConfig {

  private String subscriptionKey;
  private RecognitionMode mode;
  private RecognitionFormat format;
  private String language;
  private String crisKey;

  private BingSpeechConfig(String subscriptionKey,
                           RecognitionMode mode,
                           RecognitionFormat format,
                           String language,
                           String crisKey) {
    this.subscriptionKey = subscriptionKey;
    this.mode = mode;
    this.format = format;
    this.language = language;
    this.crisKey = crisKey;
  }

  public String getSubscriptionKey() {
    return subscriptionKey;
  }

  public RecognitionMode getMode() {
    return mode;
  }

  public RecognitionFormat getFormat() {
    return format;
  }

  public String getLanguage() {
    return language;
  }

  public String getCrisKey() {
    return crisKey;
  }

  public static BingSpeechConfigBuilder builder() {
    return new BingSpeechConfigBuilder();
  }

  public static class BingSpeechConfigBuilder {
    private String subscriptionKey;
    private RecognitionMode mode = RecognitionMode.dictation;
    private RecognitionFormat format = RecognitionFormat.detailed;
    private String language = "en-US";
    private String crisKey;

    public BingSpeechConfigBuilder subscriptionKey(String subscriptionKey) {
      this.subscriptionKey = subscriptionKey;
      return this;
    }

    public BingSpeechConfigBuilder mode(RecognitionMode mode) {
      this.mode = mode;
      return this;
    }

    public BingSpeechConfigBuilder format(RecognitionFormat format) {
      this.format = format;
      return this;
    }

    public BingSpeechConfigBuilder language(String language) {
      this.language = language;
      return this;
    }

    public BingSpeechConfigBuilder crisKey(String crisKey) {
      this.crisKey = crisKey;
      return this;
    }

    public BingSpeechConfig build() {
      return new BingSpeechConfig(subscriptionKey, mode, format, language, crisKey);
    }
  }
}
