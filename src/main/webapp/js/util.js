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
 * Returns a container for a deal.
 * @param {object} deal deal whose info will be shown.
 * @param {number} numCol the bootstrap width of each deal
 * @param {number} sectionId the sectionId of each deal
 * @return {HTMLDivElement} a DOM element showing deal's info.
 */
function createHomeDealCard(deal, numCol, sectionId) {
  const dealBody = document.createElement('div');
  dealBody.classList.add('col-md-'+numCol, 'mt-5');
  const date = new Date(Date.parse(deal.timestamp));
  dealBody.innerHTML += `
      <div id=deal-card-${sectionId} class="card deal-card h-100">
        <img class="card-img-top home-deal-img" src=${deal.pic} alt="">
        <div class="card-body d-flex flex-column">
        <div class="card-text float-right"><i class="fa fa-thumbs-up"></i>  
          ${deal.votes}</div>
          <div class="card-text">${date.toLocaleString()}</div>
          <div class="d-flex justify-content-end"></div>
          <h5 class="card-title">${deal.description}</h5>
          <div class="card-text">Restaurant: 
          <a href="/restaurant/${deal.restaurant.id}">
          ${deal.restaurant.name}</a></div>
          <div class="card-text">Posted by: 
          <a href="/user/${deal.poster.id}">${deal.poster.username}</a></div>
          <div class= "card-text tags" ></div>
          <a href="/deals/${deal.id}"
            class="btn btn-primary align-self-end mt-auto float-right">
            See More
          </a>
        </div>
      </div>`;

  const dealTags = dealBody.querySelector('.tags');
  for (let i = 0; i < deal.tags.length; i++) {
    dealTags.innerText += '#' + deal.tags[i].name + ', ';
  };

  return dealBody;
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
