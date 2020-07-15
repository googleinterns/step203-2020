const homePageData = {
  popularDeals: [
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 1,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
  ],
  usersIFollow: [
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
  ],
  restaurantsIFollow: [
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
  ],
  tagsIFollow: [
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: '/api/images/a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
      timestamp: '2020-07-10T10:15:30',
    },
  ],
};
/**
 * Creates deal elements on home page
 * @param {object} homePage
 */
function createHomePage(homePage) {
  const homePageSections = ['popularDeals', 'restaurantsIFollow',
    'usersIFollow', 'tagsIFollow'];
  const carouselElements = document.querySelectorAll('.carousel.slide');
  for (let i = 0; i < carouselElements.length; i++) {
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
      dealRestaurant.innerText = homePageData[j].restaurantName;
      dealRestaurant.href = '/restaurants/' + homePageData[j].restaurantId;

      const dealPoster = dealBody.children[4].children[0];
      dealPoster.innerText = homePageData[j].posterName;
      dealPoster.href = '/users/' + homePageData[j].posterId;

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
 */
function createCarouselElements(numCarouselSlidesList, numDealPerSlide) {
  const carouselElements = document.querySelectorAll('.carousel.slide');
  // number of sections on homepage
  for (let i = 0; i < carouselElements.length; i++) {
    carouselElements[i].id = 'carousel-' + i;
    const indicatorListElement = carouselElements[i].children[0];
    const carouselItemList = carouselElements[i].children[1];
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
    carouselElements[i].children[2].href = '#carousel-' + i;
    carouselElements[i].children[3].href = '#carousel-' + i;
  }
}

/**
 * Calls backend for data on home page deals
 */
function initHomePage() {
  $.ajax('/api/home')
      .done((homePageDeals) => {
        homePageDeals = homePageData;
        createCarouselElements([2, 2, 3, 3], 4);
        createHomePage(homePageDeals);
      })
      .fail(() => {
        showNotFound();
      });
}

addLoadEvent(() => {
  initHomePage();
});
