import * as React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import Placeholder from "./Placeholder";

const mockedHandleEditorChange = jest.fn();

describe("Placeholder", () => {
  it("Renders Placeholder component", async () => {
    render(<Placeholder type="page" title="Lorem" description="Ipsum" />);

    // ensure monaco editor loads correctly
    expect(await screen.findByText("Lorem")).toBeInTheDocument();
    expect(await screen.findByText("Ipsum")).toBeInTheDocument();
  });
});
