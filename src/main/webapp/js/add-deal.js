/*
 * Preview the image when a file is uploaded
 */
const reader = new FileReader();
reader.onload = function(e) {
  $('#img-preview').attr('src', e.target.result);
};
$('#img-input').change((event) => {
  const input = event.currentTarget;
  if (input.files && input.files[0]) {
    reader.readAsDataURL(input.files[0]);
    $('#img-preview').show();
  } else {
    $('#img-preview').hide();
  }
});

/*
 * Handles restaurant results when the search bar is typed
 */
searchRestaurantThrottle = throttle(searchRestaurant, 1000);
$('#restaurant-input').keyup(function() {
  searchRestaurantThrottle();
});
$('#restaurant-search-results').hide();
$('#restaurant-input').focus(function() {
  searchRestaurantThrottle();
});
$('#restaurant-input').blur(function() {
  $('#restaurant-search-results').hide();
});

/*
 * Form validation
 */
$('#from-date, #to-date').change(checkDatesOrdered);
const form = document.getElementsByClassName('needs-validation')[0];
form.addEventListener('submit', (event) => {
  if (form.checkValidity() === false || !checkFormValid()) {
    event.preventDefault();
    event.stopPropagation();
  }
  const validateGroup = form.querySelectorAll('.validate-me');
  validateGroup.forEach((element) => {
    element.classList.add('was-validated');
  });
});

/*
 * Sets the form URL
 */
form.style.display = 'none';
$.ajax({
  url: '/api/upload-deals-url',
  method: 'GET',
}).done((url) => {
  form.action = url;
  form.style.display = 'block';
});

/**
 * Custom validation for form
 * @return {boolean}
 */
function checkFormValid() {
  return checkDatesOrdered();
}

/**
 * Checks if the dates of the form is ordered and displays error message.
 * @return {boolean}
 */
function checkDatesOrdered() {
  const start = document.getElementById('from-date');
  const end = document.getElementById('to-date');
  const message = document.getElementById('date-error-msg');
  if (start.value && end.value && start.value > end.value) {
    message.style.display = 'block';
    return false;
  }
  message.style.display = 'none';
  return true;
}

/**
 * Handles restaurant selection
 * @param {object} restaurant
 */
function selectRestaurant(restaurant) {
  const restaurantDiv = $('#restaurant-selected')[0];
  restaurantDiv.innerHTML = `
    <div class="d-flex align-items-center p-2">
      <span class="flex-grow-1">${restaurant.name}</span>
      <img class="search-menu-pic" src="${restaurant.image}">
    </div>
  `;
  const restaurantHiddenInput = $('#restaurant-selected-id-input')[0];
  restaurantHiddenInput.value = restaurant.id;
}

/**
 * Calls backend for retaurant based on text in restaurant search, and displays
 * results.
 */
function searchRestaurant() {
  const text = $('#restaurant-input').val().trim();
  if (text == '') {
    $('#restaurant-search-results').hide();
    return;
  }
  $.ajax({
    url: '/api/search/restaurants',
    data: {
      query: text,
    },
  }).done((restaurants) => {
    const menu = document.getElementById('restaurant-search-results');
    menu.innerHTML = '';
    if (restaurants.length == 0) {
      const row = document.createElement('div');
      row.className = 'd-flex align-items-center search-menu-item p-2';
      row.innerHTML = 'No results';
      menu.appendChild(row);
    } else {
      restaurants.forEach((restaurant) => {
        const row = document.createElement('div');
        row.className = 'd-flex align-items-center search-menu-item p-2';
        row.innerHTML = `
          <span class="flex-grow-1">${restaurant.name}</span>
          <img class="search-menu-pic" src="${restaurant.image}">
        `;
        row.onmousedown = () => selectRestaurant(restaurant);
        menu.appendChild(row);
      });
    }
    $('#restaurant-search-results').show();
  });
}

/**
 * Throttles a function. The function can be called at most once in limit
 * milliseconds.
 * @param {function} func
 * @param {number} limit - The limit of the function in milliseconds
 * @return {function}
 */
function throttle(func, limit) {
  let lastFunc;
  let lastRan;
  return function(...args) {
    if (!lastRan) {
      func(...args);
      lastRan = Date.now();
    } else {
      clearTimeout(lastFunc);
      lastFunc = setTimeout(function() {
        if ((Date.now() - lastRan) >= limit) {
          func(...args);
          lastRan = Date.now();
        }
      }, limit - (Date.now() - lastRan));
    }
  };
}
