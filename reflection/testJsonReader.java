package edu.brown.cs.student.main.reflection;

import java.io.IOException;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

public class testJsonReader {

  jsonReader testJsonReader;

  public testJsonReader() {
    this.testJsonReader = new jsonReader();
  }

  @Test
  public void testJsonLoad() throws IOException {
    this.testJsonReader.readJson("src/main/java/edu.brown.cs.student.main/reflection/jsonFIle");
    Map<String, Object> jsonMap = this.testJsonReader.getJsonMap();
    Assert.assertNotNull(jsonMap);
    Assert.assertTrue(jsonMap.containsKey("Presidents"));
  }

  @Test
  public void invalidJsonLoad() throws IOException{
    jsonReader invaldJsonReader = new jsonReader();
    invaldJsonReader.readJson("src/path/to/invalidJson");
    Map<String, Object> jsonMap = invaldJsonReader.getJsonMap();
    Assert.assertNull(jsonMap);
  }
}