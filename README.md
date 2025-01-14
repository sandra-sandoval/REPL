# repl

### Project details

Team members and contributions : Sandra Sandoval (spsandov) & Jason Uranta (juranta)

Estimated time it took to complete project: 20

Link to repo: https://github.com/cs0320-f23/repl-juranta-spsandov.git

### Design choices

**Explain the relationships between classes/interfaces.**
**_REPL_** contains the handleSubmit method which calls on the functionMap (by keying into it by the user input)
that is located inside the REPLFunction file. The handlesubmit method also checks for the user inputs of mode
to switch the mode accordingly. Contains a useEffect block that is responsible for registering keyboard presses
to their designated functionalities: M (mode), V (view), C (clear) , Enter, Up, Down

**_REPLInput_** - Displays the command box and submit button and passes the command to the REPL

**_REPLHistory_** - Displays the command history into the history box to be viewed by the user

**_FunctionMap_** - Calls on the LoadViewSearch file that contains the various functions pertaining to csv's and the
Broadband file containing the broadband method. It adds these function to a dictionary mapping a string to the function
that is of type REPLFunction. Contains various wrapper methods such as addMap, removeMap, getSize, and contains. Used
so the user is able to register new commands and to test the map. It also includes mocked functions to allow for the usage of mock data rather than the backend server.

**_REPLFunction _** - interface that allows methods to take in an array of strings as an argument and return a promise of a
promise string, string, or react element.

**_LoadViewSearch_** - contains load_file, view, and search methods that call on the backend server to retrieve data from these
inputs It also includes mock versions of these methods that call on the mocked data.

**_Broadband_** - contains broadband call to the backend server that calls on the census api. Retrieves data or message that is
returned to be displayed It also includes a mock version of this method and calls on the mocked broadband data.

**_CSVTable_** - contains an html table that is used to display data receieved from the server in a formatted way.

### Errors/Bugs.

- Our search has a bug where when someone types in an invalid request (invalid term) and does not provide a second argument (y or n), the site crashes. Example: search invalid would crash the site.

However, it works fine when searching for invalid term and invalid column. Other than that, we have no known bugs.

### Tests

We test in both playwright and jtest. The majority of our tests are done through playwright as it checks visual outputs from our code. In the App.spec.ts testing file, we tested that the commands available to the user are accurately being displayed on screen. For jtesting, we had to create an example function of type REPLFunction to ensure that our wrapper methods were functioning properly and that the user would be able to register a new command.

- Load
  - load a valid file : the successful output message is visible
  - load an invalid file: the failure output message is visible
- View
  - view before a file has been loaded : failure message is visible telling user that a csv file has not been loaded yet.
  - successful view: table with content is visible on screen
- Search
  - search with column name - table with term in that column is visible
  - search with column index - table with term in that column index is visible
  - search with invalid arguments - returns a failure message telling user their arguments are invalid
  - search without columnID - table with all rows containing term is visible
  - search before load - failure message is visible tellibg user csv file has not been loaded yet
- BroadBand - broadband request with a valid state and county - data in a table is visible - broadband with no arguments - error message is visible on screen - brooadband with invalid arguments - error message telling user it is an invalid request
  Also tested interactions between all commands - load, view, load, view - load,view,mode,view : text element "command: " is visible - load, search, load, search
  Tested keyboard shortcuts - ctrl M - history changes - Enter - inputs are submitted - ctrl C - history has no data visible - ctrl V - data table is visible if csv is loaded, else error messafe

Tested with mocks to reduce need to make api calls to our backend server.

- mocked valid and invalid response for load
- mocked view output
- mocked valid and invalid data for search with column index, name, & no header
- mocked valid and invalid broadband requests
  To test with mocks, we created seperate fucntions that dealt with mocks. We added these to our function map to reduce the need to call from different maps. User can type in mockLoad, mockView, mockBroadband to utilize program with mocks rather than the bakcked server,

Tested functionMap utilities with jest:

- FunctionMap file contains a map that maps a string to a function of type REPLFunction. It contains
  wrapper methods such as get(), contains(), getSize(), add(), remove(). These are exported functions which allow us to keep our functionMap private and safe. We use these methods in testing and in REPL where we call from the map.
- getSize(): ensure that the initial size is 8 : containing broadband, load, view, search, mockBroadband, mockLoad, mockView, mockSearch
- contain() : ensure that it is accurately returning boolean indicating whether a value is in the dictionary.
- add() : We created an exampleFunction which simulated a user registering a new command. We ensured that after the add method was called, the size incremeneted and the contains method returned true.
- remove() : After simulating a new command being registered and testing its size, we called the remove method to ensure that methods could also be removed. We checked that the size had decremeneted and contains no longer returned true.

### How to…

**_Run tests_**:
You may run the tests by going into the terminal and typing npx playwright test or npx playwright test --ui which will give you the results for the tests.

**_Build and run your program_**:
First run the backend server. Then you can type in "npm start" into the terminal to start the front end program. Once you input this command
a link will be returned in the terminal. This link should be entered into a web browser.

Once loaded, you will have access to the program.The program takes in four commands: load_file, view, search, and broadband. However, you may also input "mode verbose" or "mode brief" to
change how results are displayed.

Alternatively, you may press on ctrl m to switch modes. The load_file command takes in two arguments : a file name y or n indicating the presence of headers.

The search command takes in two arguments: the search term and the column to search in. However, you may only input a searchTerm if you wish.

The view command does not take any commands. In addition to calling search by inputting the command into the
command box, you may press ctrl v to call view.

The broadband command takes in two arguments: a state and a county name.

To submit an input into the command box, you may click the submit button or press "Enter" key.

To clear the history, you may press ctrl c.

To scroll the page, you may press up or down arrow.
