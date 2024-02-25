import "./TemplateDialog.scss";
import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import ListGroup from "react-bootstrap/ListGroup";
import { useEffect, useState } from "react";
import Placeholder from "react-bootstrap/Placeholder";

const ListData = (props) => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchTemplates = async () => {
    let templates = [];
    try {
      const data = await fetch("http://localhost:7001/templates", {
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

  useEffect(() => {
    const fetchDataAsync = async () => {
      const result = await fetchTemplates();
      setLoading(false);
      setData(result);
    };

    if (!data?.length) {
      fetchDataAsync();
    }
  }, []);

  if (loading) {
    return (
      <>
        <Placeholder as="p" animation="glow">
          <Placeholder xs={12} />
          <Placeholder xs={6} />
        </Placeholder>
        <Placeholder as="p" animation="glow">
          <Placeholder xs={12} />
          <Placeholder xs={6} />
        </Placeholder>
        <Placeholder as="p" animation="glow">
          <Placeholder xs={12} />
          <Placeholder xs={6} />
        </Placeholder>
      </>
    );
  }

  return (<>
    {
      data?.map(({ id, title, description }, i) => (
        // eslint complains missing key
        // eslint-disable-next-line
        <ListGroup.Item
          eventKey={i+id}
          onClick={() => props.onTemplateSelected(id)}
          action
        >
          <h5>{title}</h5>
          <p>{description}</p>
        </ListGroup.Item>
      ))
    }
  </>);
};

const TemplateDialog = (props) => {
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
          <ListData onTemplateSelected={props.onTemplateSelected} />
        </ListGroup>
      </Modal.Body>
      <Modal.Footer>
        <Button onClick={props.onHide}>Close</Button>
      </Modal.Footer>
    </Modal>
  );
};

export default TemplateDialog;
