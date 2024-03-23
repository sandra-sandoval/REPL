import { test, expect } from "@playwright/test";

test.beforeEach(async ({ page }) => {
  await page.goto("http://localhost:8000/");
});
test("mock valid load", async ({ page }) => {
  // it("should load the file successfully", async () => {
  await page.getByPlaceholder("Enter command here!").click();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("mockLoad postsecondary_education.csv,y");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Output: Loaded data from postsecondary_education.csv")
  ).toBeVisible();
});

test("mock invalid load", async ({ page }) => {
  // it("should load the file successfully", async () => {
  await page.getByPlaceholder("Enter command here!").click();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("mockLoad invalid.csv,y");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Output: invalid.csv (No such file or directory)")
  ).toBeVisible();
});

test("mock valid view", async ({ page }) => {
  // it("should load the file successfully", async () => {
  await page.getByPlaceholder("Enter command here!").click();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("mockLoad dol_ri_earnings_disparity.csv,y");
  await page.getByPlaceholder("Enter command here!").click();
  await page.getByPlaceholder("Enter command here!").fill("mockView ");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText(
      "RIWhite1058.47395773.65211.075%RIBlack770.2630424.803760.736%RINative American/A"
    )
  ).toBeVisible();
});
test("mock search with index", async ({ page }) => {
  // it("should load the file successfully", async () => {
  await page.getByPlaceholder("Enter command here!").click();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("mockLoad dol_ri_earnings_disparity.csv,y");
  await page.getByPlaceholder("Enter command here!").click();
  await page.getByPlaceholder("Enter command here!").fill("mockSearch Black,1");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Output: RIBlack770.2630424.803760.736%")
  ).toBeVisible();
});
test("mock search without columnID", async ({ page }) => {
  // it("should load the file successfully", async () => {
  await page.getByPlaceholder("Enter command here!").click();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("mockLoad dol_ri_earnings_disparity.csv");
  await page.getByPlaceholder("Enter command here!").click();
  await page.getByPlaceholder("Enter command here!").fill("mockSearch Black");
  await page.getByRole("button", { name: "Submit" }).click();
  await expect(
    page.getByText("Output: RIBlack770.2630424.803760.736%")
  ).toBeVisible();
});

test("mock valid broadband request", async ({ page }) => {
  await page.goto("http://localhost:8000/");
  await page.getByPlaceholder("Enter command here!").click();
  await page
    .getByPlaceholder("Enter command here!")
    .fill("mockBroadband California,Ventura");
  await page.getByPlaceholder("Enter command here!").press("Enter");
  await expect(
    page.getByText(
      "Output: NAMES2802_C03_022EstatecountyVentura County, California91.706111"
    )
  ).toBeVisible();
});
