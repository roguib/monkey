import Editor from "../../components/editor/Editor";

import "./Playground.css";

function Playground() {

  return (
    <div className="Playground" data-testid="playground-screen">
      <Editor />
    </div>
  );
}

export default Playground;
