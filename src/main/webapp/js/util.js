/* eslint-disable no-unused-vars */
/**
 * Adds a function to window onload event.
 * @param {function} func The function to be executed.
 */
function addLoadEvent(func) {
  const oldonload = window.onload;
  if (typeof oldonload == 'function') {
    window.onload = function() {
      oldonload();
      func();
    };
  } else {
    window.onload = func;
  }
}

/**
 * Configures login status in header.
 */
function configureHeaderLoginStatus() {
  $.ajax('/api/authentication')
      .done((loginStatus) => {
        const loginStatusLink = document.getElementById('login-status-link');
        const headerUsername = document.getElementById('header-username');
        if (loginStatus.isLoggedIn) {
          loginStatusLink.href = loginStatus.logoutUrl;
          loginStatusLink.innerText = 'Logout';
          headerUsername.innerText = loginStatus.username;
          headerUsername.href = '/user/' + loginStatus.id;
        } else {
          loginStatusLink.href = loginStatus.loginUrl;
          loginStatusLink.innerText = 'Login';
          headerUsername.innerText = '';
        }
      });
}

addLoadEvent(configureHeaderLoginStatus);
