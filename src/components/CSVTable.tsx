// CSVTable.tsx
import React from "react";

interface CSVTableProps {
  data: (string | number)[][];
}
/**
 * Component to display CSV data in formatted table
 * @param props data 2D array from CSV file
 * @returns
 */
const CSVTable: React.FC<CSVTableProps> = ({ data }) => {
  return (
    <table aria-label="table-description" className="csv-table">
      <thead>
        <tr>
          {data[0].map((header, index) => (
            <th key={index}>{header}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {data.slice(1).map((row, rowIndex) => (
          <tr key={rowIndex}>
            {row.map((cell, cellIndex) => (
              <td key={cellIndex}>{cell}</td>
            ))}
          </tr>
        ))}
      </tbody>
      <div id="table-description">
        This table displays the results receieved from the command inputted
      </div>
    </table>
  );
};

export default CSVTable;
