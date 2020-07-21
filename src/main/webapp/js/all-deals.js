<<<<<<< HEAD
=======
function setDealInfo() {
  
}
>>>>>>> b0da71b... set to check length of data instead
/**
 * Loads the deals onto the page
 * @param {array} deals
 */
function loadDealDataToPage(deals) {
  $('#loading').hide();
  $('#list').show();

  const listDiv = document.getElementById('list');
  for (const deal of deals) {
    const card = createDealCard(deal);
    listDiv.appendChild(card);
  }
}

/**
 * Calls backend on data on deals
 */
function initDeals() {
  $.ajax('/api/deals')
      .done((deals) => {
        loadDealDataToPage(deals);
      });
}

addLoadEvent(() => {
  initDeals();
});
