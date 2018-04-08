package com.orctom.bing.speech;

public abstract class BingSpeechConstants {

  public static String ENDPOINT_DEFAULT = "wss://speech.platform.bing.com";
  public static String ENDPOINT_CRIS = "wss://%1s.api.cris.ai";
  public static String ENDPOINT_PATH = "/speech/recognition/%1s/cognitiveservices/v1?language=%2s&format=%3s";

  public static String TOKEN_ENDPOINT_DEFAULT = "https://api.cognitive.microsoft.com/sts/v1.0/issueToken";
  public static String TOKEN_ENDPOINT_CRIS = "https://westus.api.cognitive.microsoft.com/sts/v1.0/issueToken";
}
