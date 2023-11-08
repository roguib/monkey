import { default as MonacoEditor } from "@monaco-editor/react";

import "./Editor.css";

function Editor() {

  return (
    <div className="Editor">
      <p>This is the Editor area</p>
      <MonacoEditor
        height="90vh"
        defaultLanguage="javascript"
        defaultValue="// some comment"
      />
    </div>
  );
}

export default Editor;
