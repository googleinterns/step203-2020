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
  let reqSort = null;
  if (myPathElem.length == 4) {
    reqSort = myPathElem[3];
  }
  $.ajax({
    url: '/api/home',
    data: {
      section: reqSection,
      sort: reqSort,
    },
  }).done((deals) => {
    deals = sectionDealsData;
    createAllDealCards(deals, reqSection);
  },
  );
}

addLoadEvent(() => {
  initAllDeals();
});
