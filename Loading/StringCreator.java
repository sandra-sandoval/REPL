package edu.brown.cs.student.main.Loading;

import java.util.ArrayList;
import java.util.List;

public class StringCreator implements CreatorFromRow<List<String>> {

  /** Constructor for StringCreator */
  public StringCreator() {}

  /**
   * Returns the list of strings which constitute a row, also parses it for searching
   *
   * @param row: Row to be converted
   * @return: The refined row of strings
   * @throws FactoryFailureException
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    if (row.isEmpty()) {
      throw new FactoryFailureException("No data in row", row);
    }
    List<String> newRow = new ArrayList<String>();
    for (String item : row) {
      item = item.replaceAll("\"", "").trim();
      newRow.add(item);
    }
    return newRow;
  }
}
