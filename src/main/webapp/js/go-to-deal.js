let dealId;
let votes = 0;
let myVote = 0;

let isLoggedIn;
let userId = null;

/**
 * Calls the backend to get the list of comments and loads it to the page
 */
function initComments() {
  document.getElementById('dealId-input').value = dealId;
  $.ajax({
    url: '/api/comments',
    data: {
      dealId: dealId,
    },
  }).done((comments) => {
    loadCommentsToPage(comments);
  });
}

/**
 * Loads the deal onto the page
 * @param {object} deal
 */
function loadDealDataToPage(deal) {
  $('#deal-loading').hide();
  $('#deal-page').show();

  const dealTitleElement = document.getElementById('deal-title');
  dealTitleElement.innerText = deal.description;

  const dealImageElement = document.getElementById('deal-image');
  dealImageElement.src = deal.image;

  const voteElement = document.getElementById('votes-num');
  votes = deal.votes;
  voteElement.innerText = deal.votes;

  loadDataToDetails(deal);
  loadDataToForm(deal);

  dealId = deal.id;
}

/**
 * Loads the deal onto the details tab
 * @param {object} deal
 */
function loadDataToDetails(deal) {
  const dealInfoElement = document.getElementById('deal-info');
  dealInfoElement.innerText = deal.description;

  const dealRestaurantElement = document.getElementById('restaurant-info');
  dealRestaurantElement.innerText = deal.restaurant.name;
  dealRestaurantElement.href = '/restaurant/' + deal.restaurant.id;

  const dealValidStart = document.getElementById('start-date');
  dealValidStart.innerText = deal.start;
  const dealValidEnd = document.getElementById('end-date');
  dealValidEnd.innerText = deal.end;

  const dealPoster = document.getElementById('user-poster');
  dealPoster.href = '/user/' + deal.poster.id;
  dealPoster.innerText = deal.poster.username;

  const tagsContainer = document.getElementById('tags');
  for (const tag of deal.tags) {
    const tagContainer = createTagContainer(tag);
    tagsContainer.appendChild(tagContainer);
  }

  const dealSource = document.getElementById('deal-source');
  dealSource.innerText = deal.source;
}

/**
 * Loads the deal onto the edit form
 * @param {object} deal
 */
function loadDataToForm(deal) {
  const descriptionInput = document.getElementById('description-input');
  descriptionInput.value = deal.description;

  const restaurantInput = document.getElementById('restaurant-input');
  restaurantInput.value = deal.restaurant.name;

  const startInput = document.getElementById('start-input');
  startInput.value = deal.start;
  const endInput = document.getElementById('end-input');
  endInput.value = deal.end;

  const posterInput = document.getElementById('poster-input');
  posterInput.value = deal.poster.username;

  const dealSource = document.getElementById('source-input');
  dealSource.value = deal.source;

  const restaurantIdInput = document.getElementById('restaurant-id-input');
  restaurantIdInput.value = deal.restaurant.id;
}

/**
 * Returns a container with tag's name.
 * @param {object} tag The tag object.
 * @return {HTMLSpanElement} a container with tag's name.
 */
function createTagContainer(tag) {
  const tagContainer = document.createElement('span');
  tagContainer.className = 'badge badge-pill badge-primary mx-1';
  tagContainer.innerText = tag.name;
  return tagContainer;
}

/**
 * Get comments for a deal
 * @param {array} comments
 */
function loadCommentsToPage(comments) {
  const commentListElement = document.getElementById('comment-list');
  commentListElement.innerHTML = '';
  comments.forEach((comment) => {
    commentListElement.appendChild(createCommentBox(comment));
  });
}

/**
 * Creates comment element
 * @param {object} comment
 * @return {HTMLDivElement} commentBox
 */
function createCommentBox(comment) {
  const commentBox = document.createElement('div');
  commentBox.className = 'border border-info py-3 px-3 my-3';

  addCommentContentToBox(commentBox, comment);

  return commentBox;
}

