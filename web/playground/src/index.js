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

const url = Configuration.path;

const router = createBrowserRouter([
  {
    path: `${url}`,
    element: <App />,
  },
  {
    path: `${url}:playgroundId`,
    element: <Playground />
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
