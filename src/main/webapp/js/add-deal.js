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

addLoadEvent(() => {
  initSearchRestaurant(
      document.getElementById('search-container'),
      selectRestaurant,
  );
});
