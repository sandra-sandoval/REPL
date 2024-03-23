package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import edu.brown.cs.student.main.Broadbanding.StateCountyCodes;
import edu.brown.cs.student.main.Handlers.BroadbandHandler;
import edu.brown.cs.student.main.Handlers.LoadCSVHandler;
import edu.brown.cs.student.main.Loading.FactoryFailureException;
import edu.brown.cs.student.main.Loading.StringCreator;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;
import com.squareup.moshi.JsonAdapter;
import java.util.Arrays;


public class BroadbandTest {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /**
   * Shared state for all tests.
   */

  @BeforeEach
  public void setup() {
    // Re-initialize state, etc. for _every_ test method run
    // In fact, restart the entire Spark server for every test!
    Spark.get("/broadband", new BroadbandHandler());
    Spark.init();
    Spark.awaitInitialization(); // don't continue until the server is listening
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("/broadband");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

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

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * Testing for no parameters/queries
   * @throws IOException
   */
  @Test
  // Recall that the "throws IOException" doesn't signify anything but acknowledgement to the type checker
  public void testAPINoParameters() throws IOException {
    HttpURLConnection clientConnection = tryRequest("broadband");
    // Get an OK response (the *connection* worked, the *API* provides an error response)
    assertEquals(200, clientConnection.getResponseCode());

    // Now we need to see whether we've got the expected Json response.
    // SoupAPIUtilities handles ingredient lists, but that's not what we've got here.
    Moshi moshi = new Moshi.Builder().build();
    // We'll use okio's Buffer class here
    BroadbandHandler.BroadbandRequestFailureResponse response =
        moshi.adapter(BroadbandHandler.BroadbandRequestFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));


    // ^ If that succeeds, we got the expected response. Notice that this is *NOT* an exception, but a real Json reply.

    clientConnection.disconnect();
  }

  /**
   * Testing for correct entry of state and county, and correct output
   * @throws Exception
   */
  @Test
  // Recall that the "throws IOException" doesn't signify anything but acknowledgement to the type checker
  public void testAPISuccessResponse() throws Exception {

    HttpURLConnection clientConnection = tryRequest("broadband?state=California&county=Ventura");
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
    List<String> row1 = new ArrayList<>(Arrays.asList("NAME", "S2802_C03_022E", "state", "county"));
    List<String> row2 = new ArrayList<>(
        Arrays.asList("Ventura County, California", "91.7", "06", "111"));
    toCheck.add(row1);
    toCheck.add(row2);
    assertEquals(toCheck, body.get("Data"));

    clientConnection.disconnect();
  }

  /**
   * Testing for incorrect state/county entry error message
   * @throws Exception
   */
  @Test
  public void testAPIRequestFailure() throws Exception {

    HttpURLConnection clientConnection = tryRequest("broadband?state=bruh&county=Ventura");
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", body.get("result"));
    // We'll use okio's Buffer class here


    assertEquals("Wrong entry! Enter full state name and county name", body.get("Error type"));

    clientConnection.disconnect();
  }

  /**
   * Testing for single missing parameter error message
   * @throws Exception
   */
  @Test
  public void testAPIRequestFailure2() throws Exception {

    HttpURLConnection clientConnection = tryRequest("broadband?state=California");
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_bad_request", body.get("result"));
    // We'll use okio's Buffer class here


    assertEquals("missing parameters", body.get("Error type"));

    clientConnection.disconnect();
  }

  /**
   * Unit tests for correct statecountycodes output, on various state and county combinations of
   * correct and incorrect entries
   * @throws Exception
   */
  @Test
  public void testUnitStateCounty() throws Exception {
    StateCountyCodes stateCountyCodes = new StateCountyCodes();
    assertEquals(53, stateCountyCodes.getStateCodes().size());
    List<String> codes = stateCountyCodes.getCodes("meh", "New York");
    Assert.assertTrue(codes.isEmpty());
    List<String> codes2 = stateCountyCodes.getCodes("Wyoming", "Gilette");
    assertEquals("56", codes2.get(0));
    List<String> codes3 = stateCountyCodes.getCodes("California", "San Fransisco");
    assertEquals("", codes3.get(1)); //testing for spelling mistake in county
    List<String> codes4 = stateCountyCodes.getCodes("California", "San Francisco");
    assertEquals("075", codes4.get(1));
  }

}
