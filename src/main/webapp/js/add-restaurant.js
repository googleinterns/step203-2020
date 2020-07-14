/*
 * Handles restaurant results when the search bar is typed
 */
searchPlaceThrottle = throttle(searchPlace, 1000);
$('#place-input').keyup(function() {
  searchPlaceThrottle();
});

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
const form = document.getElementsByClassName('needs-validation')[0];
form.addEventListener('submit', (event) => {
  if (form.checkValidity() === false) {
    event.preventDefault();
    event.stopPropagation();
  }
  const validateGroup = form.querySelectorAll('.validate-me');
  validateGroup.forEach((element) => {
    element.classList.add('was-validated');
  });
});

let service;
/**
 * Init Places library
 */
function initMap() {
  service = new google.maps.places.PlacesService(
      document.createElement('div'),
  );
}

/**
 * Calls Places API to search for places based on text in the search bar,
 * and displays the results in the table
 */
function searchPlace() {
  const query = document.getElementById('place-input').value;
  const request = {
    query: query,
    type: 'restaurant',
  };
  service.textSearch(request, handleSearchCallback);
}

/**
 * Callback for places library
 * @param {*} results
 * @param {*} status
 */
function handleSearchCallback(results, status) {
  console.log(results);
}
