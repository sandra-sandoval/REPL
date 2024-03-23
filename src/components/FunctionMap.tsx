import { load_file } from "./LoadViewSearch";
import { view } from "./LoadViewSearch";
import { search } from "./LoadViewSearch";
import { REPLFunction } from "./REPLFunction";
import { broadband, mockBroadband } from "./Broadband";
import { mockLoad, mockSearch, mockView } from "./LoadViewSearch";
/**
 * dictionary that maps a string to a function of type REPLFunction.
 * Allows for the user to register new commands.
 */
const functionMap = new Map<string, REPLFunction>();
functionMap.set("load_file", load_file);
functionMap.set("view", view);
functionMap.set("search", search);
functionMap.set("broadband", broadband);
functionMap.set("mockLoad", mockLoad);
functionMap.set("mockView", mockView);
functionMap.set("mockSearch", mockSearch);
functionMap.set("mockBroadband", mockBroadband);

/**
 * Function that adds to the map without giving the user direct access to
 * the map. Allows the map to stay private.
 * @param methodName name of method to be registered
 * @param methodFunc REPL Function to be registered
 */
export function mapAdd(methodName: string, methodFunc: REPLFunction) {
  functionMap.set(methodName, methodFunc);
}
/**
 * FUnction that removes an element from the map while keeping the map private.
 * @param methodName name of the method to be removed
 * @param methodFunc REPL Function to be removed
 */
export function mapRemove(methodName: string, methodFunc: REPLFunction) {
  functionMap.delete(methodName);
}
/**
 * Wrapper function that retrieves size of the map
 * @returns size
 */
export function getSize() {
  return functionMap.size;
}
/**
 * Wrapper function that checks if the map contains a certain value
 * @returns boolean
 */
export function contains(functionName: string) {
  if (functionMap.has(functionName)) {
    return true;
  } else return false;
}
/**
 * Retreieves element from teh map
 * @param functionName name of function to get
 * @returns function
 */
export function get(functionName: string) {
  return functionMap.get(functionName);
}
