package com.orctom.bing.speech;

import javax.websocket.ClientEndpointConfig;
import java.util.List;
import java.util.Map;

public class BingSpeechClientEndpointConfigurator extends ClientEndpointConfig.Configurator {

  private Map<String, List<String>> headers;

  public BingSpeechClientEndpointConfigurator(Map<String, List<String>> headers) {
    this.headers = headers;
  }

  @Override
  public void beforeRequest(Map<String, List<String>> headers) {
    headers.putAll(this.headers);
  }
}
