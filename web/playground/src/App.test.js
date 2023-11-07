import * as React from "react";
import { render, screen } from "@testing-library/react";
import {
  BrowserRouter,
} from "react-router-dom";
import "@testing-library/jest-dom";
import App from "./App";

const mockedUsedNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockedUsedNavigate,
}));


describe("App", () => {
  it("renders App component", () => {
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
  
    // ensure both buttons are in the document and enabled
    expect(screen.getByTestId("playground-from-scratch")).toBeEnabled();
    expect(screen.getByTestId("playground-from-template")).toBeEnabled();

    // click button, check it navigates to /playground
    screen.getByTestId("playground-from-scratch").click();
    expect(mockedUsedNavigate).toHaveBeenCalledWith("/playground");

    mockedUsedNavigate.mockReset();

    // click button, check it navigates to /playground
    screen.getByTestId("playground-from-template").click();
    expect(mockedUsedNavigate).toHaveBeenCalledWith("/playground");
  });
});