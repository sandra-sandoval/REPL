package edu.brown.cs.student.main.reflection;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okio.BufferedSource;
import okio.Okio;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class jsonReader {
  private Map<String, Object> jsonMap;

  public void readJson(String file) {
    Moshi moshi = new Moshi.Builder().build();

    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    File jsonFile = new File(file);
    System.out.println("jsonFile exists: " + jsonFile.exists());

    try {
      if (jsonFile.exists()) {
        BufferedSource buffer = Okio.buffer(Okio.source(jsonFile));
        this.jsonMap = adapter.fromJson(buffer);
        buffer.close();
        System.out.println(jsonMap);
      } else {
        System.out.println("Error: JSON file does not exist.");
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("IOException: Error reading JSON file.");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("An unexpected error has occurred.");
    }
  }

  public Map<String, Object> getJsonMap() {
    System.out.println("jsonMap: " + this.jsonMap);
    return this.jsonMap;
  }
}
