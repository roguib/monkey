import React from "react";
import ReactDOM from "react-dom/client";
import {
  createBrowserRouter,
  RouterProvider,
} from "react-router-dom";
import "./index.css";
import App from "./App";
import Playground from "./screens/playground/Playground";

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
  },
  {
    path: "/playground/:playgroundId",
    element: <Playground />
  }
]);

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <RouterProvider router={router} />
  </React.StrictMode>
);
