import Editor from "../../components/editor/Editor";
import Shell from "../../components/shell/Shell";
import { useState, useCallback, useMemo } from "react";
import { useParams } from "react-router-dom";
import useWebSocket, { ReadyState } from "react-use-websocket";
import "./Playground.css";

function Playground() {
  const WEBSOCKET_URL = "ws://localhost:7001/websocket";
  const [messageHistory, setMessageHistory] = useState([]);

  const { sendMessage, lastMessage, readyState } = useWebSocket(WEBSOCKET_URL);

  const { playgroundId } = useParams();

  useMemo(() => {
    if (lastMessage !== null) {
      console.log(lastMessage);
      const evalData = JSON.parse(lastMessage.data);
      // we're ignoring status for now
      setMessageHistory((prev) => prev.concat(evalData.result));
      console.log(messageHistory);
    }
  }, [lastMessage]);

  const connectionStatus = {
    [ReadyState.CONNECTING]: "Connecting",
    [ReadyState.OPEN]: "Open",
    [ReadyState.CLOSING]: "Closing",
    [ReadyState.CLOSED]: "Closed",
    [ReadyState.UNINSTANTIATED]: "Uninstantiated",
  }[readyState];

  /**
   * A function that sends the latest program to the server and waits
   * for an evaluation (TBD)
   * @param {*} program
   */
  const handleEditorChanged = useCallback((program) => {
    console.log(connectionStatus);
    console.log(program);
    sendMessage(JSON.stringify({
      playgroundId,
      program,
    }));
  }, []);


  return (
    <div className="playground" data-testid="playground-screen">
      <div style={{width: "70vw"}}>
        <Editor
          onEditorChanged={handleEditorChanged}
        />
      </div>
      <div style={{width: "30vw", height: "100vh"}}>
        <Shell 
          evalResults={messageHistory}
        />
      </div>
    </div>
  );
}

export default Playground;
