import * as React from "react";
import { act, render, screen, waitForElementToBeRemoved } from "@testing-library/react";
import "@testing-library/jest-dom";
import TemplateDialog from "./TemplateDialog";

jest.mock('../../services/TemplateService.js', () => {
  const originalModule = jest.requireActual('../../services/TemplateService.js');
  return {
    __esModule: true,
    ...originalModule,
    fetchTemplates: () => jest.fn(() => [{ id: '1', title: 'title', description: 'description' }])
  };
});
describe("TemplateDialog", () => {
  it("Doesn't render TemplateDialog if show prop is false", () => {
    render(<TemplateDialog show={false} onTemplateSelected={() => {}} />);
    expect(screen.queryByTestId("template-dialog")).toBeNull();
  });

  it("Renders TemplateDialog if show prop is true", () => {
    render(<TemplateDialog show={true} onTemplateSelected={() => {}} />);
    expect(screen.queryByTestId("template-dialog")).toBeInTheDocument();
  });

  it("Dialog is closed", async () => {
    const mockedCloseDialogFn = jest.fn();
    render(<TemplateDialog show={true} onTemplateSelected={() => {}} onHide={mockedCloseDialogFn} />);
    const closeBtn = screen.queryByTestId("template-dialog-close-btn");
    expect(closeBtn).toBeInTheDocument();
    closeBtn.click();
    expect(mockedCloseDialogFn).toHaveBeenCalled();
  });

  it("Correctly loads a list of templates", async () => {
    render(<TemplateDialog show={true} onTemplateSelected={() => {}} />);
    expect(screen.queryByTestId("template-dialog")).toBeInTheDocument();
    // await loading animation disappears
    await waitForElementToBeRemoved(() => screen.getAllByTestId('loading-templates-placeholder'));
    // expects the list of templates to be present in the dialog after loading completes
    expect(screen.queryByTestId("template-list-group-item-1")).toBeInTheDocument();
  });

  it("Correctly selects one template", async () => {
    const mockedOnTemplateSelected = jest.fn();
    render(<TemplateDialog show={true} onTemplateSelected={mockedOnTemplateSelected} />);
    // await loading animation disappears
    await waitForElementToBeRemoved(() => screen.getAllByTestId('loading-templates-placeholder'));
    // click one item of the list
    screen.queryByTestId("template-list-group-item-1").click();
    expect(mockedOnTemplateSelected).toHaveBeenCalledWith('1');
  });
});
