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
/*
<div class="${numCol} mt-5">
            <div class="card deal-card h-100">
              <img class="card-img-top home-deal-img" src="" alt="">
              <div class="card-body d-flex flex-column">
                <div class="card-text deal-time"></div>
                <div class="d-flex justify-content-end"
                  style="display: none;">
                  <button type="button" class="btn upvote-btn">
                    <span class="fas fa-angle-up"></span>
                  </button>
                  <span class="my-auto votes-num"></span>
                  <button type="button" class="btn downvote-btn">
                    <span class="fas fa-angle-down"></span>
                  </button>
                </div>
                <h5 class="card-title deal-title"></h5>
                <div>Restaurant: <a href='#' class="card-text deal-restaurant">
                </a></div>
                <div>Posted by: <a href='#' class="card-text deal-poster"></a>
                </div>
                <div class= "card-text tags" ></div>
                <a href="#"
                  class="btn btn-primary align-self-end mt-auto float-right">
                  See More
                </a>
              </div>
            </div>
          </div>`;*/
/**
 * Returns a container for a deal.
 * @param {object} deal deal whose info will be shown.
 * @return {object} a DOM element showing deal's info.
 */
function createHomeDealCard(deal) {
  const dealCardCol = document.createElement('div');
  dealCardCol.className = 'col-md-3';

  const dealCard = document.createElement('div');
  dealCard.classList.add('deal-card', 'card', 'h-100');

  const dealImage = document.createElement('img');
  dealImage.className = 'card-img-top home-deal-img';

  const dealBody = document.createElement('div');
  dealBody.classList.add('card-body', 'd-flex', 'flex-column');

  const dealTime = document.createElement('div');
  dealTime.classList.add('card-text');
  dealTime.innerText = deal.timestamp;

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
  dealPosterLink.href = '/users/' + deal.poster.id;

  dealPoster.appendChild(dealPosterLink);

  const dealTags = document.createElement('div');

  for (let i = 0; i < deal.tags.length; i++) {
    dealTags.innerText += '#' + deal.tags[i].name + ', ';
  };

  const dealSeeMore = document.createElement('a');
  dealSeeMore.href = '/deals/' + deal.id;
  dealSeeMore.innerText = 'See More';
  dealSeeMore.classList.add('btn', 'btn-primary', 'align-self-end', 'mt-auto', 'float-right');

  dealBody.appendChild(dealTime);
  dealBody.appendChild(dealName);
  dealBody.appendChild(dealRestaurant);
  dealBody.appendChild(dealPoster);
  dealBody.appendChild(dealTags);
  dealBody.appendChild(dealSeeMore);

  dealCard.appendChild(dealImage);
  dealCard.appendChild(dealBody);

  dealCardCol.appendChild(dealCard);

  return dealCardCol;

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
