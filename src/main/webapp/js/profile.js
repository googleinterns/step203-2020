const user = 
{
  'id': 1234,
  'name': 'Aaron Tan',
  'username': 'aarontan',
  'email': 'aaront@example.com',
  'picture': 'images/profile_pic.svg',
  'bio': 'a short bio of aaron tan',
  'dealsUploaded': [
    {
        'id': 1234,
        'name': 'Starbucks Mocha 1-for-1',
        'votes': 5,
        'poster': 'def',
        'image': 'images/profile_pic.svg'
    },
    {
      'id': 1234,
      'name': 'Starbucks Mocha 1-for-1',
      'votes': 5,
      'poster': 'def',
      'image': 'images/profile_pic.svg'
    },
    {
      'id': 1234,
      'name': 'Starbucks Mocha 1-for-1',
      'votes': 5,
      'poster': 'def',
      'image': 'images/profile_pic.svg'
    },
    {
      'id': 1234,
      'name': 'Starbucks Mocha 1-for-1',
      'votes': 5,
      'poster': 'def',
      'image': 'images/profile_pic.svg'
    },
    {
      'id': 1234,
      'name': 'Starbucks Mocha 1-for-1',
      'votes': 5,
      'poster': 'def',
      'image': 'images/profile_pic.svg'
    },
  ],
  'followers': [
      {
        'id': 1234,
        'name': 'Alice Chen',
        'username': 'alicechen',
        'picture': 'images/profile_pic.svg'
      },
      {
        'id': 1234,
        'name': 'Alice Chen',
        'username': 'alicechen',
        'picture': 'images/profile_pic.svg'
      },
      {
        'id': 1234,
        'name': 'Alice Chen',
        'username': 'alicechen',
        'picture': 'images/profile_pic.svg'
      },
      {
        'id': 1234,
        'name': 'Alice Chen',
        'username': 'alicechen',
        'picture': 'images/profile_pic.svg'
      },
      {
        'id': 1234,
        'name': 'Alice Chen',
        'username': 'alicechen',
        'picture': 'images/profile_pic.svg'
      },
      {
        'id': 1234,
        'name': 'Alice Chen',
        'username': 'alicechen',
        'picture': 'images/profile_pic.svg'
      }
  ],
  'following': [
      {
          'id': 1234,
          'name': 'Alice Chen',
          'username': 'alicechen',
          'picture': 'images/profile_pic.svg'
      },
      {
        'id': 2345,
        'name': 'Starbucks',
        'username': 'starbucks',
        'picture': 'images/profile_pic.svg'
    },
  ],
  'restaurantsFollowed': [
    {
      'id': 2345,
      'name': 'Starbucks',
      'picture': 'images/profile_pic.svg'
    },
  ],
  'tagsFollowed': [
    {
      'id': 23452,
      'name': 'coffee'
    },
    {
      'id': 23452,
      'name': 'sushi'
    }
  ]
}

/** Configures user's profile. */
function configureUserProfile(user) {
  const nameContainer = document.getElementById('name');
  nameContainer.innerText = user.name;
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

/** Creates a container for deal. */
function createDealCard(deal) {
  const dealCard = document.createElement('div');
  dealCard.classList.add('deal-card', 'card');
  const dealImage = document.createElement('img');
  dealImage.className = 'card-img-top deal-card-img';
  dealImage.src = deal.image;
  dealImage.alt = 'Deal image';
  
  const dealBody = document.createElement('div');
  dealBody.className = 'card-body';
  
  const dealName = document.createElement('h6');
  dealName.className = 'card-title'
  dealName.innerText = deal.name;

  const dealVotes = document.createElement('p');
  dealVotes.className = 'card-text';
  dealVotes.innerText = deal.votes;

  const dealLink = document.createElement('a');
  dealLink.innerText = 'See detail';
  dealLink.href = '/deal/' + deal.id;

  dealBody.appendChild(dealName);
  dealBody.appendChild(dealVotes);
  dealBody.appendChild(dealLink);

  dealCard.appendChild(dealImage);
  dealCard.appendChild(dealBody);
  return dealCard;
}

function configureDealsPublishedBy(user) {
  const dealsUploadedContainer = document.getElementById('deals');
  dealsUploadedContainer.classList.add('card-columns');
  for (const deal of user.dealsUploaded) {
    const dealCard = createDealCard(deal);
    dealsUploadedContainer.appendChild(dealCard);
  }
}

/** Returns a container with user profile photo and username. */
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
  username.href = '/profile/' + user.id;
  userContainer.appendChild(username);
  return userContainer;
}

