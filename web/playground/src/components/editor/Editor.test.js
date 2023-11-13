import * as React from "react";
import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import Editor from "./Editor";
import { default as MonacoEditor } from "@monaco-editor/react";
import userEvent from "@testing-library/user-event";


const mockedHandleEditorChange = jest.fn();

jest.mock("./Editor", () => ({
  __esModule: true,
  ...jest.requireActual("./Editor"),
  default: jest.fn(),
  handleEditorChange: () => mockedHandleEditorChange,
}));

describe("Editor", () => {
  beforeAll(() => {
  });

  it("Renders Editor component", () => {
    Editor.mockReturnValue(<MonacoEditor />);
    render(<Editor />);

    // ensure monaco editor loads correctly
    expect(screen.getByText("Loading...")).toBeInTheDocument();
  });

  it("Reacts to changes from the editor", async () => {
    Editor.mockReturnValue(<input data-testid="monaco-input-area" onChange={mockedHandleEditorChange} />);
    const user = userEvent.setup();

    render(<Editor />);
    screen.debug();

    expect(screen.getByTestId("monaco-input-area")).toBeInTheDocument();
    await user.type(screen.getByTestId("monaco-input-area"), "input code from the user");
    expect(mockedHandleEditorChange).toHaveBeenCalled();
  });
});
