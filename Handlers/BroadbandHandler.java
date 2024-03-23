package edu.brown.cs.student.main.Handlers;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Broadbanding.StateCountyCodes;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Class that handles broadbrand and use of API
 */
public class BroadbandHandler implements Route {

  String state;
  String county;
  String apiUrl;
  String completeUrl;

  String API_KEY = "9186c312afefc86e1babeca43a5e49400868e5cc";

  /**
   * sets apiUrl to the US census API
   */
  public BroadbandHandler(){
    this.apiUrl = "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E";
  }

  /**
   * Makes API calls to the ACS database and fetches the broadband service data to display in our API
   * @param request the request to handle
   * @param response use to modify properties of the response
   * @return response content
   * @throws Exception This is part of the interface; we don't have to throw anything.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {

    try {
      Moshi moshi = new Moshi.Builder().build();
      Type listStringObject = Types.newParameterizedType(List.class, Types.newParameterizedType(List.class, String.class));
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listStringObject);


      this.state = request.queryParams("state");
      this.county = request.queryParams("county");
      if(this.state == null || this.county == null){
        return new BroadbandRequestFailureResponse("missing parameters").serialize();
      }
      StateCountyCodes stateCountyCodes = new StateCountyCodes();
      List<String> codes = stateCountyCodes.getCodes(this.state, this.county); //getting codes for state and county
      if(codes.isEmpty() || codes.get(0) == "" || codes.get(1) == ""){
        return new BroadbandRequestFailureResponse("Wrong entry! Enter full state name and county name").serialize();
      }
      this.completeUrl =
          apiUrl + "&for=county:" + codes.get(1) + "&in=state:" + codes.get(0) + "&key="
              + this.API_KEY;
      URL url = new URL(this.completeUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      int responseCode = connection.getResponseCode();
      System.out.println("Response Code: " + responseCode);

      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(connection.getInputStream()));
        StringBuilder jsonString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          jsonString.append(line);
        }
        reader.close();
        List<List<String>> responseObject = adapter.fromJson(jsonString.toString());
        System.out.println(responseObject);
        connection.disconnect();
        return new BroadbandSuccessResponse(responseObject).serialize();
      } else {
        return new BroadbandHTTPFailureResponse("HTTP Failure").serialize();
      }
    }
    catch(Exception e){
      return new BroadbandFailureResponse(e.getMessage()).serialize();
    }

    // Prepare to send a reply

    /*Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("type", "success");
    //responseMap.put("Results:", reader);



    // Generate the reply
    try {
      double state_double = Double.parseDouble(this.state);
      double county_double = Double.parseDouble(this.county);
      //Geolocation loc = new Geolocation(lat_double, lon_double);
      // Low-level NWS API invocation isn't the job of this class!
      // Neither is caching!
      // Building responses is the job of this class:


      // Decision point; note the difference here

      return adapter.toJson(responseMap);
    } catch (Exception e) {
      responseMap.put("type", "error");
      responseMap.put("error_type", "datasource");
      responseMap.put("details", e.getMessage());
      return adapter.toJson(responseMap);*/
  }

  /**
   * Response object for successful return of API call. Serializes the found data into json
   * @param response_type: success in this case
   * @param data: found data with search items
   */
  public record BroadbandSuccessResponse(String response_type, List<List<String>> data) {
    public BroadbandSuccessResponse(List<List<String>> data) {
      this("success", data);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        Map<String, Object> responseMap = new HashMap<>();
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        responseMap.put("result", "success");
        responseMap.put("Data", data);
        return adapter.toJson(responseMap);
      } catch(Exception e) {

        e.printStackTrace();
        throw e;
      }
    }
  }



  /**
   * Response object for a bad request made by user. Serializes the response into json
   * @param error: response type
   * @param error_m: error message
   */
  public record BroadbandRequestFailureResponse(String error, String error_m) {
    public BroadbandRequestFailureResponse(String error_m) {
      this("error", error_m);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {

        Map<String, Object> errorMap = new HashMap<>();
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        errorMap.put("result", "error_bad_request");
        errorMap.put("Error type", error_m);
        return adapter.toJson(errorMap);
      } catch(Exception e) {

        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * Response for other broadband failures
   * @param response_type: success as csv searched successfully
   * @param message: message to send
   */
  public record BroadbandFailureResponse(String response_type, String message) {
    public BroadbandFailureResponse(String message) {
      this("error", message);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {

        Map<String, Object> errorMap = new HashMap<>();
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        errorMap.put("result", "error_bad_json");
        errorMap.put("error type:", message);
        return adapter.toJson(errorMap);
      } catch(Exception e) {

        e.printStackTrace();
        throw e;
      }
    }
  }
  /**
   * Response for ACS HTTP failure
   * @param response_type: success as csv searched successfully
   * @param message: message to send
   */
  public record BroadbandHTTPFailureResponse(String response_type, String message) {
    public BroadbandHTTPFailureResponse(String message) {
      this("error", message);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {

        Map<String, Object> errorMap = new HashMap<>();
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        errorMap.put("result", "error_datasource");
        errorMap.put("error type:", message);
        return adapter.toJson(errorMap);
      } catch(Exception e) {

        e.printStackTrace();
        throw e;
      }
    }
  }


}

