import logo from "./logo.svg";
import "./App.css";
import { useNavigate } from "react-router-dom";

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
    navigate("/playground");
  };

  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <div>
          <button 
            onClick={() => createNewPlayground({ empty: true })}
            data-testid="playground-from-scratch">
              New playground from scratch
          </button>
          <button 
            onClick={() => createNewPlayground({ empty: false })}
            data-testid="playground-from-template">
              New playground from template
          </button>
        </div>
      </header>
    </div>
  );
}

export default App;
