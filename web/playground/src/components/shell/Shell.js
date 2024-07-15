import "./Shell.scss";
import ListGroup from "react-bootstrap/ListGroup";
import { memo } from "react";
import Badge from "react-bootstrap/Badge";

/**
 * 
 * @param {Object} props 
 * @param {Array<{date: string, result: string}>} props.evalResults Evaluation results that have been generated
 * during the lifecycle of this playground
 */
const Shell = memo(function Shell({ evalResults = [] }) {
  return (
    <div data-testid="shell">
      <ListGroup variant="flush">
        {
          evalResults
            .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime())
            .map((result, i) => (
              // eslint complains missing key
              // eslint-disable-next-line
              <ListGroup.Item
                eventKey={i}
                data-testid="shell-result"
                onClick={(e) => e.stopPropagation()}
              >
                <div className="flex-column">
                  <div className="d-flex justify-content-end">
                    <Badge className="date-badge" bg="primary" pill>
                      {new Date(result.date).toLocaleString()}
                    </Badge>
                  </div>
                  <div className="d-flex align-items-center">
                    <svg
                      stroke="currentColor"
                      fill="currentColor"
                      strokeWidth="0"
                      viewBox="0 0 24 24"
                      className="ml-[6px] w-4 h-auto text-[#0076cf] dark:text-[#2fafff]"
                      height="1em"
                      width="1em"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path d="M10.707 17.707 16.414 12l-5.707-5.707-1.414 1.414L13.586 12l-4.293 4.293z" />
                    </svg>
                    <span id="shell-result">{result.result}</span>
                  </div>
                </div>
              </ListGroup.Item>
            ))
        }
      </ListGroup>
    </div>
  );
});

export default Shell;