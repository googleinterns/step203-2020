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
              '<div><h6>' +
                place.name +
                '</h6><p>' +
                place.formatted_address +
                '</p>');
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
 * Initializes the restaurant page based on the id.
 */
function initRestaurantPage() {
  const id = window.location.pathname.substring(12); // Remove '/restaurant/'
<<<<<<< HEAD
  // TODO call back end
  restaurant = {
    name: 'McDonald',
    photoUrl: 'https://d1nqx6es26drid.cloudfront.net/app/uploads/2019/11/05175538/McD_TheToken%C2%AE_1235_RGB.png',
    placeIds: ['ChIJS5IVbvwa2jERDxPwmWkRghI', 'ChIJuUFoBdcb2jERsu6OBBfVMQQ'],
    deals: [
      {
        id: 1,
        description: 'bubble tea',
        image: '/assets/deals/bubble-tea.jpeg',
      },
    ],
  };
  configureRestaurantInfo(restaurant);
  initMap(restaurant);
  configureRestaurantInfo(restaurant);
  configureDealsOfRestaurant(restaurant.deals);
  configureDealsHeader(restaurant);
=======
  $.ajax('/api/restaurants/' + id)
      .done((restaurant) => {
        configureRestaurantInfo(restaurant);
        configureDealsOfRestaurant(restaurant.deals);
        configureDealsHeader(restaurant);
      });
>>>>>>> d57c64e84c139ee257a3415d824ecf5976dc93b1

  $.ajax('/api/authentication')
      .done((loginStatus) => {
        if (!loginStatus.isLoggedIn) {
          return;
        }
        configureFollowButton(id, loginStatus.id);
      });
}

addLoadEvent(() => {
  initRestaurantPage();
});
