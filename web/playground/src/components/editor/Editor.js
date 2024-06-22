import { useEffect, useState, useRef } from "react";
import { formfactor } from "platform-detect";
import { default as MonacoEditor } from "@monaco-editor/react";
import "./Editor.scss";


/**
 *
 * @param {Object} props
 * @param {Function} onEditorChanged A callback function that expects the current value of the
 * program written in the MonacoEditor
 * @returns
 */
function Editor({ onEditorChanged, initialValue }) {
  const MS_TIMEOUT_TO_EVALUATE = 5000;
  const [lastModifiedTimeout, setLastModifiedTimeout] = useState(() => {});
  const [program, setProgram] = useState(initialValue);
  const editorRef = useRef(undefined);

  const handleKeydownEvent = (event) => {
    event.stopImmediatePropagation();
    if (event.key === "S" || event.key === "s" && (event.ctrlKey || event.metaKey)) {
      // handle save action
      onEditorChanged(program);
      event.preventDefault();
    }
  };

  useEffect(() => {
    editorRef?.current?.addEventListener("keydown", handleKeydownEvent);
    return () => {
      editorRef?.current?.removeEventListener("keydown", handleKeydownEvent);
    };
  });

  /**
   * Sets a timeout that will pass the latest modification to the parent component
   * if the latest modification was made MS_TIMEOUT_TO_EVALUATE or more. If two
   * or more modifications are made between 0..MS_TIMEOUT_TO_EVALUATE then it clears
   * the timeout and sets another one with the updated value
   *
   * @param {string} programInEditor
   */
  const handleEditorChange = (programInEditor) => {
    setProgram(programInEditor);

    if (formfactor !== "desktop") {
      if (lastModifiedTimeout) {
        clearTimeout(lastModifiedTimeout);
      }
      setLastModifiedTimeout(
        setTimeout(() => {
          console.log(`${MS_TIMEOUT_TO_EVALUATE} without changes, sending the back to parent component`);
          onEditorChanged(program);
        }, MS_TIMEOUT_TO_EVALUATE));
    }
  };

  return (
    <div className="Editor" ref={editorRef}>
      <MonacoEditor
        height="100vh"
        defaultLanguage="javascript"
        theme="vs-dark"
        value={initialValue}
        onChange={handleEditorChange}
        options={{
          minimap: {
            enabled: false,
          },
        }}
      />
    </div>
  );
}

export default Editor;
