/**
 * Configures restaurant name and photo.
 * @param {Object} restaurant restaurant object
 */
function configureRestaurantInfo(restaurant) {
  const restaurantName = document.getElementById('restaurant-name');
  restaurantName.innerText = restaurant.name;
  const restaurantPhoto = document.getElementById('restaurant-photo');
  restaurantPhoto.src = restaurant.photoUrl;
}

/**
 * Configures follow button.
 * @param {number} restaurantId id of the restaurant to be followed or
 *        unfollowed
 * @param {number} loggedInUserId id of the logged in user
 */
function configureFollowButton(restaurantId, loggedInUserId) {
  const followButton = document.getElementById('follow-btn');
  $.ajax('/api/follows/restaurants/' + restaurantId, {
    data: {
      followerId: loggedInUserId,
    },
  }).done((isFollowing) => {
    if (isFollowing.startsWith('true')) {
      followButton.innerText = 'Unfollow';
      followButton.onclick = () => unfollow(restaurantId);
    } else {
      followButton.innerText = 'Follow';
      followButton.onclick = () => follow(restaurantId);
    }
    followButton.hidden = false;
  });
}

/**
 * Follows a restaurant and reloads the page.
 * @param {number} restaurantId id of the restaurant to be followed.
 */
function follow(restaurantId) {
  $.ajax('/api/follows/restaurants/' + restaurantId,
      {method: 'POST'})
      .done(() => location.reload());
}

/**
 * Unfollows a user and reloads the page.
 * @param {number} restaurantId the id of restaurant to be unfollowed.
 */
function unfollow(restaurantId) {
  $.ajax('/api/follows/restaurants/' + restaurantId,
      {method: 'DELETE'})
      .done(() => location.reload());
}

/**
 * Initializes the restaurant map.
 * @param {object} restaurant restaurant object.
 */
function initMap(restaurant) {
  const center = {lat: 1.352, lng: 103.8198};
  const map = new google.maps.Map(document.getElementById('restaurant-map'),
      {zoom: 13, center: center});
  setRestaurantMarkers(restaurant.placeIds, map);
}

/**
 * Sets markers on the map.
 * @param {String[]} placeIds place ids of restaurant branches
 * @param {Object} map google map
 */
function setRestaurantMarkers(placeIds, map) {
  const service = new google.maps.places.PlacesService(map);
  const bounds = new google.maps.LatLngBounds();
  for (const placeId of placeIds) {
    const request = {
      placeId: placeId,
      fields: ['formatted_address', 'geometry', 'name'],
    };

    service.getDetails(request, function(place, status) {
      if (status === google.maps.places.PlacesServiceStatus.OK) {
        const marker = new google.maps.Marker({
          map: map,
          position: place.geometry.location,
        });
        bounds.extend(marker.position);
        map.panToBounds(bounds);
        map.fitBounds(bounds);

        const infoWindow = new google.maps.InfoWindow();
        google.maps.event.addListener(marker, 'click', function() {
          infoWindow.setContent(
              `<div>
                  <h6> ${place.name} </h6>
                  <p> ${place.formatted_address} </p>`);
          infoWindow.open(map, marker);
        });
      }
    });
  }
}

/**
 * Configures deals of the restaurant.
 * @param {Object} deals deals of the restaurant
 */
function configureDealsOfRestaurant(deals) {
  const dealsContainer = document.getElementById('deals-container');
  dealsContainer.classList.add('card-columns');
  for (const deal of deals) {
    dealsContainer.appendChild(createDealCard(deal));
  }
}

/**
 * Configures header of deals section.
 * @param {Object} restaurant restaurant object
 */
function configureDealsHeader(restaurant) {
  const dealsHeader = document.getElementById('deals-header');
  dealsHeader.innerText = 'Deals from ' + restaurant.name;
}

/**
<<<<<<< HEAD
 * Configures menu button.
 * @param {object} restaurant the restaurant shown on the page
 * @param {number} loggedInUserId id of the logged in user
 */
function configureMenuButton(restaurant, loggedInUserId) {
  const menuBtn = document.getElementById('menu-btn');
  if (restaurant.poster.id != loggedInUserId) {
    menuBtn.hidden = true;
    return;
  }

  const deleteBtn = document.getElementById('delete-btn');
  deleteBtn.onclick = function() {
    if (confirm('Are you sure you want to delete this restaurant?')) {
      $.ajax('/api/restaurants/' + restaurant.id, {method: 'DELETE'})
          .done(() => {
            window.location.href = '/all-restaurants';
          });
    }
  };
}

let loginStatus;
/**
 * Fetches login status and stores in global variable.
 */
function fetchLoginStatus() {
  $.ajax('/api/authentication')
      .done((response) => {
        loginStatus = response;
=======
 * Gets authentication status to and configures follow button
 * @param {string} restaurantId ID of restaurant
 */
function initAuthentication(restaurantId) {
  $.ajax('/api/authentication')
      .done((loginStatus) => {
        if (!loginStatus.isLoggedIn) {
          return;
        }
        configureFollowButton(restaurantId, loginStatus.id);
>>>>>>> 4a097a6fa66c11ae63538e96ecce40c7900d1577
      });
}

/**
 * Initializes the restaurant page based on the id.
 */
async function initRestaurantPage() {
  const id = window.location.pathname.substring(12); // Remove '/restaurant/'
  await fetchLoginStatus();
  $.ajax('/api/restaurants/' + id)
      .done((restaurant) => {
        $('#restaurant-loading').hide();
        $('#restaurant-page').show();
        initAuthentication(id);
        configureRestaurantInfo(restaurant);
        initMap(restaurant);
        configureDealsOfRestaurant(restaurant.deals);
        configureDealsHeader(restaurant);
<<<<<<< HEAD
        configureFollowButton(id, loginStatus.id);
        configureMenuButton(restaurant, loginStatus.id);
=======
      })
      .fail(() => {
        $('#restaurant-loading').hide();
        $('#restaurant-not-found').show();
>>>>>>> 4a097a6fa66c11ae63538e96ecce40c7900d1577
      });
}

addLoadEvent(() => {
  initRestaurantPage();
});
