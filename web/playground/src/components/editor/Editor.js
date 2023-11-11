import { useState } from "react";
import { default as MonacoEditor } from "@monaco-editor/react";
import "./Editor.css";


/**
 *
 * @param {Object} props
 * @param {Function} onEditorChanged A callback function that expects the current value of the
 * program written in the MonacoEditor
 * @returns
 */
function Editor({ onEditorChanged }) {
  const MS_TIMEOUT_TO_EVALUATE = 5000;
  const [lastModifiedTimeout, setLastModifiedTimeout] = useState(() => {});

  /**
   * Sets a timeout that will pass the latest modification to the parent component
   * if the latest modification was made MS_TIMEOUT_TO_EVALUATE or more. If two
   * or more modifications are made between 0..MS_TIMEOUT_TO_EVALUATE then it clears
   * the timeout and sets another one with the updated value
   *
   * @param {string} program
   */
  const handleEditorChange = (program) => {
    if (lastModifiedTimeout) {
      clearTimeout(lastModifiedTimeout);
    }

    setLastModifiedTimeout(
      setTimeout(() => {
        console.log(program);
        console.log(`${MS_TIMEOUT_TO_EVALUATE} without changes, sending the back to parent component`);
        onEditorChanged(program);
      }, MS_TIMEOUT_TO_EVALUATE));
  };

  return (
    <div className="Editor">
      <MonacoEditor
        height="90vh"
        defaultLanguage="javascript"
        defaultValue="// some comment"
        onChange={handleEditorChange}
      />
    </div>
  );
}

export default Editor;
