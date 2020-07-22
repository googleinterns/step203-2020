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
  $.ajax('/api/restaurants/' + id)
      .done((restaurant) => {
        configureRestaurantInfo(restaurant);
        configureDealsOfRestaurant(restaurant.deals);
        configureDealsHeader(restaurant);
      });

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
