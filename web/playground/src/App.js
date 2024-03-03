import logo from "./logo.png";
import "./App.scss";
import  TemplateDialog from "./components/templateDialog/TemplateDialog";
import { useNavigate } from "react-router-dom";
import Button from "react-bootstrap/Button";
import { useState } from "react";

function App() {
  const navigate = useNavigate();
  const [showDialog, setShowDialog] = useState(false);

  const createNewPlayground = async (templateId) => {
    const data = await fetch("http://localhost:7001/playground/new", {
      method: "POST",
      headers: {
        "Access-Control-Allow-Origin": "*",
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        templateId
      })
    });
    if (!data.ok) {
      // no-op
    }
    const { id } = await data.json();
    console.log(`new playground created with id ${id}`);
    navigate(`/playground/${id}`, { state: { skipFetchHistory: !templateId ? true : false } });
  };

  return (
    <div className="landing-page-wrapper container d-flex justify-content-center align-items-center">
      <div>
        <div className="row d-flex justify-content-center align-items-center">
          <div className="col-2 d-flex align-items-center justify-content-center">
            <img src={logo} className="App-logo" alt="logo"/>
          </div>
          <span className="col-10 display-3">The Monkey Playground</span>
        </div>
        <div className="row pt-4 d-flex align-items-center justify-content-center">
          <div className="col-6 d-flex align-items-center justify-content-center">
            <Button
              onClick={() => createNewPlayground()}
              data-testid="playground-from-scratch">
                New playground from scratch
            </Button>
          </div>
          <div className="col-6 d-flex align-items-center justify-content-center">
            <Button
              onClick={() => setShowDialog(true)}
              data-testid="playground-from-template">
                New playground from template
            </Button>
          </div>
        </div>
      </div>
      <TemplateDialog
        show={showDialog}
        onTemplateSelected={(templateId) => createNewPlayground(templateId)}
        onHide={() => setShowDialog(false)}
      />
    </div>
  );
}

export default App;
