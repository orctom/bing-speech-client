package com.orctom.bing.speech;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orctom.bing.speech.model.BingSpeechMessage;
import com.orctom.bing.speech.model.BingSpeechResult;
import com.orctom.bing.speech.model.BingSpeechTranscript;

import java.util.ArrayList;
import java.util.List;

import static com.orctom.bing.speech.BingSpeechHeaders.*;
import static com.orctom.bing.speech.model.BingSpeechMessageType.*;

public abstract class BingSpeechMessageParser {

  public static BingSpeechMessage parse(String message) {
    String[] items = message.split(SEPARATOR + SEPARATOR);
    if (2 != items.length) {
      return null;
    }

    String header = items[0];
    String payload = items[1];
    JSONObject json = JSON.parseObject(payload);
    String[] headers = header.split(SEPARATOR);
    for (String key : headers) {
      if (!key.startsWith(PATH)) {
        continue;
      }
      switch (key) {
        case PATH_RES_BOS: {
          return new BingSpeechMessage(BOS, json.getInteger("Offset"), 0);
        }
        case PATH_RES_HYPOTHESIS:
        case PATH_RES_FRAGMENT: {
          String transcript = json.getString("Text");
          int offset = json.getInteger("Offset");
          int duration = json.getInteger("Duration");
          return BingSpeechResult.createInterimResult(transcript, offset, duration);
        }
        case PATH_RES_PHRASE: {
          String status = json.getString("RecognitionStatus").toLowerCase();
          int offset = json.getInteger("Offset");
          int duration = json.getInteger("Duration");
          JSONArray nbest = json.getJSONArray("NBest");
          if (null != nbest) {
            return getBingSpeechNBestResult(status, offset, duration, nbest);
          }

          String transcript = json.getString("DisplayText");
          if (null != transcript) {
            return BingSpeechResult.createFinalResult(status, transcript, offset, duration);
          }
        }
        case PATH_RES_EOS: {
          return new BingSpeechMessage(EOS, json.getInteger("Offset"), 0);
        }
        case PATH_RES_END: {
          return new BingSpeechMessage(END);
        }
      }
    }
    return null;
  }

  private static BingSpeechMessage getBingSpeechNBestResult(String status, int offset, int duration, JSONArray nbest) {
    List<BingSpeechTranscript> results = new ArrayList<>(nbest.size());
    for (Object o : nbest) {
      if (o instanceof JSONObject) {
        JSONObject node = (JSONObject) o;
        String lexical = node.getString("Lexical");
        String itn = node.getString("ITN");
        String masked = node.getString("MaskedITN");
        String display = node.getString("Display");
        float confidence = node.getFloat("Confidence");
        results.add(new BingSpeechTranscript(lexical, itn, masked, display, confidence));
      }
    }
    return BingSpeechResult.createFinalResult(status, results, offset, duration);
  }
}
