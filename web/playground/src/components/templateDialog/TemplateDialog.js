import "./TemplateDialog.scss";
import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import ListGroup from "react-bootstrap/ListGroup";
import { useEffect, useState } from "react";

/**
 *
 */
function TemplateDialog(props) {
  const loadTemplates = async () => {
    let templates = [];
    try {
      const data = await fetch("http://localhost:7001/playground/templates", {
        method: "GET",
        headers: {
          "Access-Control-Allow-Origin": "*",
        }
      });
      if (!data.ok) {
        // no-op
      }
      templates = (await data.json())?.templates;
      if (!templates.length) {
        // TODO: Show alert an error has happened while loading templates
      }
    } catch (error) {
      // no-op
    }
    return templates;
  };

  const [templates, setTemplates] = useState([]);

  useEffect(() => {
    const fn = async () => {
      const templates = await loadTemplates();
      setTemplates(templates);
    };
    if (props.show) {
      fn();
    }
  }, [props.show]);

  return (
    <Modal
      {...props}
      size="lg"
      aria-labelledby="contained-modal-title-vcenter"
      centered
    >
      <Modal.Header closeButton>
        <Modal.Title id="contained-modal-title-vcenter">
          Template picker
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <p>You can start experimenting with the Monkey programming language by picking one of the templates that showcase the different features of the language.</p>
        <ListGroup>
          {
            templates.map(({ id, title, description }, i) => (
              // eslint complains missing key
              // eslint-disable-next-line
              <ListGroup.Item
                eventKey={i}
                onClick={() => {}}
                action
              >
                <span>{id}</span>
                <span>{title}</span>
                <span>{description}</span>
              </ListGroup.Item>
            ))
          }
        </ListGroup>
      </Modal.Body>
      <Modal.Footer>
        <Button onClick={props.onHide}>Close</Button>
      </Modal.Footer>
    </Modal>
  );
}

export default TemplateDialog;