/**
 * Adds the content and edit/delete buttons to the provided div
 * @param {HTMLDivElement} commentBox
 * @param {object} comment
 */
function addCommentContentToBox(commentBox, comment) {
  commentBox.innerHTML = '';

  const commentElement = document.createElement('div');
  commentElement.className = 'd-flex';
  commentBox.appendChild(commentElement);

  const contentElement = document.createElement('div');
  contentElement.className = 'flex-grow-1 d-flex flex-column ' +
    'justify-content-between';
  commentElement.appendChild(contentElement);

  const textElement = document.createElement('div');
  textElement.innerText = comment.user.username + ': ' + comment.content;
  contentElement.appendChild(textElement);

  const timeElement = document.createElement('small');
  timeElement.className = 'text-muted';
  const date = new Date(Date.parse(comment.timestamp));
  timeElement.innerText = 'Posted on: ' + date.toString();
  contentElement.appendChild(timeElement);

  if (isLoggedIn && comment.user.id == userId) {
    const deleteEditContainer = document.createElement('div');
    deleteEditContainer.className = 'd-flex flex-column';
    commentElement.appendChild(deleteEditContainer);

    const editBtn = document.createElement('button');
    editBtn.className = 'btn btn-warning btn-sm mb-1';
    editBtn.innerHTML = '<i class="fa fa-pencil-alt" aria-hidden="true"></i>';
    editBtn.onclick = () => addCommentEditToBox(commentBox, comment);
    deleteEditContainer.appendChild(editBtn);

    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'btn btn-danger btn-sm';
    deleteBtn.innerHTML = '<i class="fa fa-trash" aria-hidden="true"></i>';
    deleteBtn.onclick = () => {
      if (confirm('Are you sure you want to delete this comment?')) {
        deleteComment(commentBox, comment);
      }
    };
    deleteEditContainer.appendChild(deleteBtn);
  }
}

/**
 * Adds the textarea and save/cancel button to the provided div to edit the
 * comment
 * @param {HTMLDivElement} commentBox
 * @param {object} comment
 */
function addCommentEditToBox(commentBox, comment) {
  commentBox.innerHTML = '';

  const textareaDiv = document.createElement('div');
  const textarea = document.createElement('textarea');
  textarea.className = 'w-100 form-control mb-2';
  textarea.value = comment.content;
  textareaDiv.appendChild(textarea);
  commentBox.appendChild(textareaDiv);

  const buttonDiv = document.createElement('div');
  commentBox.appendChild(buttonDiv);

  const saveBtn = document.createElement('button');
  saveBtn.className = 'btn btn-primary';
  saveBtn.innerText = 'Save';
  saveBtn.onclick = () => {
    const newContent = textarea.value;
    comment.content = newContent;
    updateComment(comment);
    addCommentContentToBox(commentBox, comment);
  };
  buttonDiv.appendChild(saveBtn);

  const cancelBtn = document.createElement('button');
  cancelBtn.className = 'btn btn-primary ml-2';
  cancelBtn.innerText = 'Cancel';
  cancelBtn.onclick = () => addCommentContentToBox(commentBox, comment);
  buttonDiv.appendChild(cancelBtn);
}

/**
 * Makes a request to the backend to delete the comment, and removes the div
 * element
 * @param {HTMLDivElement} commentBox div to be deleted
 * @param {object} comment comment object
 */
function deleteComment(commentBox, comment) {
  commentBox.remove();
  $.ajax({
    url: '/api/comments/' + comment.id,
    method: 'DELETE',
  });
}

/**
 * Makes a request to the backend to update the comment
 * @param {object} comment comment object
 */
function updateComment(comment) {
  $.ajax({
    url: '/api/comments/' + comment.id,
    method: 'PUT',
    data: {
      content: comment.content,
    },
  });
}

/**
 * Updates vote UI based on global variable vote and myVote
 */
