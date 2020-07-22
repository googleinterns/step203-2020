const homePageDealsData = {
  trending: [
    {
      restaurant: 'A',
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      poster: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
    {
      restaurant: 'A',
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      poster: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
    {
      restaurant: 'A',
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      poster: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
  ],
};


/**
 * Creates deal elements on home page
 * @param {object} homePage
 */
function createHomePage(homePage) {
  const homePageSections = ['trending', 'restaurants',
    'users', 'tags'];
  for (let i = 0; i < Object.keys(homePage).length; i++) {
    const homePageData = homePage[homePageSections[i]];
    const dealCardId = 'deal-card-' + i;
    const dealCardElements =
      document.querySelectorAll('#' + dealCardId + '.deal-card');
    for (let j = 0; j < homePageData.length; j++) {
      const childElements = dealCardElements[j].children;
      const dealImage = childElements[0];
      dealImage.src = homePageData[j].pic;

      const dealBody = childElements[1];

      const dealTime = dealBody.children[0];
      dealTime.innerText = homePageData[j].timestamp;

      const dealVotes = dealBody.children[1].children[1];
      dealVotes.innerText = homePageData[j].votes;

      const dealTitle = dealBody.children[2];
      dealTitle.innerText = homePageData[j].description;

      const dealRestaurant = dealBody.children[3].children[0];
      dealRestaurant.innerText = homePageData[j].restaurant.name;
      dealRestaurant.href = '/restaurants/' + homePageData[j].restaurant.id;

      const dealPoster = dealBody.children[4].children[0];
      dealPoster.innerText = homePageData[j].poster.username;
      dealPoster.href = '/users/' + homePageData[j].poster.id;

      const dealTags = dealBody.children[5];
      const numTags = homePageData[j].tags.length;
      for (let i = 0; i < numTags; i++) {
        dealTags.innerText += '#' + homePageData[j].tags[i].name + ', ';
      };

      const dealLink = dealBody.children[6];
      dealLink.href = '/deals/' + homePageData[j].id;
    }
  }
}

/**
 * Creates carousel on home page
 * @param {object} numCarouselSlidesList
 * @param {object} numDealPerSlide
 * @param {object} homePageDeals
 */
function createCarouselElements(numCarouselSlidesList,
    numDealPerSlide, homePageDeals) {
  const carouselElements = document.querySelectorAll('.carousel.slide');
  const homePageSections = ['trending', 'restaurants',
    'users', 'tags'];

  // number of sections on homepage
  let i;
  for (i = 0; i < Object.keys(homePageDeals).length; i++) {
    carouselElements[i].children[0].href = '/all-section-deals/' + homePageSections[i];
    carouselElements[i].id = 'carousel-' + i;
    const indicatorListElement = carouselElements[i].children[1];
    const carouselItemList = carouselElements[i].children[2];
    const numCarouselSlides = numCarouselSlidesList[i];

    for (let j = 0; j < numCarouselSlides; j++) { // number of carousel slides
      const indicatorListChild = document.createElement('li');
      indicatorListChild.dataset.target = '#carousel-' + i;
      indicatorListChild.setAttribute('data-slide-to', j);

      const carouselItemListChild = document.createElement('div');
      carouselItemListChild.classList.add('carousel-item');

      const rowElement = document.createElement('div');
      rowElement.className = 'row';
      const numCol = 'col-md-' + 12 / numDealPerSlide;
      for (let k = 0; k < numDealPerSlide; k++) {
        rowElement.innerHTML += `
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
      carouselItemListChild.append(rowElement);
      if (j == 0) {
        indicatorListChild.classList.add('active');
        carouselItemListChild.classList.add('active');
      }
      indicatorListElement.appendChild(indicatorListChild);
      carouselItemList.appendChild(carouselItemListChild);
    }
    carouselElements[i].children[3].href = '#carousel-' + i;
    carouselElements[i].children[4].href = '#carousel-' + i;
  }
  // for subsequent carousel elements, set to no display due to no data
  for (let k = i; k < carouselElements.length; k++) {
    // set display to none if there is no data for that section
    carouselElements[k].children[0].style.display = 'none';
    carouselElements[k].children[3].style.display = 'none';
    carouselElements[k].children[4].style.display = 'none';

    // inform users to log in to view
    const showNotFound = document.createElement('h5');
    showNotFound.className = 'text-center mt-5';
    showNotFound.innerText = 'Please log in to view.';
    carouselElements[k].appendChild(showNotFound);
    continue;
  }
}

/**
 * Calls backend for data on home page deals
 */
function initHomePage() {
  $.ajax('/api/home')
      .done((homePageDeals) => {
        homePageDeals = homePageDealsData;
        console.log(homePageDeals);
        createCarouselElements([2, 2, 3, 3], 4, homePageDeals);
        createHomePage(homePageDeals);
      });
}

addLoadEvent(() => {
  initHomePage();
});
