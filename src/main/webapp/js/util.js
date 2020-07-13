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
  fetch('/api/authentication')
      .then((response) => response.json())
      .then((loginStatus) => {
        const loginLink = document.getElementById('login-link');
        const usernameDropdown = document.getElementById('username-dropdown');
        if (loginStatus.isLoggedIn) {
          loginLink.hidden = true;
          usernameDropdown.hidden = false;
          const headerUsername = document.getElementById('header-username');
          headerUsername.innerHTML =
              '<i class="fa fa-user-circle"></i> '+ loginStatus.username;
          const profileLink = document.getElementById('header-profile-link');
          profileLink.href = '/user/' + loginStatus.id;
          const logoutLink = document.getElementById('logout-link');
          logoutLink.href = loginStatus.logoutUrl;
        } else {
          loginLink.hidden = false;
          usernameDropdown.hidden = true;
          loginLink.href = loginStatus.loginUrl;
          headerUsername.hidden = true;
        }
      });
}

addLoadEvent(configureHeaderLoginStatus);
