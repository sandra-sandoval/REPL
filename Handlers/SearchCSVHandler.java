package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Handlers.LoadCSVHandler.LoadCSVParameterFailureResponse;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
/**
 * Handles searching for data in csvs, errors, and requests and responses needed
 */
public class SearchCSVHandler implements Route {

  //Parser parsing;
  LoadCSVHandler loading;
  String file;
  String head;
  List<List<String>> data;
  List<String> headers;

  /**
   * Constructor to instantiate Searcher
   * @param l: LoadCSVHandler object for the parsed/loaded csv
   */
  public SearchCSVHandler(LoadCSVHandler l) {
    this.loading = l;
  }

  /**
   * Function that conducts the search over the CSV of the specific String
   *
   * @param toSearch: term to be searched
   * @param colName: column name where the term should be found, "-1" if no column is specified
   * @return List of rows which contain the specified term
   */
  public List<List<String>> search(String toSearch, String colName) throws IllegalArgumentException {
    List<List<String>> foundItems = new ArrayList<List<String>>();
    System.out.println(this.headers);
    System.out.println(colName);


    try {
      if (colName.equals("-1")){
        throw new NumberFormatException(); // to trigger the catch if "-1" to go through all rows
      }
      int colIndex = Integer.parseInt(colName); //  will trigger the catch if colName is words
      if (this.headers.size() == 0) {
        //System.out.println("No columns in data, wrong specification.");
      } else {
        if (colIndex < this.headers.size()) {
          for (List<String> row : this.data) {
            String item = row.get(colIndex);
            if (item.equalsIgnoreCase(toSearch)) {
              foundItems.add(row);
            }
          }
        } else {
          //System.out.println("Could not find column: " + colName);
          throw new IllegalArgumentException(); // column to search exceeds columns available
        }
      }

    } catch (NumberFormatException e) {

      if (colName.equals("-1")) {
        //System.out.println("HIIII");
        for (List<String> row : this.data) {
          //System.out.println("hello");
          for (String item : row) {
            if (item.toLowerCase().contains(toSearch.toLowerCase())) {
              foundItems.add(row);
              break;
            }
          }
        }
      } else {
        if (this.headers.size() == 0) {
          //System.out.println("No columns in data, wrong specification.");
        } else {
          int index = this.headers.indexOf(colName);
          if (index != -1) {
            for (List<String> row : this.data) {
              String item = row.get(index);
              if (item.toLowerCase().contains(toSearch.toLowerCase())) {
                foundItems.add(row);
              }
            }
          } else {
            //System.out.println("Could not find column: " + colName);
            throw new IllegalArgumentException();
          }
        }
      }


    }


    return foundItems;
  }

  /**
   * Takes a loaded CSV and accepts a search term, also maybe a column, to search for rows with
   * given term
   * @param request the request to handle
   * @param response use to modify properties of the response
   * @return response content
   * @throws Exception This is part of the interface; we don't have to throw anything.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    if(!this.loading.isLoaded()){
      return new SearchCSVLoadFailureResponse("CSV not loaded").serialize();
    }
    else{
      this.headers = this.loading.getHeaders();
      this.data = this.loading.getData();
      String toSearch = request.queryParams("search");
      String column = request.queryParams("col_name");
      if(toSearch == null){
        return new SearchError("Wrong/missing parameters").serialize();
      }
      if(column == null){
        column = "-1";
      }
      try{
        List<List<String>> foundItems = this.search(toSearch, column);
        if(foundItems.isEmpty()){
          return new SearchNotFoundSuccess("Sorry! Term not found in csv").serialize();
        }
        return new SearchCSVSuccessResponse(foundItems).serialize();
      }
      catch(Exception e){
        return new SearchError(e.getMessage()).serialize();
      }
    }
  }

  /**
   * Response object for successful searching of csv. Serializes the found data into json
   * @param response_type: success in this case
   * @param data: found data with search items
   */
  public record SearchCSVSuccessResponse(String response_type, List<List<String>> data) {
    public SearchCSVSuccessResponse(List<List<String>> data) {
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
        responseMap.put("Found Items", data);
        return adapter.toJson(responseMap);
      } catch(Exception e) {

        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * Response object for failed loading of csv. Serializes response into json
   * @param error: response type
   * @param error_m: error message (csv not loaded)
   */
  public record SearchCSVLoadFailureResponse(String error, String error_m) {
    public SearchCSVLoadFailureResponse(String error_m) {
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

  /**
   * Response object for failed searching of csv. Serializes the response into json
   * @param error: response type
   * @param error_m: error message
   */
  public record SearchError(String error, String error_m) {
    public SearchError(String error_m) {
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
   * Response for search term not found
   * @param response_type: success as csv searched successfully
   * @param message: message to send
   */
  public record SearchNotFoundSuccess(String response_type, String message) {
    public SearchNotFoundSuccess(String message) {
      this("success", message);
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
        errorMap.put("result", "success");
        errorMap.put("Data", message);
        return adapter.toJson(errorMap);
      } catch(Exception e) {

        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * Getter function for headers
   *
   * @return: List of column names of the data
   */
  public List<String> getHeaders() {
    return this.headers;
  }

  /**
   * Getter function for the data
   *
   * @return CSV data
   */
  public List<List<String>> getData() {
    return this.data;
  }



}