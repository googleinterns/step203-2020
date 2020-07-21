function setDealInfo() {
  
}
/**
 * Creates deal elements on on view all deals page
 */
function createAllDealCards() {
  const numCol = 'col-md-3';
  const rowElements = document.querySelectorAll('.row.row-deals');
  for (let i = 0; i < rowElements.length; i++) {
    for (let j = 0; j < 4; j++) {
      rowElements[i].innerHTML += `
          <div class="${numCol} mt-5">
            <div id=deal-card-${i} class="card deal-card h-100">
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
          </div>`;
    }
  }
}

/**
 * Calls backend for data on home page deals
 */
function initAllDeals() {
  const myPath = window.location.pathname; // path is /all-deals/*
  const reqSection = myPath.substr(11);
  $.ajax({
    url: '/api/home',
    data: {
      section: reqSection,
    },
  }).done((deals) => {
    createAllDealCards();
    setDealInfo(deals);
  },
  );
}

addLoadEvent(() => {
  initAllDeals();
});
