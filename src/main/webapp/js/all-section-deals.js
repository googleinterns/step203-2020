/**
 * Creates deal elements on on view all deals for each section page
 * @param {object} deals
 */
function createAllDealCards(deals) {
  $('#deals-loading').hide();
  const rowElements = document.querySelectorAll('.row.row-deals');
  for (let i = 0; i < rowElements.length; i++) {
    for (let j = 0; j < 4; j++) {
      const indexOfData = i*4 + j;
      if (indexOfData < deals.length) {
        rowElements[i].appendChild(createHomeDealCard(deals[i*4 + j], 3));
      }
    }
  }
}

/**
 * Calls backend for data on home page deals
 */
function initAllDeals() {
  const myPath = window.location.pathname; // path is /all-section-deals/*
  const reqSection = myPath.substr(19);
  $.ajax({
    url: '/api/home',
    data: {
      section: reqSection,
    },
  }).done((deals) => {
    createAllDealCards(deals);
  });
}

addLoadEvent(() => {
  initAllDeals();
});
