const homePage = {
  'Popular Deals': [
    {
      'id': 1234,
      'name': 'Bubble Tea',
      'votes': 5,
      'poster': 'abc',
      'image': 'assets/deals/bubble-tea.jpeg',
    },
    {
      'id': 2345,
      'name': 'Dessert',
      'votes': 5,
      'poster': 'bcd',
      'image': 'assets/deals/dessert.jpeg',
    },
    {
      'id': 3456,
      'name': 'KFC',
      'votes': 5,
      'poster': 'bcd',
      'image': 'assets/deals/kfc.jpeg',
    },
    {
      'id': 5678,
      'name': 'Pizza',
      'votes': 5,
      'poster': 'bcd',
      'image': 'assets/deals/pizza.jpeg',
    },
  ],
  'Restaurants I Follow': [
    {
      'id': 1234,
      'name': 'Starbucks Mocha 1-for-1',
      'votes': 5,
      'poster': 'def',
      'image': 'assets/deals/bubble-tea.jpeg',
    },
  ],
  'Users I Follow': [
    {
      'id': 1234,
      'name': 'Starbucks Mocha 1-for-1',
      'votes': 5,
      'poster': 'Starbucks',
      'image': 'assets/deals/bubble-tea.jpeg',
    },
  ],
  'Tags I Follow': [
    {
      'id': 1234,
      'name': 'Starbucks Mocha 1-for-1',
      'votes': 5,
      'poster': 'def',
      'tags': ['coffee', '1-for-1'],
      'image': 'assets/deals/bubble-tea.jpeg',
    },
  ],
};

document.addEventListener('DOMContentLoaded', () => {
  createCarouselElements([2, 2, 3, 3], 4);
  createHomePage(homePage);
});

/**
 * Creates deal elements on home page
 * @param {object} homePage
 */
function createHomePage(homePage) {
  const homePageSections = ['Popular Deals', 'Restaurants I Follow',
    'Users I Follow', 'Tags I Follow'];
  const carouselElements = document.querySelectorAll('.carousel.slide');
  for (let i = 0; i < carouselElements.length; i++) {
    const homePageData = homePage[homePageSections[i]];
    const dealCardId = 'deal-card-'+i;
    const dealCardElements =
      document.querySelectorAll('#'+dealCardId+'.deal-card');
    for (let j = 0; j < homePageData.length; j++) {
      const childElements = dealCardElements[j].children;
      const dealImage = childElements[0];
      dealImage.src = homePageData[j].image;
      const dealBody = childElements[1];
      const dealTitle = dealBody.children[0];
      dealTitle.innerText = homePageData[j].name;
      const dealPoster = dealBody.children[2];
      dealPoster.innerText = homePageData[j].poster;
      const dealLink = dealBody.children[3];
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
      indicatorListChild.dataset.target = '#carousel-' +i;
      indicatorListChild.setAttribute('data-slide-to', j);
      const carouselItemListChild = document.createElement('div');
      carouselItemListChild.classList.add('carousel-item');
      const rowElement = document.createElement('div');
      rowElement.className='row';
      const numCol = 'col-md-' + 12/numDealPerSlide;
      for (let k = 0; k < numDealPerSlide; k++) {
        rowElement.innerHTML += `
          <div class="${numCol} mt-5">
            <div id=deal-card-${i} class="card deal-card h-100">
              <img class="card-img-top home-deal-img" src="" alt="">
              <div class="card-body d-flex flex-column">
                <h5 class="card-title deal-title"></h5>
                   <p class="card-text deal-text"></p>
                   <p class="card-text deal-poster"></p>
                   <a href="#"
                   class="btn btn-primary align-self-end mt-auto float-right">
                   See More
                    </a>
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
    carouselElements[i].children[2].href='#carousel-'+i;
    carouselElements[i].children[3].href='#carousel-'+i;
  }
}
