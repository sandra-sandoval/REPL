import { ReactElement, useState, useEffect } from "react";
import "../styles/main.css";
import { REPLHistory } from "./REPLHistory";
import { REPLInput } from "./REPLInput";
import { Command } from "../command";
import SearchTable from "./searchTable";
import { get } from "./FunctionMap";
import { load_file } from "./LoadViewSearch";
import { view } from "./LoadViewSearch";
import { search } from "./LoadViewSearch";
import CSVTable from "./CSVTable";
import { REPLFunction } from "./REPLFunction";

/* 
  You'll want to expand this component (and others) for the sprints! Remember 
  that you can pass "props" as function arguments. If you need to handle state 
  at a higher level, just move up the hooks and pass the state/setter as a prop.
  
  This is a great top level component for the REPL. It's a good idea to have organize all components in a component folder.
  You don't need to do that for this gearup.
*/

export default function REPL() {
  // State to hold commands and their output
  const [commandHistory, setCommandHistory] = useState<Command[]>([]);
  const [mode, setMode] = useState<"brief" | "verbose">("brief");
  const [commandString, setCommandString] = useState<string>("");
  const [currentDataset, setCurrentDataset] = useState<
    (number | string)[][] | null
  >(null);
  //how to deal with case in which user types in Rhode Island.
  //Currently using only first arg - problem bc we need first and second argument in that case
  async function broadband(args: Array<string>): Promise<any> {
    console.log(args);
    console.log(args.length);
    if (args.length >= 2) {
      return fetch(
        "http://localhost:1318/broadband?state=" +
          args[0] +
          "&county=" +
          args[1]
      )
        .then((res) => res.json())
        .then((data) => {
          if (data["result"] === "success") {
            return <CSVTable data={data["Data"]} />;
          } else {
            return "Invalid request";
            // return data["error type"];
          }
        });
    }
    return "Invalid number of arguments";
  }
  /**
   * Function to handle submit of REPLInput command. Parses, cleans, and executes command. Also updates command history
   */
  const handleSubmit = async () => {
    const command = commandString.split(" ");
    if (commandString === "mode verbose") {
      setMode("verbose");
    } else if (commandString === "mode brief") {
      setMode("brief");
    }
    const replFunction = get(command[0]);

    if (replFunction) {
      //removes first element
      command.shift();
      const output = await replFunction(command);

      setCommandHistory((prevHistory) => [
        ...prevHistory,
        { command: commandString, output: output },
      ]);
    } else if (
      commandString != "mode verbose" &&
      commandString != "mode brief"
    ) {
      setCommandHistory((prevHistory) => [
        ...prevHistory,
        {
          command: commandString,
          output:
            "Invalid command. Please input load_file, view, search, or broadband.",
        },
      ]);
    }

    setCommandString("");
  };
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Enter") {
        handleSubmit();
      } else if (event.ctrlKey && event.key.toLowerCase() === "m") {
        setMode((prevMode) => (prevMode === "brief" ? "verbose" : "brief"));
      } else if (event.ctrlKey && event.key.toLowerCase() === "v") {
        view([]).then((res) =>
          setCommandHistory((prevHistory) => [
            ...prevHistory,
            { command: "view", output: res },
          ])
        );
      } else if (event.ctrlKey && event.key.toLowerCase() === "c") {
        setCommandHistory([]);
      } else if (event.key.toLowerCase() === "arrowdown") {
        window.scrollBy({ top: 100, behavior: "smooth" });
      } else if (event.key.toLowerCase() === "arrowup") {
        window.scrollBy({ top: -100, behavior: "smooth" });
      } else if (event.ctrlKey && event.key.toLowerCase() === "l") {
        setCommandString("load_file ");
      } else if (event.ctrlKey && event.key.toLowerCase() === "s") {
        setCommandString("search ");
      }
    };

    document.addEventListener("keydown", handleKeyDown);

    return () => {
      // Cleanup the event listener when the component is unmounted
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [handleSubmit, setMode]);

  return (
    <div className="repl">
      {/*This is where your REPLHistory might go... You also may choose to add it within your REPLInput 
      component or somewhere else depending on your component organization. What are the pros and cons of each? */}
      {/* Update your REPLHistory and REPLInput to take in new shared state as props */}
      <REPLHistory
        commandHistory={commandHistory}
        mode={mode}
        aria-label={"command history"}
      />

      <hr></hr>
      <REPLInput
        commandString={commandString}
        setCommandString={setCommandString}
        currentDataset={currentDataset}
        handleSubmit={handleSubmit}
      />
    </div>
  );
}