function updateMyVote() {
  const upvoteBtn = document.getElementById('upvote-btn');
  const downvoteBtn = document.getElementById('downvote-btn');
  upvoteBtn.classList.remove('active');
  downvoteBtn.classList.remove('active');
  if (myVote > 0) {
    upvoteBtn.classList.add('active');
  } else if (myVote < 0) {
    downvoteBtn.classList.add('active');
  }
  const voteElement = document.getElementById('votes-num');
  voteElement.innerText = votes + myVote;
}

/**
 * Calls backend and POSTs vote to deal
 * @param {number} dir
 */
function postVote(dir) {
  $.ajax({
    url: '/api/vote/' + dealId,
    method: 'POST',
    data: {
      dir: dir,
    },
  });
}

/**
 * Called when the user clicks the upvote button
 */
function handleUpvote() {
  if (myVote == 1) {
    myVote = 0;
    postVote(0);
  } else {
    myVote = 1;
    postVote(1);
  }
  updateMyVote();
}

/**
 * Called when the user clicks the downvote button
 */
function handleDownvote() {
  if (myVote == -1) {
    myVote = 0;
    postVote(0);
  } else {
    myVote = -1;
    postVote(-1);
  }
  updateMyVote();
}

/**
 * Display Deal Not Found on the page
 */
function showNotFound() {
  $('#deal-loading').hide();
  $('#deal-notfound').show();
}

/**
 * Calls backend to get user's current vote status, and shows the
 * upvote/downvote buttons
 */
function initVotes() {
  $.ajax('/api/vote/' + dealId)
      .done((dir) => {
        myVote = parseInt(dir);
        votes -= myVote; // exclude myVote from global vote count
        const voteDiv = document.getElementById('vote-div');
        voteDiv.style.display = 'block';
        updateMyVote();
      });
}

/**
 * Shows the form to edit the deal, and hides the deal details
 */
function handleEdit() {
  $('#deal-details').hide();
  $('#edit-form').show();
}

/**
 * Shows the deal details and hides the form
 */
function handleCancelEdit() {
  $('#deal-details').show();
  $('#edit-form').hide();
}

/**
 * Submits the form with a PUT request and refreshes the page
 */
function handleSubmit() {
  const form = document.getElementById('edit-form');
  const validateGroup = form.querySelectorAll('.validate-me');
  validateGroup.forEach((element) => {
    element.classList.add('was-validated');
  });
  if (!form.checkValidity() || !checkFormDates()) {
    return;
  }

  $.ajax({
    type: 'PUT',
    url: '/api/deals/' + dealId,
    data: $(form).serialize(),
  }).done((a) => {
    location.reload();
  });
}

/**
 * Calls backend for data on deal
 */
function initDeal() {
  const myPath = window.location.pathname; // path is /deals/<id>
  const myId = myPath.substr(7);
  $.ajax('/api/deals/' + myId)
      .done((deal) => {
        loadDealDataToPage(deal);
        initComments();
        initVotes();
      })
      .fail(() => {
        showNotFound();
      });
}

/**
 * Handles restaurant selection
 * @param {object} restaurant
 */
function selectRestaurant(restaurant) {
  document.getElementById('restaurant-id-input').value = restaurant.id;
  document.getElementById('restaurant-input').value = restaurant.name;
}

/**
 * Checks if the dates of the form is ordered and displays error message.
 * @return {boolean}
 */
function checkFormDates() {
  const start = document.getElementById('start-input');
  const end = document.getElementById('end-input');
  const message = document.getElementById('date-error-msg');
  return checkDatesOrdered(start, end, message);
}

/**
 * Loads user's login info into global variables
 * @return {Promise} promise of when the info is done loading
 */
function loadUserLoginInfo() {
  return $.ajax('/api/authentication')
      .done((loginStatus) => {
        if (loginStatus.isLoggedIn) {
          isLoggedIn = true;
          userId = loginStatus.id;
        } else {
          isLoggedIn = false;
        }
      });
}

/**
 * Initializes data on the page
 */
async function initPage() {
  await loadUserLoginInfo();
  initDeal();
  initSearchRestaurant(
      document.getElementById('search-container'),
      selectRestaurant,
  );
}

addLoadEvent(() => {
  initPage();
});
