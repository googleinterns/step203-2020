const sectionDealsData = [
  {
    restaurant: {'id': 1, 'name': 'A'},
    description: 'starbucks mocha 1-for-1',
    timestamp: '2020-07-10T10:15:30',
    votes: 0,
    id: 1,
    pic: 'a_blob_key',
    poster: {'id': 1, 'username': 'Alice'},
    tags: [{'id': 1, 'name': '1for1'}],
  },
  {
    restaurant: {'id': 1, 'name': 'A'},
    description: 'starbucks mocha 1-for-1',
    timestamp: '2020-07-10T10:15:30',
    votes: 0,
    id: 1,
    pic: 'a_blob_key',
    poster: {'id': 1, 'username': 'Alice'},
    tags: [{'id': 1, 'name': '1for1'}],
  },
  {
    restaurant: {'id': 1, 'name': 'A'},
    description: 'starbucks mocha 1-for-1',
    timestamp: '2020-07-10T10:15:30',
    votes: 0,
    id: 1,
    pic: 'a_blob_key',
    poster: {'id': 1, 'username': 'Alice'},
    tags: [{'id': 1, 'name': '1for1'}],
  },
];


/**
 * Creates deal elements on on view all deals for each section page
 * @param {object} deals
 * @param {object} reqSection
 */
function createAllDealCards(deals, reqSection) {
  const rowElements = document.querySelectorAll('.row.row-deals');
  for (let i = 0; i < rowElements.length; i++) {
    for (let j = 0; j < 4; j++) {
      if (i*4 + j < deals.length) {
        rowElements[i].appendChild(createHomeDealCard(deals[i*4 + j]));
      }
    }
  }
  const dropdownMenu = document.querySelector('.dropdown-sort');
  console.log('HERE');
  dropdownMenu.children[0].href = '/all-section-deals/' + reqSection + '/trending';
  dropdownMenu.children[1].href = '/all-section-deals/' + reqSection + '/votes';
  dropdownMenu.children[2].href = '/all-section-deals/' + reqSection + '/new';
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
