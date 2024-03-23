import { ReactElement, useState } from "react";
import "../styles/main.css";
import CSVTable from "./CSVTable";
import { REPLFunction } from "./REPLFunction";
import { mockedLoad, mockedView, mockedSearch } from "../Mocked/mockedJson";

/**
 * load function that calls on the backend server to get the data receieved from
 * that call. Returns the output of the server to be displayed in history
 * @param args file to load & header boolean
 * @returns output message from server
 */

export async function load_file(args: Array<string>): Promise<string> {
  const fileName = args[0];
  const hasHeader = args[1];
  const fetchLoadCsv = await fetch(
    `http://localhost:1318/loadcsv?filepath=${fileName}&headers=${hasHeader}`
  );
  console.log(
    `http://localhost:1318/loadcsv?filepath=${fileName}&headers=${hasHeader}`
  );
  const loadJson = await fetchLoadCsv.json();

  console.log(`results: ${loadJson["result"]}`);
  if (loadJson["result"] === "success") {
    return `Loaded dataset from ${fileName}`;
  } else if (loadJson["result"] === "error_datasource") {
    return `${fileName} (No such file or directory)`;
  } else {
    return "Wrong/missing parameters, input valid arguments";
  }
}
/**
 * View function that calls on the backend server to get the retreieved data
 * and return it to display it in history
 * @param args none
 * @returns table with data or error message
 */
export async function view(
  args: Array<string>
): Promise<ReactElement | string> {
  if (args.length == 0) {
    return fetch("http://localhost:1318/viewcsv")
      .then((res) => res.json())
      .then((data) => {
        if (data["result"] === "success") {
          return <CSVTable data={data["Data"]} />;
        } else {
          return data["Error type"];
        }
      });
  }
  return "Invalid number of parameters";
}
/**
 * search function that calls on the backend server to get the search results
 * and return it to be displayed in history
 * @param args searchTerm, col_name
 * @returns search results in a table or an error message.
 */
export async function search(
  args: Array<string>
): Promise<ReactElement | string> {
  if (args.length > 1) {
    return fetch(
      `http://localhost:1318/searchcsv?search=${args[0]}&col_name=${args[1]}`
    )
      .then((res) => res.json())
      .then((data) => {
        if (data["result"] === "success") {
          return <CSVTable data={data["Found Items"]} />;
        } else {
          return data["Error type"];
        }
      });
  } else if (args.length == 0) {
    return "Wrong/Missing arguments";
  } else {
    return fetch(`http://localhost:1318/searchcsv?search=${args[0]}`)
      .then((res) => res.json())
      .then((data) => {
        if (data["result"] === "success") {
          return <CSVTable data={data["Found Items"]} />;
        } else {
          return data["Error type"];
        }
      });
  }
}
/**
 * Mocked function for load calling on mocked load data
 * @param args fileName and haHeaders
 * @returns output message
 */
export async function mockLoad(args: Array<string>): Promise<string> {
  console.log("mock args: " + args);
  return mockedLoad[args.join()];
}
/**
 * Mocked function for view calling on view mocked data
 * @param args none
 * @returns table with data
 */
export async function mockView(
  args: Array<string>
): Promise<ReactElement | string> {
  const data = mockedView.get("");
  if (data) {
    return <CSVTable data={data} />;
  } else return "CSV not loaded";
}
/**
 * Mocked function for search calling on mocked search data
 * @param args searchTerm and column
 * @returns table with data
 */
export async function mockSearch(
  args: Array<string>
): Promise<ReactElement | string> {
  const input = args.join();
  console.log("input is " + input);
  const data = mockedSearch.get(args.join());
  console.log("search data mock: " + data);
  if (data) {
    return <CSVTable data={data} />;
  } else return "No results found";
}
