import React from "react";
import ReactDOM from "react-dom/client";
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import "./index.scss";
import App from "./App";
import Playground from "./screens/playground/Playground";
import { Configuration } from "./Configuration.js";
import ErrorBoundary from "./components/errorBoundary/ErrorBoundary";
import Placeholder from "./components/placeholder/Placeholder";

const url = Configuration.path;

const getErrorElement = () => {
  return (
    <ErrorBoundary
      fallback={
        <Placeholder
          type="page"
          title="Ops! 404 Not found"
          description="The page you're looking for doesn't exist." />
      }
    />
  );
};

const router = createBrowserRouter([
  {
    path: `${url}`,
    element: <App />,
    errorElement: getErrorElement()
  },
  {
    path: `${url}:playgroundId`,
    element: <Playground />,
    errorElement: getErrorElement(),
    // TODO: loader function that checks if the playground exist before navigating
  }
]);

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  // StrictMode renders the components twice to find mistakes
  // it means that some hooks might be triggered twice
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
