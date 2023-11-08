import * as React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import Playground from "./Playground";

describe("Playground", () => {
  it("renders Playground component", () => {
    render(
      <Playground />
    );

    // ensure playground is in the document and enabled
    expect(screen.getByTestId("playground-screen")).toBeEnabled();
  });
});