import CSVTable from "./CSVTable";
import { mockedBroadband } from "../Mocked/mockedJson";
import { ReactElement } from "react";

/**
 * Broadband function that calls on the back server's broadband and
 * returns the output to be displayed in the history
 * @param args state and county inputted by the user
 * @returns the broadband data retrieved from the backend server
 */

//how to deal with case in which user types in Rhode Island.
//Currently using only first arg - problem bc we need first and second argument in that case
export async function broadband(args: Array<string>): Promise<any> {
  console.log(args);
  console.log(args.length);
  if (args.length >= 2) {
    return fetch(
      "http://localhost:1318/broadband?state=" + args[0] + "&county=" + args[1]
    )
      .then((res) => res.json())
      .then((data) => {
        if (data["result"] === "success") {
          return <CSVTable data={data["Data"]} />;
        } else {
          return "Invalid request";
        }
      });
  }
  return "Invalid number of arguments";
}

export async function mockBroadband(
  args: Array<string>
): Promise<ReactElement | string> {
  const input = args.join();
  console.log("input is " + input);
  const data = mockedBroadband.get(args.join());
  console.log("broadband data mock: " + data);
  if (data) {
    return <CSVTable data={data} />;
  } else return "Invalid request";
}
