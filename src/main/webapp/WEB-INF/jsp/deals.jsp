<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
  <meta name="description" content="" />
  <meta name="author" content="" />
  <title>Deals | Deal Finder</title>
  <script src="/js/util.js"></script>
  <!-- Font Awesome icons (free version)-->
  <script src="https://use.fontawesome.com/releases/v5.13.0/js/all.js" crossorigin="anonymous"></script>
  <script src="/js/go-to-deal.js"></script>
  <!-- Google fonts-->
  <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css" />
  <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet"
    type="text/css" />
  <!-- Core theme CSS (includes Bootstrap)-->
  <link href="/css/styles.css" rel="stylesheet" />
  <style>
    #downvote-btn:hover {
      color: #dc3545;
    }

    #downvote-btn.active {
      color: #dc3545;
    }

    #upvote-btn:hover {
      color: #28a745;
    }

    #upvote-btn.active {
      color: #28a745;
    }
  </style>
</head>

<body id="page-top">
  <%@include file="/WEB-INF/components/header.html"%>
  <div class="container">
    <div class="page-section">
      <div id="deal-page" style="display: none;">
        <div class="row mb-5 mt-5">
          <div class="col-md-8">
            <h2 class="masthead-heading mb-6" id="deal-title">title</h2>
            <img id="deal-image" src="" class="deal-img" />
          </div>
        </div>
        <nav>
          <div class="nav nav-tabs mb-2" id="nav-tab" role="tablist">
            <a class="nav-item nav-link active" id="nav-details-tab" data-toggle="tab" href="#details" role="tab"
              aria-controls="details" aria-selected="true">Details</a>
            <a class="nav-item nav-link" id="nav-comments-tab" data-toggle="tab" href="#comments" role="tab"
              aria-controls="comments" aria-selected="false">Comments</a>
          </div>
          <div id="vote-div" class="d-flex justify-content-end" style="display: none;">
            <button type="button" id="upvote-btn" class="btn" onclick="handleUpvote()">
              <span class="fas fa-angle-up"></span>
            </button>
            <span id="votes-num" class="my-auto">0</span>
            <button type="button" id="downvote-btn" class="btn" onclick="handleDownvote()">
              <span class="fas fa-angle-down"></span>
            </button>
          </div>
        </nav>
        <div class="tab-content" id="nav-tabContent">
          <div class="tab-pane fade show active" id="details" role="tabpanel" aria-labelledby="nav-details-tab">
            <div class="col-md-8">
              <p class="text-secondary">Description: <span id="deal-info"></span></p>
              <p class="text-secondary">Restaurant: <span id="restaurant-info"></span></p>
              <ul class="list-group" id="outlet-list"><span id="all-or-selected"></span></ul>
              <p class="text-secondary">Validity: <span id="start-date"></span> to <span id="end-date"></span></p>
              <p class="text-secondary">Posted by: <a id="user-poster" href=""></a></p>
              <p class="text-secondary">Source: <a id="deal-source" href=""></a></p>
              <ul class="list-group" id="menu"></ul>
            </div>
          </div>
          <div class="tab-pane fade" id="comments" role="tabpanel" aria-labelledby="nav-comments-tab">
            <div class="row">
              <div class="col-sm-12 mt-3">
                <form id="comment-form" class="hidden" enctype="multipart/form-data" method="POST">
                  <textarea style="width: 100%;" class="comment form-control mb-3" type="text" name="comment"
                    placeholder="Leave a comment..."></textarea>
                  <input type="submit" class="btn btn-secondary float-right" />
                </form>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12" id="comment-list"></div>
            </div>
          </div>
        </div>
      </div>
      <div id="deal-notfound" style="display: none;">
        <div class="row mb-5 mt-5">
          <div class="col-md-8">
            <h2 class="masthead-heading mb-6" id="deal-title">Deal Not Found</h2>
            <div>This deal does not exist.</div>
          </div>
        </div>
      </div>
      <div id="deal-loading">
        <div class="spinner-border" role="status">
          <span class="sr-only">Loading...</span>
        </div>
      </div>
    </div>
  </div>

  <%@include file="/WEB-INF/components/footer.html"%>
  <!-- Bootstrap core JS-->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.bundle.min.js"></script>
  <!-- Third party plugin JS-->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.4.1/jquery.easing.min.js"></script>
  <!-- Core theme JS-->
  <script src="/js/scripts.js"></script>
</body>

</html>
