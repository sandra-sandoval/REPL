package edu.brown.cs.student.main.Broadbanding;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which simply extracts state and county codes from a given state name and county
 */
public class StateCountyCodes implements CensusCodes{
  String API_KEY = "9186c312afefc86e1babeca43a5e49400868e5cc";
  private List<List<String>> stateCodes;
  private List<List<String>> countyCodes;

  /**
   * Constructor to initialize StateCountyCodes
   * @throws Exception
   */
  public StateCountyCodes() throws Exception{
    this.stateCodes = this.getAllCodes("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*&key=" + API_KEY);
  }

  /**
   * Gets a list of all codes using an API request for either states or counties in a state
   * @param regionUrl: URL for API request containing the list of codes
   * @return: List of list of Strings with each region's code
   * @throws Exception: any excpetions due to the api request, handled in BroadbandHandler
   */
  private List<List<String>> getAllCodes(String regionUrl) throws Exception{
    Moshi moshi = new Moshi.Builder().build();
    Type listStringObject = Types.newParameterizedType(List.class, Types.newParameterizedType(List.class, String.class));
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listStringObject);
    URL url = new URL(regionUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    //int responseCode = connection.getResponseCode();
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder jsonString = new StringBuilder();
    String line;
    while((line = reader.readLine()) != null){
      jsonString.append(line);
    }
    reader.close();
    List<List<String>> responseObject = adapter.fromJson(jsonString.toString());
    return responseObject;
  }

  /**
   * Finds a specific state or county in a list of list of strings with their corresponding census codes
   * @param allCodes: data to search for the state or county
   * @param value: name of state or county
   * @param index: index at which the code is stored in the list of codes
   * @return: code of the state or county
   */
  private String findStateCounty(List<List<String>> allCodes, String value, int index){
    for(List<String> code: allCodes){
      if(index == 1) {
        if (code.get(0).equalsIgnoreCase(value)) {
          return code.get(index);
        }
      }
      else if(index == 2){
        if (code.get(0).startsWith(value)){
          return code.get(index);
        }
      }
    }
    return "";
  }

  /**
   * Gets the state and county codes
   * @param state: State to find
   * @param county: County to find
   * @return: a list with [state_code, county_code]
   * @throws Exception: handled in BroadbandHandler
   */
  public List<String> getCodes(String state, String county) throws Exception{
    List<String> codes = new ArrayList<>();
    String stateCode = this.findStateCounty(this.stateCodes, state, 1);
    if(stateCode == ""){
      return codes;
    }
    String countyUrl = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode + "&key=" + API_KEY;
    this.countyCodes = this.getAllCodes(countyUrl);
    String countyCode = this.findStateCounty(this.countyCodes, county, 2);
    codes.add(stateCode);
    codes.add(countyCode);
    return codes;
  }

  public List<List<String>> getStateCodes(){
    return this.stateCodes;
  }



}
