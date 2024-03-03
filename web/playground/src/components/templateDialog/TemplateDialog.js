import "./TemplateDialog.scss";
import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import ListGroup from "react-bootstrap/ListGroup";
import { useEffect, useState } from "react";
import Placeholder from "react-bootstrap/Placeholder";
import { fetchTemplates } from "../../services/TemplateService";

const ListData = (props) => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);

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
      <div data-testid="loading-templates-placeholder">
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
      </div>
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
          data-testid={`template-list-group-item-${id}`}
          action
        >
          <div>
            <h5>{title}</h5>
            <p>{description}</p>
          </div>
        </ListGroup.Item>
      ))
    }
  </>);
};

const TemplateDialog = (props) => {
  return (
    <Modal
      {...props}
      data-testid="template-dialog"
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
        <Button data-testid="template-dialog-close-btn" onClick={props.onHide}>Close</Button>
      </Modal.Footer>
    </Modal>
  );
};

export default TemplateDialog;
