import "../styles/App.css";
import REPL from "./REPL";

/**
 * This is the highest level component!
 */
function App() {
  return (
    <div className="App">
      <p
        className="App-header"
        aria-label={"REPL program"}
        aria-describedby={"REPl-description"}
      >
        
        <h1>REPL</h1>
      </p>
      <REPL />
      <div id="REPL-description">
        REPL Program that allows you to load, search, and view csv file
      </div>
    </div>
  );
}

export default App;
