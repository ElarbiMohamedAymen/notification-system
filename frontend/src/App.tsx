import { useState } from "react";
import "./App.css";
import AdminPanel from "./AdminPanel";
import UserPanel from "./UserPanel";

function randomUserId() {
  return "user-" + Math.random().toString(36).slice(2, 8);
}

function App() {
  const [userId, setUserId] = useState(() => localStorage.getItem("userId") ?? randomUserId());
  const [view, setView] = useState<"user" | "admin">("user");

  function updateUserId(next: string) {
    setUserId(next);
    localStorage.setItem("userId", next);
  }

  return (
    <div className="app">
      <header>
        <h1>Reservation notifications demo</h1>
        <nav>
          <button className={view === "user" ? "active" : ""} onClick={() => setView("user")}>
            User
          </button>
          <button className={view === "admin" ? "active" : ""} onClick={() => setView("admin")}>
            Admin
          </button>
        </nav>
        {view === "user" && (
          <label className="row">
            User id
            <input value={userId} onChange={(e) => updateUserId(e.target.value)} />
          </label>
        )}
      </header>

      <main>{view === "user" ? <UserPanel userId={userId} /> : <AdminPanel />}</main>
    </div>
  );
}

export default App;
