/**
 * Loads the restaurants onto the page
 * @param {array} restaurants
 */
function loadRestaurantDataToPage(restaurants) {
  $('#loading').hide();
  $('#list').show();

  const listDiv = document.getElementById('list');
  for (const restaurant of restaurants) {
    const card = createRestaurantCard(restaurant);
    listDiv.appendChild(card);
  }
}

/**
 * Returns a container for a restaurant.
 * @param {object} restaurant restaurant whose info will be shown.
 * @return {HTMLDivElement} a DOM element showing restaurant's info.
 */
function createRestaurantCard(restaurant) {
  const restaurantCard = document.createElement('div');
  restaurantCard.className = 'card';
  const restaurantImage = document.createElement('img');
  restaurantImage.className = 'card-img-top restaurant-card-img';
  restaurantImage.src = restaurant.photoUrl;
  restaurantImage.alt = 'Restaurant image';

  const restaurantBody = document.createElement('div');
  restaurantBody.className = 'card-body';

  const restaurantName = document.createElement('h6');
  restaurantName.className = 'card-title';
  restaurantName.innerText = restaurant.name;

  const restaurantLink = document.createElement('a');
  restaurantLink.innerText = 'See detail';
  restaurantLink.href = '/restaurant/' + restaurant.id;

  restaurantBody.appendChild(restaurantName);
  restaurantBody.appendChild(restaurantLink);

  restaurantCard.appendChild(restaurantImage);
  restaurantCard.appendChild(restaurantBody);
  return restaurantCard;
}

/**
 * Calls backend on data on restaurants
 */
function initRestaurants() {
  $.ajax('/api/restaurants')
      .done((restaurants) => {
        loadRestaurantDataToPage(restaurants);
      });
}

addLoadEvent(() => {
  initRestaurants();
});

