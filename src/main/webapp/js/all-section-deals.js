/**
 * Creates deal elements on on view all deals for each section page
 * @param {object} deals
 * @param {object} reqSection
 */
function createAllDealCards(deals, reqSection) {
  const rowElements = document.querySelectorAll('.row.row-deals');
  for (let i = 0; i < rowElements.length; i++) {
    for (let j = 0; j < 4; j++) {
      const indexOfData = i*4 + j;
      if (indexOfData < deals.length) {
        rowElements[i].appendChild(createHomeDealCard(deals[i*4 + j], 3));
      }
    }
  }
  const dropdownMenu = document.getElementById('sort');
  // for trending section, the deals are already sorted by trending,
  // so no sorting option is available
  if (reqSection !== 'trending') {
    dropdownMenu.children[0].href = '/all-section-deals/' +
      reqSection + '/trending';
    dropdownMenu.children[1].href = '/all-section-deals/' +
      reqSection + '/votes';
    dropdownMenu.children[2].href = '/all-section-deals/' +
      reqSection + '/new';
  } else {
    const dropdownSection = document.querySelector('.dropdown-sort');
    dropdownSection.style.display = 'none';
  }
}

/**
 * Calls backend for data on home page deals
 */
function initAllDeals() {
  const myPath = window.location.pathname; // path is /all-section-deals/*
  const myPathElem = myPath.split('/');
  const reqSection = myPathElem[2];
  const location = new URLSearchParams(window.location.search);
  const latitude = location.get('latitude');
  const longitude = location.get('longitude');
  let reqSort = null;
  if (myPathElem.length == 4) {
    reqSort = myPathElem[3];
  }
  if (reqSort == null) {
    $.ajax({
      url: '/api/home',
      data: {
        section: reqSection,
      },
    }).done((deals) => {
      createAllDealCards(deals, reqSection);
    });
  } else if (latitude != null && longitude != null) {
    $.ajax({
      url: '/api/home',
      data: {
        section: reqSection,
        sort: reqSort,
        latitude: latitude,
        longitude: longitude,
      },
    }).done((deals) => {
      createAllDealCards(deals, reqSection);
    });
  } else {
    $.ajax({
      url: '/api/home',
      data: {
        section: reqSection,
        sort: reqSort,
      },
    }).done((deals) => {
      createAllDealCards(deals, reqSection);
    });
  }
}

/**
 * Retrieves user's location
 */
function getLocation() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(setNewURL, showError);
  } else {
    alert('Geolocation is not supported by this browser.');
  }
}

/**
 * Sets the url to have distance in path info and location as parameters
 * @param {object} position user position (latitude and longitude)
 */
function setNewURL(position) {
  const latitude = position.coords.latitude;
  const longitude = position.coords.longitude;
  const url = new URL(window.location.href);
  if (window.location.href.split('/').length == 5) {
    url.href += '/distance';
  } else if (window.location.href.split('/').length == 6) {
    const myPathElem = window.location.href.split('/');
    myPathElem[5] = 'distance';
    url.href = myPathElem.join('/');
  }
  const params = new URLSearchParams(url.search.slice(1));
  if (!params.has('longitude') && !params.has('latitude')) {
    url.searchParams.append('latitude', latitude);
    url.searchParams.append('longitude', longitude);
  }
  window.location = url.href;
}

/**
 * Shows the error code
 * @param {object} error the error when getting user location
 */
function showError(error) {
  switch (error.code) {
    case error.PERMISSION_DENIED:
      alert('Request for Geolocation denied. Unable to sort by distance.');
      break;
    case error.POSITION_UNAVAILABLE:
      alert('Location information is unavailable.');
      break;
    case error.TIMEOUT:
      alert('The request to get user location timed out.');
      break;
    case error.UNKNOWN_ERROR:
      alert('An unknown error occurred.');
      break;
  }
}

addLoadEvent(() => {
  initAllDeals();
});
