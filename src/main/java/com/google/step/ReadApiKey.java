package com.google.step;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReadApiKey {
  private static final String FAKE_API_KEY = "api-key";

  public static String readMapApiKey(Class<?> c) {
    String content;
    try {
      File file = new File(c.getResource("/api-key.json").getFile());
      content = new String(Files.readAllBytes(file.toPath()));
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
      return FAKE_API_KEY;
    }

    JsonObject jsonObject;
    try {
      jsonObject = JsonParser.parseString(content).getAsJsonObject();
      return jsonObject.get("map-api-key").getAsString();
    } catch (JsonSyntaxException e) {
      e.printStackTrace();
      return FAKE_API_KEY;
    }
  }
}
