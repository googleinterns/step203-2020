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
  $.ajax('/api/authentication', {
    data: {
      target: location.pathname,
    },
  }).done((loginStatus) => {
    const loginLink = document.getElementById('login-link');
    const headerUsername = document.getElementById('header-username');
    const usernameDropdown = document.getElementById('username-dropdown');
    if (loginStatus.isLoggedIn) {
      loginLink.hidden = true;
      usernameDropdown.hidden = false;
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

/**
 * Returns a container for a deal.
 * @param {object} deal deal whose info will be shown.
 * @return {object} a DOM element showing deal's info.
 */
function createDealCard(deal) {
  const dealCard = document.createElement('div');
  dealCard.classList.add('deal-card', 'card');
  const dealImage = document.createElement('img');
  dealImage.className = 'card-img-top deal-card-img';
  dealImage.src = deal.image;
  dealImage.alt = 'Deal image';

  const dealBody = document.createElement('div');
  dealBody.className = 'card-body';

  const dealName = document.createElement('h6');
  dealName.className = 'card-title';
  dealName.innerText = deal.description;

  const dealLink = document.createElement('a');
  dealLink.innerText = 'See detail';
  dealLink.href = '/deals/' + deal.id;

  dealBody.appendChild(dealName);
  dealBody.appendChild(dealLink);

  dealCard.appendChild(dealImage);
  dealCard.appendChild(dealBody);
  return dealCard;
}

/**
 * Throttles a function. The function can be called at most once in limit
 * milliseconds.
 * @param {function} func
 * @param {number} limit - The limit of the function in milliseconds
 * @return {function}
 */
function throttle(func, limit) {
  let lastFunc;
  let lastRan;
  return function(...args) {
    if (!lastRan) {
      func(...args);
      lastRan = Date.now();
    } else {
      clearTimeout(lastFunc);
      lastFunc = setTimeout(function() {
        if ((Date.now() - lastRan) >= limit) {
          func(...args);
          lastRan = Date.now();
        }
      }, limit - (Date.now() - lastRan));
    }
  };
}

/**
 * Escapes HTML string
 * @param {string} unsafe unsafe string that might contain HTML elements
 * @return {string} safe HTML string
 */
function escapeHtml(unsafe) {
  const text = document.createTextNode(unsafe);
  const p = document.createElement('p');
  p.appendChild(text);
  return p.innerHTML;
}
