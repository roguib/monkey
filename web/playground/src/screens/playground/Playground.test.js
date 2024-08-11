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
jest.mock('../../services/PlaygroundService.js', () => {
  const originalModule = jest.requireActual('../../services/PlaygroundService.js');
  return {
    __esModule: true,
    ...originalModule,
    getPlaygroundHistory: () => jest.fn(() => [{ program: '', history: [] }])
  };
});
describe("Playground", () => {
  it("renders Playground component", async () => {
    render(
      <BrowserRouter>
        <Playground />
      </BrowserRouter>
    );

    // ensure playground is in the document and enabled
    expect(await screen.findByTestId("playground-screen")).toBeEnabled();
  });
});