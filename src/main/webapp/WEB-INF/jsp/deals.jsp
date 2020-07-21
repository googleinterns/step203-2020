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
  <!-- Page CSS-->
  <link href="/css/deals.css" rel="stylesheet" />
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
              <p>Description: <span id="deal-info"></span></p>
              <p>Restaurant: <a id="restaurant-info"></a></p>
              <ul class="list-group" id="outlet-list"><span id="all-or-selected"></span></ul>
              <p>Validity: <span id="start-date"></span> to <span id="end-date"></span></p>
              <p>Posted by: <a id="user-poster" href=""></a></p>
              <p>Source: <a id="deal-source" href=""></a></p>
            </div>
            <button onclick="$('#deal-form').toggle()">Toggle form</button>
            <form style="display:none" method="post" id="deal-form" novalidate class="needs-validation">
              <div class="form-group form-inline">
                <label for="description-input" class="mr-2">Description:</label>
                <input name="description" type="text" class="form-control" id="description-input" required>
                <div class="invalid-feedback">
                  Please add a description.
                </div>
              </div>

              <div class="form-group form-inline">
                <label for="restaurant-input" class="mr-2">Restaurant:</label>
                <input disabled type="text" class="form-control">
              </div>
              <div class="mb-3">
                <div class="d-flex align-items-center">
                  <i class="fa fa-search mr-2"></i>
                  <input autocomplete="off" type="search" placeholder="Search for a restaurant..." id="restaurant-input"
                    class="flex-grow-1" style="outline: 0;border-width: 0 0 2px;">
                </div>
                <div class="search-menu-container">
                  <div id="restaurant-search-results" class="search-menu"></div>
                </div>
              </div>

              <div class="form-group validate-me form-inline">
                <label class="mr-2">Valid Date:</label>
                <input name="start" class="form-control" type="date" id="from-date" required>
                <span class="mx-2">to</span>
                <input name="end" class="form-control" type="date" id="from-date" required>
                <!-- <div id="date-error-msg" class="invalid-feedback">Start date must be before end date.</div> -->
              </div>

              <div class="form-group form-inline">
                <label for="source-input" class="mr-2">Source:</label>
                <input name="source" type="text" class="form-control" id="source-input">
              </div>

              <div class="form-group d-flex flex-row-reverse">
                <button class="btn btn-primary" id="sendMessageButton" type="submit">Submit Deal
                </button>
              </div>
            </form>
          </div>
          <div class="tab-pane fade" id="comments" role="tabpanel" aria-labelledby="nav-comments-tab">
            <div class="row">
              <div class="col-sm-12 mt-3">
                <form id="comment-form" action="/api/comments" method="POST">
                  <textarea class="w-100 form-control mb-3" type="text" name="content"
                    placeholder="Leave a comment..."></textarea>
                  <input type="hidden" name="dealId" id="dealId-input">
                  <input type="submit" class="btn btn-primary float-right" />
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
