/**
 * Calls backend on data on deals
 */
function initDeals() {
  $.ajax('/api/deals')
      .done((deals) => {
        console.log(deals);
      });
}

addLoadEvent(() => {
  initDeals();
});
