import { describe, test, expect } from "vitest";
import {
  mapAdd,
  mapRemove,
  getSize,
  contains,
} from "./Users/jasonuranta/CS32/Sprints/REPL/repl-juranta-spsandov/Mock - Frontend Code/src/components/FunctionMap.tsx";
import { example_function } from "./Users/jasonuranta/CS32/Sprints/REPL/repl-juranta-spsandov/Mock - Frontend Code/tests/exampleFunction.tsx";

describe("tests function map", () => {
  test("testing getting size", () => {
    expect(getSize() === 8);
  });
  test("test contain", () => {
    expect(contains("mockLoad") === true);
    expect(contains("invalid") === false);
  });
  test("adding to the function map", () => {
    expect(contains("exampleFunction") === false);
    expect(getSize() === 8);
    mapAdd("exampleFunction", example_function);
    expect(contains("exampleFunction") === true);
    expect(getSize() === 9);
  });
  test("removing from the function map", () => {
    expect(contains("exampleFunction") === true);
    expect(getSize() === 9);
    mapRemove("exampleFunction", example_function);
    expect(contains("exampleFunction") === false);
    expect(getSize() === 8);
    console.log("removed: " + getSize());
  });
});
