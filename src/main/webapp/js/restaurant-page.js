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
 * Initializes the restaurant page based on the id.
 */
function init() {
  const id = window.location.pathname.substring(12); // Remove '/restaurant/'
  restaurant = {
    name: "McDonald",
    photoUrl: "https://d1nqx6es26drid.cloudfront.net/app/uploads/2019/11/05175538/McD_TheToken%C2%AE_1235_RGB.png"
  }
  //$.ajax('/api/restaurants/' + id)
  //   .done((restaurant) => {
        configureRestaurantInfo(restaurant);
  //    });
}

addLoadEvent(() => {
  init();
});
