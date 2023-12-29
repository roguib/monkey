import "./Shell.scss";
import { memo } from "react";

/**
 * 
 * @param {Object} props 
 * @param {string[]} props.evalResults Evaluation results that have been generated
 * during the lifecycle of this playground
 */
const Shell = memo(function Shell({ evalResults }) {
  return (
    <div data-testid="shell">
      <ul className="no-bullets">
        {
          evalResults?.map((result, i) => {
            return i > 0 ? 
              (<>
                <li data-testid="shell-separator" key={i + 300000}><hr /></li>
                <li data-value={result} data-testid="shell-result" key={i}>{result}</li>
              </>) :
              (<li data-value={result} data-testid="shell-result" key={i}>{result}</li>);
          })
        }
      </ul>
    </div>
  );
});

export default Shell;