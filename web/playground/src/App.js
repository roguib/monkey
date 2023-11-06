import logo from './logo.svg';
import './App.css';
import { useNavigate } from 'react-router-dom';

function App() {
  const navigate = useNavigate();

  const createNewPlayground = ({ empty }) => {
    navigate('/playground');
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
