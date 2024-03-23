/**
 * Command interface for history of REPL commands and outputs to be displayed in REPLHistory.
 */
export interface Command {
  command: string;
  output: string | React.ReactNode;
}
