function addLoadEvent(func) {
	var oldonload = window.onload;
	if(typeof oldonload == "function") {
		window.onload = function() {
			oldonload();
			func();
		}
	} else {
		window.onload = func;
    }
}

function configureHeaderLoginStatus() {
	fetch("/api/authentication")
	  .then(response => response.json())
	  .then(loginStatus => {
		const loginStatusLink = document.getElementById('login-status-link');
		const headerUsername = document.getElementById('header-username');
		if (loginStatus.isLoggedIn) {
		  loginStatusLink.href = loginStatus.logoutUrl;
		  loginStatusLink.innerText = 'Logout';
		  headerUsername.innerText = loginStatus.userEmail;
		  headerUsername.href = '/profile';
		} else {
		  loginStatusLink.href = loginStatus.loginUrl;
		  loginStatusLink.innerText = 'Login';
		  headerUsername.innerText = '';
		}
	  });
}
  
addLoadEvent(configureHeaderLoginStatus);
