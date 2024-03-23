import { test, expect } from "@playwright/test";
/**
  The general shapes of tests in Playwright Test are:
    1. Navigate to a URL
    2. Interact with the page
    3. Assert something about the page against your expectations
  Look for this pattern in the tests below!
 */
let initialCommands: (string | null)[] = [];
let initialOutputs: (string | null)[] = [];
// If you needed to do something before every test case...
test.beforeEach(async ({ page }) => {
  // ... you'd put it here.
  await page.goto("http://localhost:8000/");
  initialCommands = await page.$$eval(
    "#repl-history > div > div:first-child",
    (elements) => elements.map((e) => e.textContent)
  );
  initialOutputs = await page.$$eval(
    "#repl-history > div > div:nth-child(2)",
    (elements) => elements.map((e) => e.textContent)
  );
});

/**
 * Don't worry about the "async" yet. We'll cover it in more detail
 * for the next sprint. For now, just think about "await" as something
 * you put before parts of your test that might take time to run,
 * like any interaction with the page.
 */
test("on page load, i see an input bar", async ({ page }) => {
  // Notice: http, not https! Our front-end is not set up for HTTPs.
  await expect(page.getByLabel("Command input")).toBeVisible();
});

test("after I type into the input box, its text changes", async ({ page }) => {
  // Step 1: Navigate to a URL
  // await page.goto('http://localhost:8000/');

  // Step 2: Interact with the page
  // Locate the element you are looking for
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("Awesome command");

  // Step 3: Assert something about the page
  // Assertions are done by using the expect() function
  const mock_input = `Awesome command`;
  await expect(page.getByLabel("Command input")).toHaveValue(mock_input);
});

test("on page load, i see a button", async ({ page }) => {
  await expect(page.locator("button")).toBeVisible();
});

test("after I click the button, my output gets pushed to REPL History", async ({
  page,
}) => {
  // Assuming 'commandList' is the id of the div where commands are listed
  // const initialOutputs = await page.$$eval("#repl-history > div", elements =>
  //   elements.map(e => e.textContent)
  // );

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("New command");

  // Click the button to push the command
  await page.locator("button").click();

  const updatedOutputs = await page.$$eval("#repl-history > div", (elements) =>
    elements.map((e) => e.textContent)
  );
  expect(updatedOutputs.length).toBe(initialOutputs.length + 1);
});

test("After I set verbose mode and click the button, my output and command get pushed onto REPL history", async ({
  page,
}) => {
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode verbose");

  // Click the button to push the command
  await page.locator("button").click();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("New command");

  // Click the button to push the command
  await page.locator("button").click();

  const updatedCommands = await page.$$eval(
    "#repl-history > div > div:first-child",
    (elements) => elements.map((e) => e.textContent)
  );
  const updatedOutputs = await page.$$eval(
    "#repl-history > div > div:nth-child(2)",
    (elements) => elements.map((e) => e.textContent)
  );
  expect(updatedCommands[updatedCommands.length - 1]).toBe(
    "Command: New command"
  );
  expect(updatedCommands.length).toBe(initialCommands.length + 1);

  expect(updatedOutputs.length).toBe(initialOutputs.length + 1);
  expect(updatedOutputs[updatedOutputs.length - 1]).toBe(
    "Output: Invalid command. Please input load_file, view, search, or broadband."
  );
});

test("after I load a file, its contents are displayed in REPL history", async ({
  page,
}) => {
  // Interact with the Command input and load the dogs.csv file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file dol_ri_earnings_disparity.csv y");
  // Click the button to push the command
  await page.locator("button").click();
  await expect(
    page.getByText("Output: Loaded dataset from dol_ri_earnings_disparity.csv")
  ).toBeVisible();
});

test("viewing without file load should result in error", async ({ page }) => {
  // Interact with the Command input and enter the 'view' command
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");

  // Click the button to push the command
  await page.locator("button").click();
  await expect(page.getByText("Output: CSV not Loaded")).toBeVisible();
});

