import "./Shell.scss";
import ListGroup from "react-bootstrap/ListGroup";
import { memo } from "react";

/**
 * 
 * @param {Object} props 
 * @param {Array<{date: string, result: string}>} props.evalResults Evaluation results that have been generated
 * during the lifecycle of this playground
 */
const Shell = memo(function Shell({ evalResults = [] }) {
  return (
    <div data-testid="shell">
      <ListGroup>
        {
          evalResults
            .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
            .map((result, i) => (
              // eslint complains missing key
              // eslint-disable-next-line
              <ListGroup.Item eventKey={i} data-testid="shell-result">{result.result}</ListGroup.Item>
            ))
        }
      </ListGroup>
    </div>
  );
});

export default Shell;