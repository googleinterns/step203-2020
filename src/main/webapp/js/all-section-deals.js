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

addLoadEvent(() => {
  initAllDeals();
});
