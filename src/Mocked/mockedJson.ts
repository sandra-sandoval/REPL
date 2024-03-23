export const mockedCSVData: Record<string, any[]> = {
  "path/to/first.csv": [
    ["RI", "White", 1058.47, 395773.6521, 1.0, "75%"],
    ["RI", "Black", 770.26, 30424.80376, 0.73, "6%"],
    ["RI", "Native American/American Indian", 471.07, 2315.505646, 0.45, "0%"],
    ["RI", "Asian-Pacific Islander", 1080.09, 18956.71657, 1.02, "4%"],
    ["RI", "Hispanic/Latino", 673.14, 74596.18851, 0.64, "14%"],
    ["RI", "Multiracial", 971.89, 8883.049171, 0.92, "2%"],
    ["RI", "Black American", 770.26, 30424.80376, 0.73, "6%"],
  ],
  "path/to/second.csv": [
    [0, "Sol", 0, 0, 0],
    [1, "two", 282.43485, 0.00449, 5.36884],
    [2, "three", 43.04329, 0.00285, -15.24144],
    [3, "four", 277.11358, 0.02422, 223.27753],
    [3759, "96G.Psc", 7.26388, 1.55643, 0.68697],
    [70667, "Proxima Centauri", -0.47175, -0.36132, -1.15037],
    [71454, "Rigel Kentaurus B", -0.50359, -0.42128, -1.1767],
    [71457, "Rigel Kentaurus A", -0.50362, -0.42139, -1.17665],
    [87666, "Barnard's Star", -0.01729, -1.81533, 0.14824],
    [118721, "last", -2.28262, 0.64697, 0.29354],
  ],

  "path/to/third.csv": [
    ["name", "year", "concentration"],
    ["jack", "2025", "cs"],
    ["mia", "2024", "biology"],
    ["elliott", "2026", "sociology"],
    ["elle", "2023", "cs"],
  ],

  "path/to/fourth.csv": [
    ["State", "number of counties", "population"],
    ["California", "58", "39.24 million"],
    ["Rhode Island", "5", "1.096 million"],
    ["Texas", "254", "29.53 million"],
    ["Georgia", "159", "10.8 million"],
  ],
  "headers.csv": [["State", "number of counties", "population"]],
  "empty.csv": [],
};

// Mocked search results
export const mockedSearchResults: Record<string, any[]> = {
  column_name_1: [["RI", "Black", 770.26, 30424.80376, 0.73, "6%"]],
  column_name_2: [[3, "four", 277.11358, 0.02422, 223.27753]],
  column_name_3: [
    ["jack", "2025", "cs"],
    ["elle", "2023", "cs"],
  ], //multiple rows returned
  column_name_4: [[]], //no search results found
};
export const mockedLoad: Record<string, string> = {
  "dol_ri_earnings_disparity.csv,y":
    "Loaded data from dol_ri_earnings_disparity.csv",
  "postsecondary_education.csv,y":
    "Loaded data from postsecondary_education.csv",
  "invalid.csv,y": "invalid.csv (No such file or directory)",
  "invalid.csv": "Wrong/missing arguments, input valid arguments",
};

export const mockedView = new Map<string, string[][]>();
mockedView.set("", [
  ["RI", "White", "1058.47", "395773.6521", "1.0", "75%"],
  ["RI", "Black", "770.26", "30424.80376", "0.73", "6%"],
  [
    "RI",
    "Native American/American Indian",
    "471.07",
    "2315.505646",
    "0.45",
    "0%",
  ],
  ["RI", "Asian-Pacific Islander", "1080.09", "18956.71657", "1.02", "4%"],
  ["RI", "Hispanic/Latino", " 673.14", "74596.18851", "0.64", "14%"],
  ["RI", "Multiracial", "971.89", "8883.049171", "0.92", "2%"],
]);

export const mockedSearch = new Map<string, string[][]>();
mockedSearch.set("RI,State", [
  ["RI", "White", "1058.47", "395773.6521", "1.0", "75%"],
  ["RI", "Black", "770.26", "30424.80376", "0.73", "6%"],
  [
    "RI",
    "Native American/American Indian",
    "471.07",
    "2315.505646",
    "0.45",
    "0%",
  ],
  ["RI", "Asian-Pacific Islander", "1080.09", "18956.71657", "1.02", "4%"],
  ["RI", "Hispanic/Latino", " 673.14", "74596.18851", "0.64", "14%"],
  ["RI", "Multiracial", "971.89", "8883.049171", "0.92", "2%"],
]);
mockedSearch.set("RI,0", [
  ["RI", "White", "1058.47", "395773.6521", "1.0", "75%"],
  ["RI", "Black", "770.26", "30424.80376", "0.73", "6%"],
  [
    "RI",
    "Native American/American Indian",
    "471.07",
    "2315.505646",
    "0.45",
    "0%",
  ],
  ["RI", "Asian-Pacific Islander", "1080.09", "18956.71657", "1.02", "4%"],
  ["RI", "Hispanic/Latino", " 673.14", "74596.18851", "0.64", "14%"],
  ["RI", "Multiracial", "971.89", "8883.049171", "0.92", "2%"],
]);
mockedSearch.set("Black,1", [
  ["RI", "Black", "770.26", "30424.80376", "0.73", "6%"],
]);
mockedSearch.set("Black", [
  ["RI", "Black", "770.26", "30424.80376", "0.73", "6%"],
]);
mockedSearch.set("invalid search", [[]]);

export const mockedBroadband = new Map<string, string[][]>();
mockedBroadband.set("California,Ventura", [
  ["NAME", "S2802_C03_022E", "state", "county"],
  ["Ventura County, California", "91.7", "06", "111"],
]);
mockedBroadband.set("California", [[]]);
