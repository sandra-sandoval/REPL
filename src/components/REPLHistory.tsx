import "../styles/main.css";
import { Command } from "../command";

interface REPLHistoryProps {
  commandHistory: Command[];
  mode: "brief" | "verbose";
}
/**
 * Component to display command history in box
 * @param props commandHistory and mode state from REPL
 * @returns
 */
export function REPLHistory(props: REPLHistoryProps) {
  const { commandHistory, mode } = props;
  return (
    <div className="repl-history" id="repl-history">
      {/* This is where command history will go */}
      {commandHistory.map((entry, index) => (
        <div key={index}>
          {mode === "verbose" && <div>Command: {entry.command}</div>}
          <div>Output: {mode === "brief" ? entry.output : entry.output}</div>
        </div>
      ))}
    </div>
  );
}
