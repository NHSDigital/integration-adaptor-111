import React from "react";
import ReactDOM from "react-dom/client";
import "./style/index.css";
import "./style/nhsuk-6.1.0.min.css";
import "nhsuk-frontend/dist/nhsuk.min.js";

import App from "./App";

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