/** Adds a list of users to the container. */
function configureUserList(userList, container) {
  for (const user of userList) {
    const userContainer = createSimpleUserContainer(user);
    userContainer.classList.add('mw-25');
    container.appendChild(userContainer);
  }
}

function configureUsersFollowedBy(user) {
  const followeeContainer = document.getElementById('following');
  configureUserList(user.following, followeeContainer);
}

function configureUserFollowers(user) {
  const followersContainer = document.getElementById('followers');
  configureUserList(user.followers, followersContainer);
}

/** Returns a container with restaurant's photo and name. */
function createSimpleRestaurantContainer(restaurant) {
  const restaurantContainer = document.createElement('div');
  restaurantContainer.className = 'mb-2';
  const imageContainer = document.createElement('div');
  imageContainer.className = 'w-25 d-inline-block text-center';
  const restaurantImage = document.createElement('img');
  restaurantImage.src = restaurant.picture;
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

function configureRestaurantsFollowedBy(user) {
  const restaurantsContainer = document.getElementById('restaurants');
  for (const restaurant of user.restaurantsFollowed) {
    const restaurantContainer = createSimpleRestaurantContainer(restaurant);
    restaurantsContainer.appendChild(restaurantContainer);
  }
}

/** Returns a container with tag's name. */
function createTagContainer(tag) {
  const tagContainer = document.createElement('span');
  tagContainer.className = 'badge badge-pill badge-primary';
  tagContainer.innerText = tag.name;
  return tagContainer;
}

function configureTagsFollowedBy(user) {
  const tagsContainer = document.getElementById('tags');
  tagsContainer.classList.add('d-flex', 'flex-wrap');
  for (const tag of user.tagsFollowed) {
    const tagContainer = createTagContainer(tag);
    tagsContainer.appendChild(tagContainer);
  }
}

function configureProfileEditButton(user) {
  const profileEditButton = document.getElementById('edit-profile-btn');
  profileEditButton.onclick = function() {
    showProfileEditingForm(user);
  }
}

/** Shows profile editing form and initializes input values with the user. */
function showProfileEditingForm(user) {
  const profile = document.getElementById('profile');
  profile.hidden = true;
  const profileForm = document.getElementById('profile-form');
  profileForm.hidden = false;
  const emailInput = document.getElementById('email-input');
  emailInput.value = user.email;
  if (typeof user.picture != 'undefined') {
    const profilePhotoPreview = document.getElementById('profile-photo-preview');
    profilePhotoPreview.src = user.picture;
  }
  if (typeof user.username != 'undefined') {
    const usernameInput = document.getElementById('username-input');
    usernameInput.value = user.username;
  }
  if (typeof user.name != 'undefined') {
    const nameInput = document.getElementById('name-input');
    nameInput.value = user.username;
  }
  if (typeof user.bio != 'undefined') {
    const bioInput = document.getElementById('bio-input');
    bioInput.value = user.bio;
  }

  if (typeof user.tagsFollowed != 'undefined') {
    const tagsInput = document.getElementById('tags-input');
    user.tagsFollowed.forEach(tag => $('#tags-input').tagsinput('add', tag.name));
  }
}

/** Shows preview of the uploaded photo. */
function profilePhotoPreview(input) {
  if (input.files && input.files[0]) {
    const reader = new FileReader();
    
    reader.onload = function(e) {
      const profilePhotoPreview = document.getElementById('profile-photo-preview');
      profilePhotoPreview.src = e.target.result;
    }
    
    reader.readAsDataURL(input.files[0]);
  }
}

/** Cancels profile editing. */
function cancelProfileEditing() {
  const profile = document.getElementById('profile');
  profile.hidden = false;
  const profileForm = document.getElementById('profile-form');
  profileForm.hidden = true;
}

addLoadEvent(() => {
    configureUserProfile(user);
    configureProfileEditButton(user);
  });
