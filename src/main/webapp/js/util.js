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
  const dealCardCol = document.createElement('div');
  dealCardCol.className = 'col-md-' + numCol;

  const dealCard = document.createElement('div');
  dealCard.classList.add('deal-card', 'card', 'h-100');
  dealCard.id = 'deal-card-' + sectionId;

  const dealImage = document.createElement('img');
  dealImage.className = 'card-img-top home-deal-img';

  const dealBody = document.createElement('div');
  dealBody.classList.add('card-body', 'd-flex', 'flex-column');

  const dealTime = document.createElement('div');
  dealTime.classList.add('card-text');
  dealTime.innerText = deal.timestamp;

  const dealVotes = document.createElement('div');
  dealTime.classList.add('card-text');
  dealVotes.innerText = 'Votes: ' + deal.votes;

  const dealName = document.createElement('h5');
  dealName.className = 'card-title';
  dealName.innerText = deal.description;

  const dealRestaurant = document.createElement('div');
  dealRestaurant.innerText = 'Restaurant: ' + deal.restaurant.name;

  const dealRestaurantLink = document.createElement('a');
  dealRestaurantLink.href = '/restaurants/' + deal.restaurant.id;

  dealRestaurant.appendChild(dealRestaurantLink);

  const dealPoster = document.createElement('div');
  dealPoster.innerText = 'Poster: ' + deal.poster.username;

  const dealPosterLink = document.createElement('a');
  dealPosterLink.href = '/user/' + deal.poster.id;

  dealPoster.appendChild(dealPosterLink);

  const dealTags = document.createElement('div');

  for (let i = 0; i < deal.tags.length; i++) {
    dealTags.innerText += '#' + deal.tags[i].name + ', ';
  };

  const dealSeeMore = document.createElement('a');
  dealSeeMore.href = '/deals/' + deal.id;
  dealSeeMore.innerText = 'See More';
  dealSeeMore.classList.add('btn', 'btn-primary', 'align-self-end',
      'mt-auto', 'float-right');

  dealBody.appendChild(dealTime);
  dealBody.appendChild(dealVotes);
  dealBody.appendChild(dealName);
  dealBody.appendChild(dealRestaurant);
  dealBody.appendChild(dealPoster);
  dealBody.appendChild(dealTags);
  dealBody.appendChild(dealSeeMore);

  dealCard.appendChild(dealImage);
  dealCard.appendChild(dealBody);

  dealCardCol.appendChild(dealCard);

  return dealCardCol;
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
