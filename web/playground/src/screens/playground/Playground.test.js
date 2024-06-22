import * as React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import {
  BrowserRouter,
} from "react-router-dom";
import Playground from "./Playground";

jest.mock("platform-detect", () => {
  return {
    __esModule: false,
    formfactor: "desktop",
    macos: true,
  };
});
describe("Playground", () => {
  it("renders Playground component", () => {
    render(
      <BrowserRouter>
        <Playground />
      </BrowserRouter>
    );

    // ensure playground is in the document and enabled
    expect(screen.getByTestId("playground-screen")).toBeEnabled();
  });
});