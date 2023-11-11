import Editor from "../../components/editor/Editor";

import "./Playground.css";

function Playground() {

  /**
   * A function that sends the latest program to the server and waits
   * for an evaluation (TBD)
   * @param {*} program
   */
  const handleEditorChanged = (program) => {
    console.log(program);
  };

  return (
    <div className="Playground" data-testid="playground-screen">
      <Editor
        onEditorChanged={handleEditorChanged}
      />
    </div>
  );
}

export default Playground;
