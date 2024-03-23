import React, { Component } from "react";

interface TableProps {
  headers: null | (string | number)[];
  data: null | (string | number)[][];
}
/**
 * Component to display Search results in formatted table. Each result is indexed.
 */
class SearchTable extends Component<TableProps, {}> {
  render() {
    return (
      <table className="search-table">
        <thead>
          <tr>
            <th>#</th> {/* Header for the row number column */}
            {this.props.headers?.map((header, index: number) => (
              <th key={index}>{header}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {this.props.data?.map(
            (row: (string | number)[], rowIndex: number) => (
              <tr key={rowIndex}>
                <td>{rowIndex + 1}</td> {/* Row number */}
                {row.map((cell: string | number, cellIndex: number) => (
                  <td key={cellIndex}>{cell}</td>
                ))}
              </tr>
            )
          )}
        </tbody>
      </table>
    );
  }
}

export default SearchTable;
