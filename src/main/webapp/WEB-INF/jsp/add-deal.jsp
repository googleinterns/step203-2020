<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
  <meta name="description" content="" />
  <meta name="author" content="" />
  <title>Add Deal</title>
  <script src="/js/util.js"></script>
  <!-- Font Awesome icons (free version)-->
  <script src="https://use.fontawesome.com/releases/v5.13.0/js/all.js" crossorigin="anonymous"></script>
  <!-- Google fonts-->
  <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css" />
  <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet"
    type="text/css" />
  <!-- Tags Input CSS -->
  <link href="tagsinput/tagsinput.css" rel="stylesheet" />
  <!-- Core theme CSS (includes Bootstrap)-->
  <link href="css/styles.css" rel="stylesheet" />
  <!-- Page CSS-->
  <link href="css/add-deal.css" rel="stylesheet" />
</head>

<body id="page-top">
  <%@include file="/WEB-INF/components/header.html"%>
  <div class="container">
    <section class="page-section">
      <h2 class="page-section-heading text-center text-secondary mb-4">Submit A Deal</h2>
      <div class="row">
        <div class="col-lg-8 mx-auto">
          <form method="post" id="deal-form" name="dealform" novalidate class="needs-validation" enctype="multipart/form-data">
            <div class="form-group validate-me">
              <label for="img-input">Choose Image</label>
              <input name="pic" type="file" class="form-control-file" id="img-input" required>
              <div class="invalid-feedback">
                Please add an image.
              </div>
            </div>
            <img style="display: none;" id="img-preview" class="mw-100 my-4" src="#" alt="your image" />

            <div class="form-group validate-me">
              <label for="description-input">Description</label>
              <input name="description" type="text" class="form-control" id="description-input" required>
              <div class="invalid-feedback">
                Please add a description.
              </div>
            </div>

            <div class="form-group">
              <label for="source-input">Source</label>
              <small class="text-muted">
                (Optional)
              </small>
              <input name="source" type="text" class="form-control" id="source-input">
            </div>

            <div class="form-group validate-me">
              <label>Valid Date</label>
              <div class="row">
                <div class="col-12 col-md-6">
                  <label class="mb-0 mr-1" for="from-date">From</label>
                  <input name="start" class="mr-1 form-control" type="date" id="from-date" required>
                  <div class="invalid-feedback">
                    Please select date.
                  </div>
                </div>
                <div class="col-12 col-md-6">
                  <label class="mb-0 mr-1" for="to-date">To</label>
                  <input name="end" class="form-control" type="date" id="to-date" required>
                  <div class="invalid-feedback">
                    Please select date.
                  </div>
                </div>
              </div>
              <div id="date-error-msg" class="invalid-feedback">Start date must be before end date.</div>
            </div>

            <div class="form-group">
              <label for="restaurant-input">Restaurant</label>
              <div class="d-flex align-items-center">
                <i class="fa fa-search mr-2"></i>
                <input autocomplete="off" type="search" placeholder="Search for a restaurant..." id="restaurant-input"
                  class="flex-grow-1" style="outline: 0;border-width: 0 0 2px;">
              </div>
              <div class="search-menu-container">
                <div id="restaurant-search-results" class="search-menu"></div>
              </div>
              <div class="validate-me mt-2">
                <div id="restaurant-selected">
                  No Restaurant Selected.
                </div>
                <input name="restaurant" id="restaurant-selected-id-input" type="text" class="d-none" required>
                <div class="invalid-feedback">
                  Please select restaurant. Search for a restaurant to select.
                </div>
              </div>
            </div>

            <div class="form-group">
              <label>Tags</label>
              <input name="tags" type="text" data-role="tagsinput">
              <small class="form-text text-muted">
                Start typing and hit enter to enter tags.
              </small>
            </div>
            <div class="form-check mb-2">
              <input type="checkbox" class="form-check-input" id="notify-followers" name="notify-followers" value="default">
              <label class="form-check-label">Notify my followers</label>
            </div>

            <div class="form-group d-flex flex-row-reverse">
              <button class="btn btn-primary" id="sendMessageButton" type="submit">Submit Deal
              </button>
            </div>
          </form>
        </div>
      </div>
    </section>
  </div>
  </div>
  <%@include file="/WEB-INF/components/footer.html"%>
  <!-- Bootstrap core JS-->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.bundle.min.js"></script>
  <!-- Third party plugin JS-->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.4.1/jquery.easing.min.js"></script>
  <script src="tagsinput/tagsinput.js"></script>
  <!-- Core theme JS-->
  <script src="js/scripts.js"></script>
  <!-- Page JS -->
  <script src="js/add-deal.js"></script>
</body>

</html>
