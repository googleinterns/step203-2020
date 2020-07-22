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
 */
function createAllDealCards(deals) {
  const rowElements = document.querySelectorAll('.row.row-deals');
  for (let i = 0; i < rowElements.length; i++) {
    for (let j = 0; j < 4; j++) {
      rowElements[i].appendChild(createHomeDealCard(deals[i*4 + j]));
    }
  }
}

/**
 * Calls backend for data on home page deals
 */
function initAllDeals() {
  const myPath = window.location.pathname; // path is /all-section-deals/*
  const reqSection = myPath.substr(19);
  console.log(reqSection);
  $.ajax({
    url: '/api/home',
    data: {
      section: reqSection,
    },
  }).done((deals) => {
    deals = sectionDealsData;
    console.log(deals);
    createAllDealCards(deals);
    setDealInfo(deals);
  },
  );
}

addLoadEvent(() => {
  initAllDeals();
});
