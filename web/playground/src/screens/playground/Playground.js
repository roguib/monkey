import Editor from "../../components/editor/Editor";
import Shell from "../../components/shell/Shell";
import { useState, useCallback, useMemo, useEffect } from "react";
import { useParams, useLocation } from "react-router-dom";
import useWebSocket from "react-use-websocket";
import "./Playground.css";

function Playground() {
  const WEBSOCKET_URL = "ws://localhost:7001/websocket";
  const [history, setHistory] = useState([]);

  const { sendMessage, lastMessage } = useWebSocket(WEBSOCKET_URL);

  const { playgroundId } = useParams();

  const { state } = useLocation();

  useMemo(() => {
    if (lastMessage !== null) {
      const evalData = JSON.parse(lastMessage.data);
      // we're ignoring status for now
      setHistory((prev) => prev.concat(evalData.result));
    }
  }, [lastMessage]);

  useEffect(() => {
    const fn = async () => {
      if (state?.skipFetchHistory) {
        return;
      }
  
      const data = await fetch(`http://localhost:7001/playground/${playgroundId}`, {
        method: "GET",
        headers: {
          "Access-Control-Allow-Origin": "*",
        }
      });
      if (!data.ok) {
        // no-op
      }
      const { history } = await data.json();
      setHistory(history);
    };
    fn();
    return () => {
      // make sure location state is cleared on browser refresh
      window.history.replaceState({}, document.title);
    };
  }, [state?.skipFetchHistory]);

  /**
   * A function that sends the latest program to the server and waits
   * for an evaluation (TBD)
   * @param {*} program
   */
  const handleEditorChanged = useCallback((program) => {
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
          evalResults={history}
        />
      </div>
    </div>
  );
}

export default Playground;
