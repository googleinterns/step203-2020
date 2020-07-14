const homePageData = {
  popularDeals: [
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
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
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
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
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
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
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
    },
    {
      restaurantName: 'A',
      restaurantId: 1,
      posterId: 1,
      description: 'starbucks mocha 1-for-1',
      votes: 0,
      id: 1,
      pic: 'a_blob_key',
      posterName: 'Alice',
      tags: [{'id': 1, 'name': '1for1'}],
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
      const dealTitle = dealBody.children[0];
      dealTitle.innerText = homePageData[j].description;
      const dealRestaurant = dealBody.children[2];
      dealRestaurant.innerText = homePageData[j].restaurantName;
      dealRestaurant.href = '/restaurants/' + homePageData[j].restaurantId;
      const dealPoster = dealBody.children[3];
      dealPoster.innerText = homePageData[j].posterName;
      dealPoster.href = '/users/' + homePageData[j].posterId;
      const dealLink = dealBody.children[4];
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
                <h5 class="card-title deal-title"></h5>
                  <p class="card-text deal-text"></p>
                  <a href='#' class="card-text deal-restaurant"></a>
                  <a href='#' class="card-text deal-poster"></a>
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
        console.log(homePageDeals);
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
