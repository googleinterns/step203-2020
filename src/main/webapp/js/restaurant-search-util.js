/**
 * Calls backend for retaurant based on text in restaurant search, and displays
 * results.
 * @param {string} query
 * @param {HTMLDivElement} searchResults the DOM element of the search results
 * div
 * @param {function} callback callback when user selects a restaurant
 */
function searchRestaurant(query, searchResults, callback) {
  if (query === '') {
    searchResults.style.display = 'none';
    return;
  }
  $.ajax({
    url: '/api/search/restaurants',
    data: {
      query: query,
    },
  }).done((restaurants) => {
    searchResults.innerHTML = '';
    if (restaurants.length == 0) {
      const row = document.createElement('div');
      row.className = 'd-flex align-items-center search-menu-item p-2';
      row.innerHTML = 'No results';
      searchResults.appendChild(row);
    } else {
      restaurants.forEach((restaurant) => {
        const row = document.createElement('div');
        row.className = 'd-flex align-items-center search-menu-item p-2';
        row.innerHTML = `
            <span class="flex-grow-1">${restaurant.name}</span>
            <img class="search-menu-pic" src="${restaurant.image}">
          `;
        row.onmousedown = () => callback(restaurant);
        searchResults.appendChild(row);
      });
    }
    searchResults.style.display = 'block';
  });
}

/**
 * Initialize the restaurant search logic
 * @param {HTMLDivElement} searchDiv A DOM element to contain the search logic
 * @param {function} callback Callback with selected restaurant
 */
function initSearchRestaurant(searchDiv, callback) {
  const searchInputDiv = document.createElement('div');
  searchInputDiv.className = 'd-flex';
  searchInputDiv.innerHTML = `
    <i class="fa fa-search mr-2"></i>
    <input autocomplete="off" type="search"
      placeholder="Search for a restaurant..."
      class="flex-grow-1"
      style="outline: 0;border-width: 0 0 2px;">
  `;
  const searchInput = searchInputDiv.querySelector('input');
  searchDiv.appendChild(searchInputDiv);

  const searchMenuContainer = document.createElement('div');
  searchMenuContainer.className = 'search-menu-container';
  const searchResults = document.createElement('div');
  searchResults.className = 'search-menu';
  searchMenuContainer.appendChild(searchResults);
  searchDiv.appendChild(searchMenuContainer);

  /*
   * Handles restaurant results when the search bar is typed
   */
  searchRestaurantThrottle = throttle(() => {
    searchRestaurant(searchInput.value, searchResults, callback);
  }, 1000);
  $(searchInput).keyup(function() {
    searchRestaurantThrottle();
  });
  searchResults.style.display = 'none';
  $(searchInput).focus(function() {
    searchRestaurantThrottle();
  });
  $(searchInput).blur(function() {
    searchResults.style.display = 'none';
  });
}
