import { ReactElement, useState } from "react";

// Interface for the methods and map
export interface REPLFunction {
    (args: Array<string>): Promise<string> | Promise<ReactElement | string>;
  }