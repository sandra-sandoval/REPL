package edu.brown.cs.student;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Handlers.LoadCSVHandler;
import edu.brown.cs.student.main.Handlers.SearchCSVHandler;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Test the actual handlers.
 *
 * https://junit.org/junit5/docs/current/user-guide/
 *
 *
 * If the backend were "the system", we might call these system tests. But
 * I prefer "integration test" since, locally, we're testing how the Load/Search/View
 * functionality connects to the handler. The important thing is that these ARE NOT
 * the usual sort of unit tests.
 *
 */
public class SearchTest {

  @BeforeAll
  public static void setup_before_everything() {

    // Set the Spark port number. This can only be done once, and has to
    // happen before any route maps are added. Hence using @BeforeClass.
    // Setting port 0 will cause Spark to use an arbitrary available port.
    Spark.port(1319);   // OR USE: Spark.port(0);
    // Don't try to remember it. Spark won't actually give Spark.port() back
    // until route mapping has started. Just get the port number later. We're using
    // a random _free_ port to remove the chances that something is already using a
    // specific port on the system used for testing.

    // Remove the logging spam during tests
    //   This is surprisingly difficult. (Notes to self omitted to avoid complicating things.)

    // SLF4J doesn't let us change the logging level directly (which makes sense,
    //   given that different logging frameworks have different level labels etc.)
    // Changing the JDK *ROOT* logger's level (not global) will block messages
    //   (assuming using JDK, not Log4J)
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }


  /**
   * Shared state for all tests. We need to be able to mutate it (adding recipes etc.) but never need to replace
   * the reference itself. We clear this state out after every test runs.
   */

  //final Set<String> menu = new HashSet<>(); // maybe replace String with Object
  final Map<String, Object> responseMap = new HashMap<>();
  static LoadCSVHandler loadcsv = new LoadCSVHandler();
  @BeforeEach
  public void setup() throws Exception{
    // Re-initialize state, etc. for _every_ test method run
    responseMap.clear();
    loadcsv.load("ri_city_town_income_acs.csv", "y");
    // In fact, restart the entire Spark server for every test!
    Spark.get("/searchcsv", new SearchCSVHandler(loadcsv));
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  /**
   * Gracefully stop Spark listening on both endpoints
   */
  @AfterEach
  public void teardown() {
    Spark.unmap("/searchcsv");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }


  @Test
  public void searchUnitTest() throws Exception{

    LoadCSVHandler load1 = new LoadCSVHandler();
    load1.load("postsecondary_education.csv", "y");
    SearchCSVHandler search1 = new SearchCSVHandler(load1);
//
    LoadCSVHandler load2 = new LoadCSVHandler();
    load1.load("income_by_race_edited.csv", "y");
    SearchCSVHandler search2 = new SearchCSVHandler(load2);

//    List<List<String>> output1 = search1.search("Asian", "0");
//    Assert.assertEquals(output1.size(), 2);
//    List<List<String>> output2 = search1.search("Asian", "IPEDS Race");
//    Assert.assertEquals(output2.size(), 2);
    List<List<String>> output3 = search1.search("Asian", "University");
    Assert.assertEquals(output3.size(), 0);
////
//    // Testing for other entries which contain the word
//    List<List<String>> output6 = search2.search("bristol", "Geography");
//    Assert.assertEquals(output6.size(), 48);
//    List<List<String>> output7 = search2.search("random", "Geography");
//    Assert.assertEquals(output7.size(), 0);
//    List<List<String>> output8 = search2.search("bristol", "-1");
//    Assert.assertEquals(output8.size(), 48);
  }

  /**
   * Helper to start a connection to a specific API endpoint/params
   * @param apiCall the call string, including endpoint
   *                (NOTE: this would be better if it had more structure!)
   * @return the connection for the given URL, just after connecting
   * @throws IOException if the connection fails for some reason
   */
  static private HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    //clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }


  @Test
  public void testAPINoParameters() throws IOException {
    HttpURLConnection clientConnection = tryRequest("searchcsv");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    // Now we need to see whether we've got the expected Json response.
    // SoupAPIUtilities handles ingredient lists, but that's not what we've got here.
    Moshi moshi = new Moshi.Builder().build();
    // We'll use okio's Buffer class here
    SearchCSVHandler.SearchError response =
        moshi.adapter(SearchCSVHandler.SearchError.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.

    clientConnection.disconnect();
  }

  /**
   * Testing for correct entry of search term and colname, and correct output
   * @throws Exception
   */
  @Test
  // Recall that the "throws IOException" doesn't signify anything but acknowledgement to the type checker
  public void testAPISuccessResponse() throws Exception {


    HttpURLConnection clientConnection = tryRequest("searchcsv?search=Bristol&col_name=-1");
    assertEquals(200, clientConnection.getResponseCode());
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> body = adapter.fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("success", body.get("result"));
    // We'll use okio's Buffer class here

    // ^ If that succeeds, we got the expected response.
    assertNotNull(body);
    List<List<String>> toCheck = new ArrayList<>();
    List<String> row1 = new ArrayList<>(
        Arrays.asList("Bristol","80,727.00","115,740.00","42,658.00"));
    toCheck.add(row1);
    assertEquals(toCheck, body.get("Data"));

    clientConnection.disconnect();
  }

  /**
   * Testing for correct entry of search term and colname, and correct output
   * @throws Exception
   */
  @Test
  // Recall that the "throws IOException" doesn't signify anything but acknowledgement to the type checker
  public void testAPIRequestFailureResponse() throws Exception {


    HttpURLConnection clientConnection = tryRequest("searchcsv?search=Bristol");
    assertEquals(200, clientConnection.getResponseCode());
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> body = adapter.fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_datasource", body.get("result"));

    // ^ If that succeeds, we got the expected response.
    assertNotNull(body);

    clientConnection.disconnect();
  }

  /**
   * Checking if success response is given even when term is not found
   * @throws Exception
   */
  @Test
  public void testAPINotFoundResponse() throws Exception {


    HttpURLConnection clientConnection = tryRequest("searchcsv?search=bubble&col_name=-1");
    assertEquals(200, clientConnection.getResponseCode());
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> body = adapter.fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_datasource", body.get("result"));

    // ^ If that succeeds, we got the expected response.
    assertNotNull(body);
    assertEquals(null, body.get("data"));
    clientConnection.disconnect();
  }





}
