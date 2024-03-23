import "../styles/main.css";
import { ControlledInput } from "./ControlledInput";

interface REPLInputProps {
  commandString: string;
  setCommandString: React.Dispatch<React.SetStateAction<string>>;
  currentDataset: (number | string)[][] | null;
  handleSubmit: () => void;
}
/**
 * Component to display input box and submit button, and pass command to REPL
 * @param props commandString, setCommandString, currentDataset, and handleSubmit function from REPL
 * @returns
 */
export function REPLInput(props: REPLInputProps) {
  return (
    <div className="repl-input">
      <fieldset>
        <legend>Enter a command:</legend>
        <ControlledInput
          value={props.commandString}
          setValue={props.setCommandString}
          ariaLabel={"Command input"}
          aria-describedby={"command-input-description"}
        />
        <div id="">
          {/* Input command here: load_file, search, view, or broadband. Then press
          submit button */}
          Input command here: load_file, search, view or broadband!
        </div>
      </fieldset>
      <button
        onClick={props.handleSubmit}
        aria-label={"Submit Button"}
        // aria-describedby={"submit-button-description"}
      >
        Submit
        {/* <div id="submit-button-description">Submit</div> */}
      </button>
    </div>
  );
}