test("after I load a file, I can search its contents", async ({ page }) => {
  // Load file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file dol_ri_earnings_disparity.csv y");
  await page.locator("button").click();
  await page.getByLabel("Command input").click();

  await page.getByLabel("Command input").fill(`search Black Data%20Type`);
  await page.locator("button").click();
  const updatedTableOutputs = await page.getByRole("table");
  console.log("table output: " + updatedTableOutputs);
  expect(updatedTableOutputs).toHaveText("RIBlack$770.2630424.80376$0.736%");

  await expect(
    page.getByText("Output: RIBlack$770.2630424.80376$0.736%")
  ).toBeVisible();
});
test("searching with column index", async ({ page }) => {
  // Load file
  await page.goto("http://localhost:8000/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file dol_ri_earnings_disparity.csv y");
  await page.locator("button").click();
  await expect(
    page.getByText("Output: Loaded dataset from dol_ri_earnings_disparity.csv")
  ).toBeVisible();
  await page.getByLabel("Command input").fill(`search Black 1`);
  await page.locator("button").click();
  await expect(
    page
      .locator("table")
      .filter({ hasText: /^RIBlack\$770\.2630424\.80376\$0\.736%$/ })
  ).toBeVisible();
});

test("searching without columnID", async ({ page }) => {
  // Load file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file dol_ri_earnings_disparity.csv y");
  await page.locator("button").click();
  await expect(
    page.getByText("Output: Loaded dataset from dol_ri_earnings_disparity.csv")
  ).toBeVisible();
  await page.getByLabel("Command input").fill(`search Black`);
  await page.locator("button").click();
  await expect(
    page
      .locator("table")
      .filter({ hasText: /^RIBlack\$770\.2630424\.80376\$0\.736%$/ })
  ).toBeVisible();
});

test("searching invalid arguments", async ({ page }) => {
  // Load file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file dol_ri_earnings_disparity.csv y");
  await page.locator("button").click();
  await expect(
    page.getByText("Output: Loaded dataset from dol_ri_earnings_disparity.csv")
  ).toBeVisible();
  await page.getByLabel("Command input").fill(`search invalid invalid`);
  await page.locator("button").click();
  await expect(page.getByText("Output:", { exact: true })).toBeVisible();
});
test("searching invalid number of arguments", async ({ page }) => {
  // Load file
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file dol_ri_earnings_disparity.csv y");
  await page.locator("button").click();
  await expect(
    page.getByText("Output: Loaded dataset from dol_ri_earnings_disparity.csv")
  ).toBeVisible();
  await page.getByLabel("Command input").fill(`search`);
  await page.locator("button").click();
  await expect(page.getByText("Output: Wrong/Missing arguments")).toBeVisible();
});

test("after I change mode to brief, command is not dol_ri_earnings_disparity.csv", async ({
  page,
}) => {
  // Interact with the Command input and load the file

  await page.goto("http://localhost:8000/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file dol_ri_earnings_disparity.csv");
  await page.locator("button").click();

  await expect(
    page.getByText("Command: load_file dol_ri_earnings_disparity.csv y")
  ).toBeHidden();
  await expect(
    page.getByText("Output: Loaded dataset from dol_ri_earnings_disparity.csv")
  ).toBeVisible();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode verbose");
  await page.locator("button").click();

  await expect(
    page.getByText("Command: load_file dol_ri_earnings_disparity.csv")
  ).toBeVisible();
  await expect(
    page.getByText("Output: Loaded dataset from dol_ri_earnings_disparity.csv")
  ).toBeVisible();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("view");
  await page.locator("button").click();

  await expect(page.getByText("Command: view")).toBeVisible();

  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("mode brief");
  await page.locator("button").click();
  await expect(page.getByText("Command: view")).toBeHidden();
});

// test that application starts off in brief mode, and command: does not appear in REPL history
test("application starts off in brief mode", async ({ page }) => {
  // check that command and output are displayed in REPL history
  await page.goto("http://localhost:8000/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("load_file dol_ri_earnings_disparity.csv");
  await page.locator("button").click();

  await expect(
    page.getByText("Command: load_file dol_ri_earnings_disparity.csv y")
  ).toBeHidden();
});

test("when I load an invalid file, history outputs error message", async ({
  page,
}) => {
  await page.goto("http://localhost:8000/");
  await page.getByPlaceholder("Enter command here!").click();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("load_file invalid y");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Output: invalid (No such file or directory)")
  ).toBeVisible();
});

