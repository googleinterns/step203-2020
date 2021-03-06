/* eslint-disable no-unused-vars */

/**
 * Configures user's profile.
 * @param {object} user The user profile.
 */
function configureUserProfile(user) {
  const usernameContainer = document.getElementById('username');
  usernameContainer.innerText = user.username;
  const emailContainer = document.getElementById('email');
  emailContainer.innerText = user.email;
  const bioContainer = document.getElementById('bio');
  bioContainer.innerText = user.bio;
  const profileImage = document.getElementById('profile-photo');
  profileImage.src = user.picture;

  configureDealsPublishedBy(user);
  configureUserFollowers(user);
  configureUsersFollowedBy(user);
  configureRestaurantsFollowedBy(user);
  configureTagsFollowedBy(user);
}

/**
 * Configures deals published by the user.
 * @param {object} user The user whose deals are shown.
 */
function configureDealsPublishedBy(user) {
  const dealsUploadedContainer = document.getElementById('deals');
  dealsUploadedContainer.classList.add('card-columns');
  for (const deal of user.dealsUploaded) {
    const dealCard = createDealCard(deal);
    dealsUploadedContainer.appendChild(dealCard);
  }
}

/**
 * Returns a container with user profile photo and username.
 * @param {object} user The user whose info will be shown.
 * @return {object} a DOM element with user's profile photo and username.
 */
function createSimpleUserContainer(user) {
  const userContainer = document.createElement('div');
  userContainer.className = 'mb-2';
  const imageContainer = document.createElement('div');
  imageContainer.className = 'w-25 d-inline-block text-center';
  const profileImage = document.createElement('img');
  profileImage.src = user.picture;
  profileImage.alt = 'profile photo';
  profileImage.className = 'img-fluid w-50 mx-auto';
  imageContainer.appendChild(profileImage);
  userContainer.appendChild(imageContainer);

  const username = document.createElement('a');
  username.innerText = user.username;
  username.className = 'h6 d-inline-block w-75';
  username.href = '/user/' + user.id;
  userContainer.appendChild(username);
  return userContainer;
}

/**
 * Adds a list of users to the container.
 * @param {object[]} userList a list users to be shown.
 * @param {object} container a DOM element in which users to be added
 */
function configureUserList(userList, container) {
  for (const user of userList) {
    const userContainer = createSimpleUserContainer(user);
    userContainer.classList.add('mw-25');
    container.appendChild(userContainer);
  }
}

/**
 * Configures users followed by the user.
 * @param {object} user The user whose following is shown.
 */
function configureUsersFollowedBy(user) {
  const followeeContainer = document.getElementById('following');
  configureUserList(user.following, followeeContainer);
}

/**
 * Configures followers of the user.
 * @param {object} user The user whose followers are shown.
 */
function configureUserFollowers(user) {
  const followersContainer = document.getElementById('followers');
  configureUserList(user.followers, followersContainer);
}

/**
 * Returns a container with restaurant's photo and name.
 * @param {object} restaurant The restaurant whose info is shown.
 * @return {object} a restaurant container with the restaurant's info.
 */
function createSimpleRestaurantContainer(restaurant) {
  const restaurantContainer = document.createElement('div');
  restaurantContainer.className = 'mb-2';
  const imageContainer = document.createElement('div');
  imageContainer.className = 'w-25 d-inline-block text-center';
  const restaurantImage = document.createElement('img');
  restaurantImage.src = restaurant.photoUrl;
  restaurantImage.alt = 'restaurant photo';
  restaurantImage.className = 'img-fluid w-50 mx-auto';
  imageContainer.appendChild(restaurantImage);
  restaurantContainer.appendChild(imageContainer);

  const restaurantName = document.createElement('a');
  restaurantName.innerText = restaurant.name;
  restaurantName.className = 'h6 d-inline-block w-75';
  restaurantName.href = '/restaurant/' + restaurant.id;
  restaurantContainer.appendChild(restaurantName);
  return restaurantContainer;
}

