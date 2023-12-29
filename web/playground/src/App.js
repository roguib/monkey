import logo from "./logo.png";
import "./App.scss";
import { useNavigate } from "react-router-dom";
import Button from "react-bootstrap/Button";

function App() {
  const navigate = useNavigate();

  // eslint-disable-next-line no-unused-vars
  const createNewPlayground = async ({ empty }) => {
    const data = await fetch("http://localhost:7001/playground/new", {
      method: "POST",
      headers: {
        "Access-Control-Allow-Origin": "*",
      }
    });
    if (!data.ok) {
      // no-op
    }
    const { id } = await data.json();
    console.log(`new playground created with id ${id}`);
    navigate(`/playground/${id}`, { state: { skipFetchHistory: true } });
  };

  return (
    <div className="container">
      <div className="row d-flex justify-content-center align-items-center">
        <div className="col-2 d-flex align-items-center justify-content-center">
          <img src={logo} className="App-logo" alt="logo"/>
        </div>
        <span className="col-8 display-1">The Monkey Playground</span>
      </div>
      <div className="row d-flex align-items-center justify-content-center">
        <div className="col-4 d-flex align-items-center justify-content-center">
          <Button
            onClick={() => createNewPlayground({ empty: true })}
            data-testid="playground-from-scratch">
              New playground from scratch
          </Button>
        </div>
        <div className="col-4 d-flex align-items-center justify-content-center">
          <Button
            onClick={() => createNewPlayground({ empty: false })}
            data-testid="playground-from-template">
              New playground from template
          </Button>
        </div>
      </div>
    </div>
  );
}

export default App;
