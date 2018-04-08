package com.orctom.bing.speech;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BingSpeechHeaders {

  public static final String X_CONNECTIONID = "X-ConnectionId";
  public static final String AUTHORIZATION = "Authorization";

  public static final String PATH = "Path";
  public static final String X_REQUESTID = "X-RequestId";
  public static final String X_TIMESTAMP = "X-Timestamp";

  public static final String CONTENT_TYPE = "Content-Type";

  public static final String PATH_CONFIG = "speech.config";
  public static final String PATH_AUDIO = "audio";

  public static final String PATH_RES_BOS = "Path:speech.startDetected";
  public static final String PATH_RES_FRAGMENT = "Path:speech.fragment";
  public static final String PATH_RES_HYPOTHESIS = "Path:speech.hypothesis";
  public static final String PATH_RES_PHRASE = "Path:speech.phrase";
  public static final String PATH_RES_EOS = "Path:speech.endDetected";
  public static final String PATH_RES_END = "Path:turn.end";

  public static final String CONTENT_TYPE_PCM = "audio/wav; codec=\"audio/pcm\"; samplerate=16000";
  public static final String CONTENT_TYPE_CFG = "application/json; charset=utf-8";

  public static final String SEPARATOR = "\r\n";

  private static final String CONFIG_PAYLOAD = "{"
      + "'context': {"
      + "  'system': {"
      + "    \"version\": \"2.0.12341\","
      + "  },"
      + "  'os': {"
      + "    'platform': 'Linux',"
      + "    'name': 'Debian',"
      + "    'version': '2.14324324'"
      + "  },"
      + "  'device': {"
      + "    'manufacturer': 'Contoso',"
      + "    'model': 'Fabrikan',"
      + "    'version': '7.341'"
      + "  },"
      + "}"
      + "}";

  public static String generateTimestamp() {
    return LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_DATE_TIME);
  }

  public static String generateSpeechConfigMessage() {
    Map<String, String> headers = new HashMap<>();
    headers.put(PATH, PATH_CONFIG);
    headers.put(CONTENT_TYPE, CONTENT_TYPE_CFG);
    headers.put(X_TIMESTAMP, generateTimestamp());
    return toHeader(headers) + SEPARATOR + SEPARATOR + CONFIG_PAYLOAD;
  }

  public static String toHeader(Map<String, String> headers) {
    return headers.entrySet().stream()
        .map(entry -> entry.getKey() + ": " + entry.getValue())
        .collect(Collectors.joining(SEPARATOR));
  }
}