/**
 * Configures restaurants followed by the user.
 * @param {object} user The user whose restaurants followed are shown.
 */
function configureRestaurantsFollowedBy(user) {
  const restaurantsContainer = document.getElementById('restaurants');
  for (const restaurant of user.restaurantsFollowed) {
    const restaurantContainer = createSimpleRestaurantContainer(restaurant);
    restaurantsContainer.appendChild(restaurantContainer);
  }
}

/**
 * Returns a container with tag's name.
 * @param {object} tag The tag object.
 * @return {object} a container with tag's name.
 */
function createTagContainer(tag) {
  const tagContainer = document.createElement('span');
  tagContainer.className = 'badge badge-pill badge-primary mx-1';
  tagContainer.innerText = tag.name;
  return tagContainer;
}

/**
 * Configures tags followed by the user.
 * @param {object} user The user whose tags are shown.
 */
function configureTagsFollowedBy(user) {
  const tagsContainer = document.getElementById('tags');
  tagsContainer.classList.add('d-flex', 'flex-wrap', 'mt-2');
  for (const tag of user.tagsFollowed) {
    const tagContainer = createTagContainer(tag);
    tagsContainer.appendChild(tagContainer);
  }
}

/**
 * Configures a button for editing the user's profile.
 * @param {object} user The user whose profile will be edited.
 */
function configureProfileEditButton(user) {
  const profileEditButton = document.getElementById('edit-profile-btn');
  profileEditButton.hidden = false;
  profileEditButton.onclick = function() {
    showProfileEditingForm(user);
  };
}

/**
 * Configures a button for following/unfollowing the user.
 * @param {object} user The user who will be followed or unfollowed.
 * @param {number} userLoggedInId The id of the current user who will
 *  follow or unfollow.
 */
function configureFollowButton(user, userLoggedInId) {
  const followButton = document.getElementById('follow-btn');
  $.ajax('/api/follows/users/' + user.id,
      {data: {followerId: userLoggedInId}})
      .done((isFollowing) => {
        followButton.hidden = false;
        if (isFollowing.startsWith('true')) {
          followButton.innerText = 'Unfollow';
          followButton.onclick = () => unfollow(user);
        } else {
          followButton.innerText = 'Follow';
          followButton.onclick = () => follow(user);
        }
      });
}

/**
 * Follows a user and reloads the page.
 * @param {object} user the user to be followed.
 */
function follow(user) {
  $.ajax('/api/follows/users/' + user.id,
      {method: 'POST'})
      .done(() => location.reload());
}

/**
 * Unfollows a user and reloads the page.
 * @param {object} user the user to be unfollowed.
 */
function unfollow(user) {
  $.ajax('/api/follows/users/' + user.id,
      {method: 'DELETE'})
      .done(() => location.reload());
}

/**
 * Sets profile form url.
 * @param {String} url url for form submission.
 */
function setProfileFormUrl(url) {
  const profileEditForm = document.getElementById('profile-form');
  profileEditForm.action = url;
}

let initialProfilePhotoUrl = undefined;
/**
 * Shows profile editing form and initializes input values with the user.
 * @param {object} user The user whose profile is being edited.
 */
