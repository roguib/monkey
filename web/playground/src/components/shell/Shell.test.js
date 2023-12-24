import * as React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import Shell from "./Shell";

describe("Shell", () => {
  it("Shell renders empty when props don't have value", () => {
    const evalResults = [];
    render(
      <Shell messageHistory={evalResults}/>
    );

    expect(screen.getByTestId("shell")).toBeInTheDocument();
    expect(screen.queryByTestId("shell-result")).toBeNull();
  });

  it("Shell renders one result", async () => {
    const evalResults = ["1"];
    const { container } = render(
      <Shell 
        evalResults={evalResults}
      />
    );

    expect(screen.getByTestId("shell")).toBeInTheDocument();
    const results = screen.queryAllByTestId("shell-result");
    expect(results[0]).toBeInTheDocument();
    expect(results.length).toEqual(1);
    expect(container.querySelector('li[data-value="1"]')).toBeDefined();
  });

  it("Shell renders several results", async () => {
    const evalResults = ["1", "2"];
    const { container } = render(
      <Shell 
        evalResults={evalResults}
      />
    );

    expect(screen.getByTestId("shell")).toBeEnabled();
    const results = screen.queryAllByTestId("shell-result");
    expect(results[0]).toBeInTheDocument();
    expect(results[1]).toBeInTheDocument();
    expect(results.length).toEqual(2);
    expect(container.querySelector('li[data-value="1"]')).toBeDefined();
    expect(container.querySelector('li[data-value="2"]')).toBeDefined();
  });
});
