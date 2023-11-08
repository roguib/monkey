import * as React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import Editor from "./Editor";

describe("Editor", () => {
  it("renders Editor component", () => {
    render(
      <Editor />
    );

    // ensure monaco editor loads correctly
    expect(screen.getByText("Loading...")).toBeDefined();
  });
});