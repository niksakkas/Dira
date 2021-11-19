import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import { BrowserRouter as Router } from 'react-router-dom';
import './CSS/index.css'
import './CSS/project_screens.css'
import './CSS/project_main.css'
import './CSS/calendar.css'
import './CSS/backlog.css'
import './CSS/sidenav.css'
import './CSS/activesprint.css'
import './CSS/members.css'
import './CSS/epics.css'
import './CSS/popup.css'
import './CSS/login.css'
import './CSS/create_project.css'
import './CSS/issue_preview.css'

ReactDOM.render(
  <React.StrictMode>
    <Router>
      <App />
    </Router>
  </React.StrictMode>,
  document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
// reportWebVitals();