test("when I load a valid file,then view, then load another file without header, history gets updated", async ({
  page,
}) => {
  await page.goto("http://localhost:8000/");
  await page.getByPlaceholder("Enter command here!").click();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("load_file postsecondary_education.csv y");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Output: Loaded dataset from postsecondary_education.csv")
  ).toBeVisible();

  await page.getByPlaceholder("Enter command here!").click();
  await page.getByPlaceholder("Enter command here!").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();

  await expect(
    page.getByText(
      "Output: IPEDS RaceID YearYearID UniversityUniversityCompletionsSlug Universitysh"
    )
  ).toBeVisible();

  await page.getByPlaceholder("Enter command here!").click();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("load_file dol_ri_earnings_disparity.csv");
  await page.getByRole("button", { name: "Submit" }).click();

  await expect(
    page.getByText("Output: Loaded dataset from dol_ri_earnings_disparity.csv")
  ).toBeVisible();
  await page.getByPlaceholder("Enter command here!").click();
  await page.getByPlaceholder("Enter command here!").fill("view");
  await page.getByRole("button", { name: "Submit" }).click();

  await expect(
    page.getByText(
      "Output: StateData TypeAverage Weekly EarningsNumber of WorkersEarnings Disparity"
    )
  ).toBeVisible();
});

test("Enter, view, mode, and clear shortcuts function", async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await page.getByPlaceholder("Enter command here!").click();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("load_file dol_ri_earnings_disparity.csv y");
  await page.getByPlaceholder("Enter command here!").press("Enter");
  const firstUpdatedOutputs = await page.$$eval(
    "#repl-history > div > div:first-child",
    (elements) => elements.map((e) => e.textContent)
  );
  expect(
    firstUpdatedOutputs,
    "Output: Loaded dataset from dol_ri_earnings_disparity.csv"
  );
  await page.keyboard.down("Control");
  await page.keyboard.press("V");
  await page.keyboard.up("Control");
  await expect(
    page.locator("table").filter({
      hasText:
        "StateData TypeAverage Weekly EarningsNumber of WorkersEarnings DisparityEmployed",
    })
  ).toBeVisible();

  await expect(page.getByText("Command: view")).toBeHidden();
  await page.keyboard.down("Control");
  await page.keyboard.press("M");
  await page.keyboard.up("Control");
  await expect(page.getByText("Command: view")).toBeVisible();

  await page.keyboard.down("Control");
  await page.keyboard.press("C");
  await page.keyboard.up("Control");
  await expect(
    page.locator("table").filter({
      hasText:
        "StateData TypeAverage Weekly EarningsNumber of WorkersEarnings DisparityEmployed",
    })
  ).toBeHidden();
});
test("valid broadband Request", async ({ page }) => {
  // check that command and output are displayed in REPL history
  await page.goto("http://localhost:8000/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("broadband California Ventura");
  await page.locator("button").click();

  await expect(
    page.getByText(
      "Output: NAMES2802_C03_022EstatecountyVentura County, California91.706111"
    )
  ).toBeVisible();
});

test("valid broadband Request with two word county", async ({ page }) => {
  // check that command and output are displayed in REPL history
  await page.goto("http://localhost:8000/");
  await page.getByLabel("Command input").click();
  await page
    .getByLabel("Command input")
    .fill("broadband California Los%20Angeles");
  await page.locator("button").click();
  await expect(
    page.getByText(
      "Output: NAMES2802_C03_022EstatecountyLos Angeles County, California89.906037"
    )
  ).toBeVisible();
});

test("invalid number of arguments in broadband Request", async ({ page }) => {
  // check that command and output are displayed in REPL history
  await page.goto("http://localhost:8000/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("broadband California");
  await page.locator("button").click();
  await expect(
    page.getByText("Output: Invalid number of arguments")
  ).toBeVisible();
});

test("invalid state and county in broadband Request", async ({ page }) => {
  // check that command and output are displayed in REPL history
  await page.goto("http://localhost:8000/");
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("broadband Brown slayville");
  await page.locator("button").click();
  await expect(page.getByText("Output: Invalid request")).toBeVisible();
});
