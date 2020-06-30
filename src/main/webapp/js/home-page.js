const homePage = {
  'Popular Deals': [
    {
      'id': 1234,
      'name': 'Bubble Tea',
      'votes': 5,
      'poster': 'abc',
    },
    {
      'id': 2345,
      'name': 'Dessert',
      'votes': 5,
      'poster': 'bcd',
    },
    {
      'id': 3456,
      'name': 'KFC',
      'votes': 5,
      'poster': 'bcd',
    },
    {
      'id': 5678,
      'name': 'Pizza',
      'votes': 5,
      'poster': 'bcd',
    },
  ],
  'Restaurant I Follow': [
    {
      'id': 1234,
      'name': 'Starbucks Mocha 1-for-1',
      'votes': 5,
      'poster': 'def',
    },
  ],
  'Users I Follow': [
    {
      'id': 1234,
      'name': 'Starbucks Mocha 1-for-1',
      'votes': 5,
      'poster': 'Starbucks',
    },
  ],
  'Tags I Follow': [
    {
      'id': 1234,
      'name': 'Starbucks Mocha 1-for-1',
      'votes': 5,
      'poster': 'def',
      'tags': ['coffee', '1-for-1'],
    },
  ],
};

document.addEventListener('DOMContentLoaded', () => {
  createCarousel();
  createHomePage(homePage);
});

/**
 * Creates deal elements on home page
 * @param {object} homePage
 */
function createHomePage(homePage) {
  const popularDealsData = homePage['Popular Deals'];
  const dealCardElements = document.querySelectorAll('.deal-card');
  for (let i = 0; i < dealCardElements.length; i++) {
    const childElements = dealCardElements[i].children;
    const dealBody = childElements[1];
    const dealTitle = dealBody.children[0];
    dealTitle.innerText = popularDealsData[i].name;
    // var dealText = dealBody.childNodes[1];
    // dealText = homePage["PopularDeals"].
    const dealPoster = dealBody.children[2];
    dealPoster.innerText = popularDealsData[i].poster;
    const dealLink = dealBody.children[3];
    dealLink.href = '/deals/' + popularDealsData[i].id;
  }
}

/**
 * Creates carousel on home page
 * @param {object} homePage
 */
function createCarousel() {
  const carouselElements = document.querySelectorAll('.carousel.slide');
  for (let i = 0; i < carouselElements.length; i++) {
    carouselElements[i].id = 'carousel-' + i;
    console.log(carouselElements[i].id);
    const indicatorListElement = carouselElements[i].children[0];
    indicatorListElement.children[0].dataset.target = '#carousel-' +i;
    indicatorListElement.children[1].dataset.target = '#carousel-'+i;
    carouselElements[i].children[2].href='#carousel-'+i;
    carouselElements[i].children[3].href='#carousel-'+i;
  }
}
