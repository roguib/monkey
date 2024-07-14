import Editor from "../../components/editor/Editor";
import Shell from "../../components/shell/Shell";
import { useState, useMemo, useEffect } from "react";
import { useParams, useLocation } from "react-router-dom";
import { getPlaygroundHistory } from "../../services/PlaygroundService";
import useWebSocket from "react-use-websocket";
import { Configuration } from "../../Configuration";
import "./Playground.scss";
import { formfactor, macos } from "platform-detect";
import Alert from "react-bootstrap/Alert";

function Playground() {
  const WEBSOCKET_URL = `${Configuration.websocketProtocol}://${Configuration.baseUrl}${Configuration.port ? ":" + Configuration.port : ""}${Configuration.path}api/websocket`;
  const [program, setProgram] = useState("");
  const [history, setHistory] = useState([]);
  const [playgroundNotFound, setPlaygroundNotFound] = useState(false);

  const { sendMessage, lastMessage } = useWebSocket(WEBSOCKET_URL);

  const { playgroundId } = useParams();

  const { state } = useLocation();

  useMemo(() => {
    if (lastMessage !== null) {
      const evalData = JSON.parse(lastMessage.data);
      // we're ignoring status for now
      setHistory((prev) => prev.concat({ result: evalData.result, date: evalData.date }));
    }
  }, [lastMessage]);

  useEffect(() => {
    (async () => {
      if (state?.skipFetchHistory) {
        return;
      }

      try {
        const { program = "", history = [] } = await getPlaygroundHistory(playgroundId);
        setProgram(program);
        setHistory(history);
      } catch (error) {
        setPlaygroundNotFound(true);
        return;
      }
    })();
    return () => {
      // make sure location state is cleared on browser refresh
      window.history.replaceState({}, document.title);
    };
  }, [state?.skipFetchHistory]);

  /**
   * A function that sends the latest program to the server and waits
   * for an evaluation
   * @param {*} program
   */
  const handleEditorChanged = (program) => {
    if (program) {
      sendMessage(JSON.stringify({
        playgroundId,
        program,
      }));
    }
  };

  const getSaveKeys = () => {
    return macos ?
      "command and S" :
      "control and S";
  };

  const getAlertString = () => {
    if (formfactor === "desktop") {
      return `Press ${getSaveKeys()} in your keyboard to compute new results of your program.`;
    } else {
      return "Wait for 5 seconds, without making any change in your program, to see the results.";
    }
  };

  if (playgroundNotFound) {
    // todo: improve this screen
    return (
      <p>404 playground wasn't found</p>
    );
  }
  return (
    <>
      <div className="playground-alert">
        <Alert key="primary" variant="primary" dismissible>
          <span>{getAlertString()}</span>
        </Alert>
      </div>
      <div className="playground" data-testid="playground-screen">
        <div className="playground-editor">
          <Editor
            onEditorChanged={handleEditorChanged}
            initialValue={program}
          />
        </div>
        <div className="playground-shell">
          <Shell
            evalResults={history}
          />
        </div>
      </div>
    </>
  );
}

export default Playground;
