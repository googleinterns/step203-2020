/**
 * Creates carousel on home page
 * @param {number} numDealPerSlide
 * @param {object} homePageDeals
 */
function createHomePage(numDealPerSlide, homePageDeals) {
  $('.section-loading').hide();
  const carouselElements = document.querySelectorAll('.carousel.slide');
  const homePageSections = ['trending', 'restaurants',
    'users', 'tags'];
  // number of sections on homepage
  let i;
  for (i = 0; i < Object.keys(homePageDeals).length; i++) {
    const homePageData = homePageDeals[homePageSections[i]];
    if (homePageData.length == 0) { // if no data, set to no data found
      showNotFound(carouselElements[i], false);
      continue;
    }
    carouselElements[i].children[1].href = '/all-section-deals/' +
      homePageSections[i];
    carouselElements[i].id = 'carousel-' + i;
    const indicatorListElement = carouselElements[i].children[2];
    const carouselItemList = carouselElements[i].children[3];
    const numCarouselSlides = Math.ceil(homePageData.length/numDealPerSlide);
    for (let j = 0; j < numCarouselSlides; j++) { // number of carousel slides
      const indicatorListChild = document.createElement('li');
      indicatorListChild.dataset.target = '#carousel-' + i;
      indicatorListChild.setAttribute('data-slide-to', j);

      const carouselItemListChild = document.createElement('div');
      carouselItemListChild.classList.add('carousel-item');

      const rowElement = document.createElement('div');
      rowElement.className = 'row';
      const numCol = 12 / numDealPerSlide;
      for (let k = 0; k < numDealPerSlide; k++) {
        const indexOfData = j*numDealPerSlide+k;
        if (indexOfData < homePageData.length) {
          rowElement.appendChild(createHomeDealCard(
              homePageData[indexOfData], numCol, i));
        }
      }
      carouselItemListChild.append(rowElement);
      if (j == 0) {
        indicatorListChild.classList.add('active');
        carouselItemListChild.classList.add('active');
      }
      indicatorListElement.appendChild(indicatorListChild);
      carouselItemList.appendChild(carouselItemListChild);
    }
    carouselElements[i].children[4].href = '#carousel-' + i;
    carouselElements[i].children[5].href = '#carousel-' + i;
  }
  // for subsequent carousel elements, set to no display due to no data
  for (let k = i; k < carouselElements.length; k++) {
    showNotFound(carouselElements[k], true);
  }
}

/**
 * Creates carousel on home page
 * @param {HTMLDivElement} carouselElement
 * @param {boolean} notLoggedIn
 */
function showNotFound(carouselElement, notLoggedIn) {
  carouselElement.children[1].style.display = 'none';
  carouselElement.children[4].style.display = 'none';
  carouselElement.children[5].style.display = 'none';

  // inform users to log in to view
  const showNotFound = document.createElement('h5');
  showNotFound.className = 'text-center mt-5';
  if (notLoggedIn) {
    showNotFound.innerText = 'Please log in to view.';
  } else {
    showNotFound.innerText = 'No data found.';
  }
  carouselElement.appendChild(showNotFound);
}

/**
 * Calls backend for data on home page deals
 */
function initHomePage() {
  $.ajax('/api/home')
      .done((homePageDeals) => {
        createHomePage(4, homePageDeals);
      });
}

addLoadEvent(() => {
  initHomePage();
});