function showProfileEditingForm(user) {
  const profile = document.getElementById('profile');
  profile.hidden = true;
  const profileEditForm = document.getElementById('profile-form');
  profileEditForm.hidden = false;
  const emailInput = document.getElementById('email-input');
  emailInput.value = user.email;

  const profilePhotoPreview =
      document.getElementById('profile-photo-preview');
  profilePhotoPreview.src = user.picture;
  const photoUploadInput = document.getElementById('photo-upload-input');
  const defaultPhotoCheckbox =
      document.getElementById('default-photo-checkbox');
  const isShowingDefaultProfilePicture = isDefaultProfilePicture(user.picture);
  defaultPhotoCheckbox.checked = isShowingDefaultProfilePicture;
  photoUploadInput.hidden = isShowingDefaultProfilePicture;
  if (isShowingDefaultProfilePicture) {
    initialProfilePhotoUrl = user.picture;
  }
  if (typeof user.username != 'undefined') {
    const usernameInput = document.getElementById('username-input');
    usernameInput.value = user.username;
  }
  if (typeof user.bio != 'undefined') {
    const bioInput = document.getElementById('bio-input');
    bioInput.value = user.bio;
  }

  if (typeof user.tagsFollowed != 'undefined') {
    user.tagsFollowed.forEach((tag) =>
      $('#tags-input').tagsinput('add', tag.name));
  }
}

/**
 * Toggles photo upload input when checkbox value changes.
 * @param {Object} checkbox default photo checkbox
 */
function toggleDefaultPhotoCheckbox(checkbox) {
  const photoUploadInput = document.getElementById('photo-upload-input');
  const profilePhotoFile = document.getElementById('profile-photo-file');
  const preview =
      document.getElementById('profile-photo-preview');
  if (checkbox.checked) {
    photoUploadInput.hidden = true;
    preview.src = '/images/default-profile-pic.svg';
    profilePhotoFile.value = '';
  } else {
    photoUploadInput.hidden = false;
    preview.src = initialProfilePhotoUrl;
  }
}

/**
 * Returns true if the given url is the default profile image.
 * @param {String} url picture url
 * @return {boolean} true if the given url is the default profile image.
 */
function isDefaultProfilePicture(url) {
  return url.startsWith('/images/');
}

/**
 * Shows preview of the uploaded photo.
 * @param {object} input The input element in DOM.
 */
function profilePhotoPreview(input) {
  if (input.files && input.files[0]) {
    const reader = new FileReader();

    reader.onload = function(e) {
      const profilePhotoPreview =
        document.getElementById('profile-photo-preview');
      profilePhotoPreview.src = e.target.result;
    };

    reader.readAsDataURL(input.files[0]);
  }
}

/**
 * Cancels profile editing.
 */
function cancelProfileEditing() {
  const profile = document.getElementById('profile');
  profile.hidden = false;
  const profileEditForm = document.getElementById('profile-form');
  profileEditForm.hidden = true;
}

/**
 * Configures buttons based the user profile and the current logged
 * in user if any.
 * @param {object} user The user whose profile is shown.
 */
function configureButtons(user) {
  $.ajax('/api/authentication')
      .done((loginStatus) => {
        if (loginStatus.isLoggedIn) {
          if (loginStatus.id == user.id) {
            configureProfileEditButton(user);
          } else {
            configureFollowButton(user, loginStatus.id);
          }
        }
      });
}

/**
 * Configures the form's post url
 * @param {string} userId
 */
function configurePostUrl(userId) {
  $.ajax('/api/user-post-url/' + userId)
      .done((url) => {
        setProfileFormUrl(url);
      });
}

/**
 * Initializes the user profile page based on the id.
 */
function initUserPage() {
  const id = window.location.pathname.substring(6); // Remove '/user/'
  $.ajax('/api/users/' + id)
      .done((user) => {
        $('#user-loading').hide();
        $('#user-page').show();
        configureButtons(user);
        configureUserProfile(user);
        configurePostUrl(id);
      })
      .fail(() => {
        $('#user-loading').hide();
        $('#user-not-found').show();
      });
}

/**
 * Initializes forms validation.
 */
function initializeForms() {
  const forms = document.getElementsByClassName('needs-validation');
  const validation = Array.prototype.filter.call(forms, function(form) {
    form.addEventListener('submit', function(event) {
      if (form.checkValidity() === false) {
        event.preventDefault();
        event.stopPropagation();
      }
      form.classList.add('was-validated');
    }, false);
  });
}

addLoadEvent(() => {
  initializeForms();
  initUserPage();
});
