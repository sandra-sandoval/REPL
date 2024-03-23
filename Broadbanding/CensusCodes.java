package edu.brown.cs.student.main.Broadbanding;
import java.util.List;

/**
 * Interface for processing codes in census and decoding the codes
 */
public interface CensusCodes {

  /**
   * Gets codes specific to census
   * @param s: One value to find code for
   * @param c: Another value to find code for
   * @return: a list with [s_code, c_code]
   * @throws Exception: handled in BroadbandHandler
   */
  public List<String> getCodes(String s, String c) throws Exception;

}
