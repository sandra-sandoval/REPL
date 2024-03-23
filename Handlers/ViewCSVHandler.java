package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.View;
import spark.Request;
import spark.Response;
import spark.Route;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.List;

/**
 * Handles viewing csvs, errors, and requests and responses needed
 */
public class ViewCSVHandler implements Route{
  private LoadCSVHandler load;

  /**
   * Constructor for viewCSV
   * @param l: The loaded csv. If not loaded an error message will be raised
   */
  public ViewCSVHandler(LoadCSVHandler l){
    this.load = l;
  }

  /**
   * Takes a loaded CSV and serializes into a json
   * @param request the request to handle
   * @param response use to modify properties of the response
   * @return response content
   * @throws Exception This is part of the interface; we don't have to throw anything.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception{
    if(!this.load.isLoaded()){
      return new ViewCSVLoadFailureResponse("CSV not loaded").serialize();
    }
    else{
      try{
        List<List<String>> data = this.load.getData();
        return new ViewCSVSuccessResponse(data).serialize();
      }
      catch(Exception e){
        return new ViewError(e.getMessage()).serialize();
      }
    }
  }

  /**
   * Response object for successful viewing of csv. Serializes the parsed data into json
   * @param response_type: success
   * @param data: data to be serialized into json
   */
  public record ViewCSVSuccessResponse(String response_type, List<List<String>> data) {
    public ViewCSVSuccessResponse(List<List<String>> data) {
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
   * Response object for failed viewing of csv. Serializes the response into json
   * @param error: response type
   * @param error_m: error message
   */
  public record ViewError(String error, String error_m) {
    public ViewError(String error_m) {
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
   * Response object for failed viewing of csv, as csv was not loaded. Serializes the response into json
   * @param error: response type
   * @param error_m: error message
   */
  public record ViewCSVLoadFailureResponse(String error, String error_m) {
    public ViewCSVLoadFailureResponse(String error_m) {
      this("error", error_m);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try{
        Map<String, Object> errorMap = new HashMap<>();
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        errorMap.put("result", "error_datasource");
        errorMap.put("Error type", error_m);
        return adapter.toJson(errorMap);

      } catch(Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }






}
