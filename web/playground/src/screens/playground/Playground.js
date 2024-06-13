import Editor from "../../components/editor/Editor";
import Shell from "../../components/shell/Shell";
import { useState, useCallback, useMemo, useEffect, useRef } from "react";
import { useParams, useLocation } from "react-router-dom";
import { getPlaygroundHistory } from "../../services/PlaygroundService";
import useWebSocket from "react-use-websocket";
import { Configuration } from "../../Configuration";
import "./Playground.scss";

function Playground() {
  const WEBSOCKET_URL = `${Configuration.websocketProtocol}://${Configuration.baseUrl}${Configuration.port ? ":" + Configuration.port : ""}${Configuration.path}api/websocket`;
  const [program, setProgram] = useState("");
  const [history, setHistory] = useState([]);
  const [playgroundNotFound, setPlaygroundNotFound] = useState(false);

  const editorRef = useRef(undefined);

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

  const handleKeydownEvent = (event) => {
    event.stopImmediatePropagation();
    if (event.key === "S" || event.key === "s" && (event.ctrlKey || event.metaKey)) {
      // handle redo action
      console.log("saved");
      event.preventDefault();
    } else if (event.key === "Z" && (event.ctrlKey || event.metaKey)) {
      // handle undo action
    }
  };

  useEffect(() => {
    (async () => {
      if (state?.skipFetchHistory) {
        return;
      }

      try {
        const { program = "", history = [] } = await getPlaygroundHistory(playgroundId);
        setProgram(program);
        setHistory(history);

        if (program && !history.length) {
          // TODO: This should already come in the history array. We shouldn't need an extra computation
          // of a known value
          handleEditorChanged(program);
        }

        editorRef?.current?.addEventListener("keydown", handleKeydownEvent);
      } catch (error) {
        setPlaygroundNotFound(true);
        return;
      }
    })();
    return () => {
      // make sure location state is cleared on browser refresh
      window.history.replaceState({}, document.title);
      editorRef?.current?.removeEventListener("keydown", handleKeydownEvent);
    };
  }, [state?.skipFetchHistory]);

  /**
   * A function that sends the latest program to the server and waits
   * for an evaluation
   * @param {*} program
   */
  const handleEditorChanged = useCallback((program) => {
    if (program) {
      sendMessage(JSON.stringify({
        playgroundId,
        program,
      }));
    }
  }, []);

  if (playgroundNotFound) {
    // todo: improve this screen
    return (
      <p>404 playground wasn't found</p>
    );
  }
  return (
    <div className="playground" data-testid="playground-screen">
      <div style={{width: "70vw"}}>
        <Editor
          onEditorChanged={handleEditorChanged}
          initialValue={program}
          ref={editorRef}
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
