package edu.brown.cs.student.main.Handlers;
import edu.brown.cs.student.main.Loading.FactoryFailureException;
import edu.brown.cs.student.main.Loading.StringCreator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import spark.Request;
import spark.Response;
import spark.Route;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.Map;
import java.lang.reflect.Type;
import com.squareup.moshi.Types;

/**
 * Handles loading/parsing for csvs, errors, and requests and responses needed
 */
public class LoadCSVHandler implements Route {
  private BufferedReader bfr;
  //CreatorFromRow<T> creator;
  static final Pattern regexSplitCSVRow = Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  private List<String> headers;
  private StringCreator sc;
  private boolean loaded;
  private List<List<String>> data;



  /**
   * Constructor for LoadCSVHandler, initializes most of the class variables
   */
  public LoadCSVHandler(){ // took out: CreatorFromRow<T> creator
    this.loaded = false;
    this.sc = new StringCreator();
    this.headers = new ArrayList<String>();
    this.data = new ArrayList<>();
  }

  /**
   * Function which parses the csv file line by line, splits cells from each row, and then calls the
   * create function to convert row into the passed type
   *
   * @return Final list of objects of type T
   * @throws IOException
   * @throws FactoryFailureException
   */
  public void load(String filename, String head) throws IOException, FactoryFailureException {


      FileReader fr = new FileReader("data/" + filename);
      this.bfr = new BufferedReader(fr);
      String line = this.bfr.readLine();
      String[] header;
      List<List<String>> rows = new ArrayList<List<String>>();
      if (head.equalsIgnoreCase("Y")) {
        header = regexSplitCSVRow.split(line);
        this.headers = Arrays.asList(header);
        rows.add(this.headers);
        line = bfr.readLine();
      }

      List<String> row;
      List<String> item;
      while (line != null) {
        String[] temp_row = regexSplitCSVRow.split(line);
        row = Arrays.asList(temp_row);
        item = this.sc.create(row);
        rows.add(item);
        row = null;
        line = this.bfr.readLine();
      }
      this.bfr.close();
      fr.close();
      this.data = rows;

  }

  /**
   * Stores the filename and makes a call to load() to parse the requested csv file. Results in an
   * error message if said file is not found
   * @param request the request to handle
   * @param response use to modify properties of the response
   * @return response content
   * @throws Exception This is part of the interface; we don't have to throw anything.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filename = request.queryParams("filepath");
    String head = request.queryParams("headers");
    if(filename == null || head == null){
      return new LoadCSVParameterFailureResponse("Wrong/missing parameters").serialize();
    }

    try {
      this.load(filename, head);
      this.loaded = true;
    } catch(Exception e){
      return new LoadCSVNoFileFailureResponse(e.getMessage()).serialize();
    }
    finally {
      this.bfr.close();
    }
    return new LoadCSVSuccessResponse(filename).serialize();
  }

  /**
   * Response object to send if parsing was successful
   * @param response_type: success
   * @param file: the file successfully parsed
   */
  public record LoadCSVSuccessResponse(String response_type, String file) {
    public LoadCSVSuccessResponse(String file) {
      this("success", file);
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
        responseMap.put("File", file);
        return adapter.toJson(responseMap);
      } catch(Exception e) {

        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * Response object to send if parsing was unsuccessful, mostly due to the file not being found
   * or there being a datasource error
   * @param error: error
   * @param error_m: name of file with error
   */
  public record LoadCSVNoFileFailureResponse(String error, String error_m) {
    public LoadCSVNoFileFailureResponse(String error_m) {
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
   * Response object to send if parsing was unsuccessful, mostly due to the file not being found
   * or there being a datasource error
   * @param error: error
   * @param error_m: name of file with error
   */

  public record LoadCSVParameterFailureResponse(String error, String error_m) {

    public LoadCSVParameterFailureResponse(String error_m) {
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
   * Getter function for Headers
   *
   * @return: List of headers of the csv
   */
  public List<String> getHeaders() {
    return this.headers;
  }

  /**
   * Getter function for CreatorFromRow object
   *
   * @return: CreatorFromRow object
   */
  /*
  public CreatorFromRow<T> getCreator() {
    return this.creator;
  }
  */

  /**
   * Checks if the csv was initially loaded or not before being viewed or searched
   * @return: boolean, true if parsed false if not
   */
  public boolean isLoaded(){
    return this.loaded;
  }

  /**
   * Getter function for parsed data
   * @return: data parsed from csv
   */
  public List<List<String>> getData(){
    return this.data;
  }



}
