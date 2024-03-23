package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Handlers.LoadCSVHandler;
import edu.brown.cs.student.main.Handlers.LoadCSVHandler.LoadCSVParameterFailureResponse;
import edu.brown.cs.student.main.Loading.FactoryFailureException;
import edu.brown.cs.student.main.Loading.StringCreator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import okio.BufferedSource;
import okio.Okio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class LoadCSVTest {

  static LoadCSVHandler parse1;
  static LoadCSVHandler parse2;
  static LoadCSVHandler parse3;
  static List<String> row1;
  static List<String> row2;
  static StringCreator strCreator;
  static final Map<String, Object> responseMap = new HashMap<>();
  private final Type mapStringObject = Types.newParameterizedType(Map.class, String.class,
      Object.class);
  private JsonAdapter<Map<String, Object>> adapter;

  @BeforeAll
  public static void setupAll() throws IOException {
    // Pick an arbitrary free port
    Spark.port(0);
    // Eliminate logger spam in console for test suite
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root

    strCreator = new StringCreator();
    parse2 = new LoadCSVHandler();
    parse3 = new LoadCSVHandler();
    row1 = new ArrayList<>();
    row1.add("Tina, 78, \"90,754\", true");
    row2 = new ArrayList<>();
    row2.add("Jack, , \"\"876, false ");


  }

  /**
   * Shared state for all tests. We need to be able to mutate it (adding recipes etc.) but never
   * need to replace the reference itself. We clear this state out after every test runs.
   */

  //final Set<String> menu = new HashSet<>(); // maybe replace String with Object
  @BeforeEach
  public void setupEach() {
    Moshi moshi = new Moshi.Builder().build();
    this.adapter = moshi.adapter(mapStringObject);
    responseMap.clear();

    Spark.get("/loadcsv", new LoadCSVHandler());
    Spark.init();
    Spark.awaitInitialization();
  }

  /**
   * Gracefully stop Spark listening on both endpoints
   */
  @AfterEach
  public void teardown() {
    Spark.unmap("/loadcsv");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }


  /**
   * To test if LoadCSVHandler's constructor instantiates it correctly
   */
  @Test
  public void testConstructor() {
    LoadCSVHandler parsing = new LoadCSVHandler();
    assertNotNull(parsing);
    assertFalse(parsing.isLoaded());
  }


  /**
   * Testing the parsing function of LoadCSVHandler, also checking for conditions where headers are
   * included or not included
   *
   * @throws IOException
   */
  @Test
  public void testLoad() throws IOException, FactoryFailureException {
    // Checking if different types of Reader class objects are accepted and parsed correctly
    parse2.load("words_in_a_line.csv", "n");
    List<List<String>> output = parse2.getData();
    Assert.assertEquals(output.size(), 3);
    Assert.assertEquals(output.get(0).size(), 2);
    // Checking for correct parsing
    Assert.assertEquals(output.get(1).size(), 4);
    Assert.assertEquals(output.get(2).size(), 3);
    Assert.assertEquals(output.get(2).get(1), "in, a");
    Assert.assertEquals(parse2.getHeaders().size(), 0);

    // Checking for correct parsing of files
    parse3.load("ri_city_town_income_acs.csv", "y");
    List<List<String>> output2 = parse3.getData();
    Assert.assertEquals(output2.size(), 41);
    Assert.assertEquals(output2.get(0).size(), 4);
    Assert.assertEquals(output2.get(3).get(2), "115,740.00");
    Assert.assertEquals(parse3.getHeaders().size(), 4);

    // Checking if headers are not indicated, file is parsed accordingly
    LoadCSVHandler parse5 = new LoadCSVHandler();
    parse5.load("ri_city_town_income_acs.csv", "n");
    List<List<String>> output6 = parse5.getData();
    Assert.assertEquals(parse5.getHeaders().size(), 0);
  }

  // SERVER TESTING

  /**
   * Helper to start a connection to a specific API endpoint/params
   *
   * @param apiCall the call string, including endpoint (NOTE: this would be better if it had more
   *                structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  static private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
    clientConnection.setRequestProperty("Content-Type", "application/json"); // Just added
    clientConnection.setRequestProperty("Accept", "application/json");        // Just added


    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    //clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }


  /**
   * @throws IOException
   */

  @Test
  // Recall that the "throws IOException" doesn't signify anything but acknowledgement to the type checker
  public void testAPINoRecipes() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    // Now we need to see whether we've got the expected Json response.
    // SoupAPIUtilities handles ingredient lists, but that's not what we've got here.
    Moshi moshi = new Moshi.Builder().build();
    // We'll use okio's Buffer class here
    LoadCSVHandler.LoadCSVNoFileFailureResponse response =
        moshi.adapter(LoadCSVHandler.LoadCSVNoFileFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.

    clientConnection.disconnect();
  }



  @Test
  public void testLoadCSVSuccessResponse() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    LoadCSVHandler loadH = new LoadCSVHandler();
    String csvFile1 = "dol_ri_earnings_disparity.csv";
    String headY = "&headers=Y";
    String headN = "&headers=N";

    String apiCall1 = "loadcsv?filepath=" + csvFile1 + headY;
    //String apiCall2 = "loadcsv?filepath=dol_ri_earnings_disparity.csv&headers=Y";

    HttpURLConnection tried = this.tryRequest(apiCall1);
    System.out.print(tried);



    //tried.setRequestMethod("GET");
    int responseCode = tried.getResponseCode();


    if (responseCode == HttpURLConnection.HTTP_OK) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(tried.getInputStream()));
      StringBuilder jsonString = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        jsonString.append(line);
      }
      reader.close();
      Map<String, Object> responseObject = this.adapter.fromJson(jsonString.toString());
      // System.out.println(responseObject);

      for (Map.Entry<String, Object> entry : responseObject.entrySet()) {
        System.out.println(entry.getKey() + ": " + entry.getValue());
      }
      assertEquals(200, responseCode);
      assertEquals(responseObject.get("result"), "success");
      assertEquals(responseObject.get("File"), "dol_ri_earnings_disparity.csv");



      tried.getInputStream();
      tried.disconnect();

    } else{
      System.out.println("Response Code: " + responseCode);
      //assertEquals(500, responseCode);
    }

  }



  @Test
  public void testLoadCSVNoFileFailureResponse() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    LoadCSVHandler loadH = new LoadCSVHandler();
    String csvFile1 = "mssingfile";
    String headY = "&headers=Y";
    String headN = "&headers=N";

    String apiCall1 = "loadcsv?filepath=" + csvFile1 + headY;
    //String apiCall2 = "loadcsv?filepath=dol_ri_earnings_disparity.csv&headers=Y";

    HttpURLConnection clientConnection = tryRequest(apiCall1);

    int responseCode = clientConnection.getResponseCode();

    InputStream inputStream;

    if (responseCode >= 400) {
      // If there is an error response, read from the error stream
      inputStream = clientConnection.getErrorStream();
      assertEquals(500, responseCode);
    } else {
      // For successful responses, read from the input stream
      inputStream = clientConnection.getInputStream();
    }

    try (InputStream is = inputStream) {
      if (is != null) {
        // Read the response body into a String
        BufferedSource bufferedSource = Okio.buffer(Okio.source(is));
        String responseBody = bufferedSource.readUtf8();
        System.out.println(responseBody);

        // Parse the response body as JSON
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        Moshi moshi = new Moshi.Builder().build();
        Object adapter = moshi.adapter(mapStringObject);
        try {
          // This line parses the JSON response into a Map<String, Object>
          Map<String, Object> body;
          body = ((JsonAdapter<Map<String, Object>>) adapter).fromJson(responseBody);
          assertNotNull(body);
          assertEquals("error_bad_request", body.get("result"));
          // Further processing with the parsed body
          System.out.println(body);
        } catch (IOException e) {
                    // Handle the parsing exception
        }

        // Perform assertions

      }
    } finally {
      clientConnection.disconnect();
    }
  }

  @Test
  public void testLoadCSVParameterFailureResponse() throws IOException {
    /////////// LOAD DATASOURCE ///////////
    LoadCSVHandler loadH = new LoadCSVHandler();
    String csvFile1 = "dol_ri_earnings_disparity.csv";
    String headY = "&headers=Y";
    String headBad = "&headers=bad";
    String headN = "&headers=N";

    String apiCall1 = "loadcsv?filepath=" + csvFile1 + headBad;
    //String apiCall2 = "loadcsv?filepath=dol_ri_earnings_disparity.csv&headers=Y";

    HttpURLConnection clientConnection = tryRequest(apiCall1);

    int responseCode = clientConnection.getResponseCode();

    InputStream inputStream;

    if (responseCode >= 400) {
      // If there is an error response, read from the error stream
      inputStream = clientConnection.getErrorStream();
      assertEquals(500, responseCode);
    } else {
      // For successful responses, read from the input stream
      inputStream = clientConnection.getInputStream();
    }

    try (InputStream is = inputStream) {
      if (is != null) {
        // Read the response body into a String
        BufferedSource bufferedSource = Okio.buffer(Okio.source(is));
        String responseBody = bufferedSource.readUtf8();
        System.out.println(responseBody);

        // Parse the response body as JSON
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        Moshi moshi = new Moshi.Builder().build();
        Object adapter = moshi.adapter(mapStringObject);
        try {
          // This line parses the JSON response into a Map<String, Object>
          Map<String, Object> body;
          body = ((JsonAdapter<Map<String, Object>>) adapter).fromJson(responseBody);
          assertNotNull(body);
          assertEquals("error_bad_request", body.get("result"));
          // Further processing with the parsed body
          System.out.println(body);
        } catch (IOException e) {
          // Handle the parsing exception
        }

        // Perform assertions

      }
    } finally {
      clientConnection.disconnect();
    }
  }
/*

    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", body.get("result"));
    // We'll use okio's Buffer class here
*/

    //assertEquals("Wrong entry! Enter full state name and county name", body.get("Error type"));

  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }


  }



