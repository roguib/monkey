import * as React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import {
  BrowserRouter,
} from "react-router-dom";
import "@testing-library/jest-dom";
import App from "./App";
import { FetchMock } from "../tests/FetchMock";

const mockedUsedNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => mockedUsedNavigate,
}));


describe("App", () => {
  it("renders App component", () => {
    const fetchMock = new FetchMock();
    fetchMock.addMock("http://localhost:7001/playground/new", { id: "abc-dfg-hij" });
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
    waitFor(() => expect(mockedUsedNavigate).toHaveBeenCalledWith("/playground"));

    mockedUsedNavigate.mockReset();

    // click button, check it navigates to /playground
    screen.getByTestId("playground-from-template").click();
    waitFor(() =>expect(mockedUsedNavigate).toHaveBeenCalledWith("/playground"));
  });
});