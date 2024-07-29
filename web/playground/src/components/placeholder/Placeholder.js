import "./Placeholder.scss";
import seeNoEvilImg from "../../images/see-no-evil-monkey.png";
import Button from "react-bootstrap/Button";

/**
 *
 * @param {Object} props
 * @param {"page"} props.type
 * @param {String} props.title
 */
function Placeholder({ type, title, description }) {
  const goBack = () => {
    window.history.go(-1);
  };

  return (
    <div className={type === "page" ? "page-placeholder" : ""}>
      <div className="d-flex">
        <img src={seeNoEvilImg} className="placeholder-image me-3" alt="logo"/>
        <div className="flex-column">
          <h1 className="display-3 ml-xl-3">{title}</h1>
          <p className="text-center">{description}</p>
        </div>
      </div>
      <div className="d-flex">
        <Button
          onClick={() => goBack()}
          data-testid="playground-from-scratch">
            Take me back
        </Button>
      </div>
    </div>
  );
}

export default Placeholder;
