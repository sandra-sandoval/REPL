package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Handlers.LoadCSVHandler;
import edu.brown.cs.student.main.Handlers.SearchCSVHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;
import spark.Spark;

public class LoadErrorTest {

  @BeforeAll
  public static void setup_before_everything() {

    // Set the Spark port number. This can only be done once, and has to
    // happen before any route maps are added. Hence using @BeforeClass.
    // Setting port 0 will cause Spark to use an arbitrary available port.
    Spark.port(1310);   // OR USE: Spark.port(0);
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
    //loadcsv.load("ri_city_town_income_acs.csv", "y");
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



  /**
   * Testing for searchcsv error response for unloaded data
   * @throws Exception
   */
  @Test
  // Recall that the "throws IOException" doesn't signify anything but acknowledgement to the type checker
  public void testLoadFailureResponse() throws Exception {


    HttpURLConnection clientConnection = tryRequest("searchcsv?search=Bristol&col_name=-1");
    assertEquals(200, clientConnection.getResponseCode());
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> body = adapter.fromJson(
        new Buffer().readFrom(clientConnection.getInputStream()));
    assertEquals("error_datasource", body.get("result"));
    // We'll use okio's Buffer class here

    // ^ If that succeeds, we got the expected response.

    clientConnection.disconnect();
  }

}
