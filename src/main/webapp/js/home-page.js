/**
 * Creates carousel on home page
 * @param {object} numCarouselSlidesList
 * @param {object} numDealPerSlide
 * @param {object} homePageDeals
 */
function createHomePage(numCarouselSlidesList,
    numDealPerSlide, homePageDeals) {
  const carouselElements = document.querySelectorAll('.carousel.slide');
  const homePageSections = ['trending', 'restaurants',
    'users', 'tags'];
  // number of sections on homepage
  let i;
  for (i = 0; i < Object.keys(homePageDeals).length; i++) {
    carouselElements[i].children[0].href = '/all-section-deals/' +
      homePageSections[i];
    carouselElements[i].id = 'carousel-' + i;
    const indicatorListElement = carouselElements[i].children[1];
    const carouselItemList = carouselElements[i].children[2];
    const numCarouselSlides = numCarouselSlidesList[i];
    const homePageData = homePageDeals[homePageSections[i]];
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
        createHomePage([2, 2, 3, 3], 4, homePageDeals);
      });
}

addLoadEvent(() => {
  initHomePage();
});
