import logo from "../Images/dira_icon_cropped.png"
import { useEffect, useState } from "react";
import { useHistory } from "react-router-dom";



const ChangePassword = ({ userClientRef, navHandle, username, doLogout }) => {
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const [error, setError] = useState(false);
  const [errMessage, setErrMessage] = useState('');

  const history = useHistory();
  useEffect(navHandle, [navHandle]);

  const handlePassChange = (e) => {
    e.preventDefault();

    if (!(currentPassword && newPassword && confirmPassword)) {
      setError(true);
      setErrMessage('Please fill in all fields');
      return;
    }
    else if (newPassword !== confirmPassword) {
      setError(true);
      setErrMessage('Passwords don\'t match');
      return;
    }
    setError(false);

    userClientRef.current.change_password({
      username,
      currentPassword,
      newPassword
    }).then(res => {
      doLogout();
    }).catch(err => {
      console.log(err);
      setError(true);
      try {
        setErrMessage(err.error.message);
      }
      catch {
        setErrMessage('Couldn\'t update password');
      }
    })
  };

  return (
    <div className="login">
      <div style={{ textAlign: "center" }}>
        <img src={logo} alt="dira logo" id="dira logo" onClick={() => history.push('/')} />
        <div className="login_grad" style={{ textAlign: "center" }}>
          <h1 style={{ fontWeight: "normal", marginBottom: "40px" }}>Change Password</h1>
          <div>
            <form onSubmit={handlePassChange} noValidate>

              <p className="inputHead" style={{ textAlign: 'left' }}>Current Password:</p>
              <input type="password"
                placeholder="Current Password"
                value={currentPassword} onChange={(e) => { setCurrentPassword(e.target.value); }}
              >

              </input>
              <p className="inputHead" style={{ textAlign: 'left' }}>New password:</p>
              <input type="password"
                placeholder="New Password"
                value={newPassword} onChange={(e) => { setNewPassword(e.target.value); }}
              >

              </input>
              <p className="inputHead" style={{ textAlign: 'left' }}>Confirm New Password:</p>
              <input type="password"
                placeholder="Confirm New Password"
                value={confirmPassword} onChange={(e) => { setConfirmPassword(e.target.value); }}
              >
              </input>
              {
                error
                &&
                <ul>
                  {
                    errMessage.split('|').map((message, index) => <li
                      key={index}
                      style={{ "color": "crimson" }}
                    >
                      {message}
                    </li>)
                  }
                </ul>
              }
              <p><button type="submit">Update Password</button></p>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ChangePassword;
