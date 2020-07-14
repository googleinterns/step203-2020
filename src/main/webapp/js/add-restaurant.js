/* eslint-disable no-unused-vars */

/*
 * Handles restaurant results when the search bar is typed
 */
searchPlaceThrottle = throttle(searchPlace, 1000);
$('#place-search-input').keyup(function() {
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
  // want to use the library from maps, but don't need the actual map
  // so a new div is created to contain the "map"
  service = new google.maps.places.PlacesService(
      document.createElement('div'),
  );
}

/**
 * Calls Places API to search for places based on text in the search bar,
 * and displays the results in the table
 */
function searchPlace() {
  const query = document.getElementById('place-search-input').value.trim();
  if (query == '') {
    handleSearchCallback([]);
    return;
  }
  const request = {
    query: query,
    type: 'restaurant',
  };
  service.textSearch(request, handleSearchCallback);
}

let searchResults = [];
/**
 * Callback for places library
 * @param {*} results
 * @param {*} status
 */
function handleSearchCallback(results) {
  searchResults = results;
  renderSearchResults();
}

/**
 * Displays the searchResults in the table
 */
function renderSearchResults() {
  const resultsTable = document.getElementById('search-results-tbody');
  resultsTable.innerHTML = `
    <tr>
      <th>Name</th>
      <th>Address</th>
      <th></th>
    </tr>`;
  searchResults.map((place) => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${place.name}</td>
      <td>${place.formatted_address}</td>`;
    const td = document.createElement('td');
    if (selectedPlaces.some(
        (selectedPlace) => place.place_id == selectedPlace.place_id,
    )) {
      td.innerHTML = '<i class="fa fa-check text-success"></i>';
    } else {
      const addButton = document.createElement('button');
      addButton.type = 'button';
      addButton.className = 'btn btn-success btn-sm';
      addButton.innerHTML = '<i class="fa fa-plus"></i>';
      addButton.onclick = () => selectPlace(place);
      td.appendChild(addButton);
    }
    row.appendChild(td);
    resultsTable.appendChild(row);
  });
}

/*
 * Logic for keeping track of what places are currently selected
 */
let selectedPlaces = [];

/**
 * Selects a place
 * @param {object} newPlace
 */
function selectPlace(newPlace) {
  if (selectedPlaces.some((place) => place.place_id == newPlace.place_id)) {
    return;
  }
  selectedPlaces.push(newPlace);
  renderSearchResults();
  renderSelectedPlaces();
}

/**
 * Unselects a place
 * @param {object} placeToRemove
 */
function unselectPlace(placeToRemove) {
  // selectedPlaces.push(place);
  selectedPlaces = selectedPlaces.filter(
      (place) => place.place_id != placeToRemove.place_id,
  );
  renderSearchResults();
  renderSelectedPlaces();
}

/**
 * Displays the selectedPlaces in the table
 */
function renderSelectedPlaces() {
  // update the hidden input field with places
  const placeInput = document.getElementById('place-input');
  placeInput.value = selectedPlaces.map((place) => place.place_id).join(',');

  // render places on table
  const selectedTable = document.getElementById('selected-restaurants-tbody');
  selectedTable.innerHTML = `
    <tr>
      <th>Name</th>
      <th>Address</th>
      <th></th>
    </tr>`;
  selectedPlaces.map((place) => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${place.name}</td>
      <td>${place.formatted_address}</td>
      <td>
        <button type="button" class="btn btn-danger btn-sm">
          <i class="fa fa-minus"></i>
        </button>
      </td>`;
    row.querySelector('button').onclick = () => unselectPlace(place);
    selectedTable.append(row);
  });
}
