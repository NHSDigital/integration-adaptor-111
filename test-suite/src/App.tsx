import React from "react";
import {
  Route,
  BrowserRouter as Router,
  Routes as Switch,
} from "react-router-dom";
import { routes } from "./routes";
import { AppRoute } from "./types";

function App() {
  return (
    <div
      style={{
        height: "100%",
        width: "100%",
        background: "#f0f4f5",
      }}
    >
      <Router>
        <Switch>
          {routes.map((r: AppRoute) => (
            <Route key={r.path} path={r.path} element={r.element} />
          ))}
        </Switch>
      </Router>
    </div>
  );
}

export default App;
