const commentsData = {
  'comments': [
    {
      'id': 1234,
      'user': {
        'id': 1234,
        'name': 'Alice Chen',
        'username': 'alicechen',
        'profile-pic': '/some-url-12345.jpg',
      },
      'timestamp': 123456789, // in milliseconds
      'text': 'I ate here last week. Portions too small.',
    },
  ],
  'token': 'bhfsdaog', // token for pagination
};

let votes = 0;

/**
 * Loads the deal onto the page
 * @param {object} deal
 */
function loadDealDataToPage(deal) {
  const dealTitleElement = document.getElementById('deal-title');
  dealTitleElement.innerText = deal.description;

  const dealImageElement = document.getElementById('deal-image');
  dealImageElement.src = deal.image;

  const dealInfoElement = document.getElementById('deal-info');
  dealInfoElement.innerText = deal.description;

  const dealRestaurantElement = document.getElementById('restaurant-info');
  dealRestaurantElement.innerText = deal.restaurant.name;

  const dealValidStart = document.getElementById('start-date');
  dealValidStart.innerText = deal.start;
  const dealValidEnd = document.getElementById('end-date');
  dealValidEnd.innerText = deal.end;

  const dealPoster = document.getElementById('user-poster');
  dealPoster.href = '/user/' + deal.poster.id;
  dealPoster.innerText = deal.poster.username;

  const dealSource = document.getElementById('deal-source');
  dealSource.innerText = deal.source;
  dealSource.href = deal.source;

  const voteElement = document.getElementById('votes-num');
  votes = deal.votes;
  voteElement.innerText = deal.votes;
}

/**
 * Get comments for a deal
 * @param {object} commentsData
 */
function getComments(commentsData) {
  const commentListElement = document.getElementById('comment-list');
  commentListElement.innerHTML = '';
  commentsData.comments.forEach((comment) => {
    commentListElement.appendChild(createCommentElement(comment));
  });
}

/**
 * Creates comment element
 * @param {object} commentEntity
 * @return {object} commentElement
 */
function createCommentElement(commentEntity) {
  const commentElement = document.createElement('div');
  commentElement.className = 'comment border border-info pb-3 pt-3 mb-3 mt-3';

  const textElement = document.createElement('span');
  textElement.innerText = commentEntity.user.username +
  ': ' + commentEntity.text;

  commentElement.appendChild(textElement);
  return commentElement;
}

let myVote = 0;
/**
 * Updates vote UI based on global variable myVote
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
 * Called when the user clicks the upvote button
 */
function handleUpvote() {
  if (myVote == 1) {
    myVote = 0;
  } else {
    myVote = 1;
  }
  updateMyVote();
}

/**
 * Called when the user clicks the downvote button
 */
function handleDownvote() {
  if (myVote == -1) {
    myVote = 0;
  } else {
    myVote = -1;
  }
  updateMyVote();
}

/**
 * Calls backend for data on deal
 */
function initDeal() {
  const myPath = window.location.pathname; // path is /deals/<id>
  const myId = myPath.substr(7);
  $.ajax({
    url: '/api/deals/' + myId,
  }).done((deal) => {
    loadDealDataToPage(deal);
  });
}

addLoadEvent(() => {
  initDeal();
  // loadDealDataToPage(deal);
  getComments(commentsData);
});
