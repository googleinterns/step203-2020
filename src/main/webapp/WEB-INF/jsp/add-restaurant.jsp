<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
  <meta name="description" content="" />
  <meta name="author" content="" />
  <title>Add Restaurant</title>
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
</head>

<body id="page-top">
  <%@include file="/WEB-INF/components/header.html"%>
  <div class="container">
    <section class="page-section">
      <h2 class="page-section-heading text-center text-secondary mb-4">Add A Restaurant</h2>
      <div class="row">
        <div class="col-lg-8 mx-auto">
          <form method="post" id="deal-form" name="dealform" novalidate class="needs-validation"
            enctype="multipart/form-data">
            <div class="form-group validate-me">
              <label for="name-input">Name</label>
              <input name="name" type="text" class="form-control" id="name-input" required>
              <div class="invalid-feedback">
                Please add a name.
              </div>
            </div>

            <div class="form-group validate-me">
              <label for="img-input">Choose Image</label>
              <input name="pic" type="file" class="form-control-file" id="img-input" required>
              <div class="invalid-feedback">
                Please add an image.
              </div>
            </div>
            <img style="display: none;" id="img-preview" class="mw-100 my-4" src="#" alt="your image" />

            <div class="form-group">
              <label for="restaurant-input">Addresses</label>
              <div>
                Addresses Selected:
                <table class="table table-striped">
                  <tbody id="selected-restaurants-tbody"></tbody>
                </table>
              </div>
              <div class="d-flex align-items-center">
                <i class="fa fa-search mr-2"></i>
                <input autocomplete="off" type="search" placeholder="Search for an address..." id="place-search-input"
                  class="flex-grow-1" style="outline: 0;border-width: 0 0 2px;">
              </div>
              <div>
                Search Results:
                <div>
                  <table class="table table-striped">
                    <tbody id="search-results-tbody"></tbody>
                  </table>
                </div>
              </div>
              <input type="text" class="d-none" name="places" id="place-input">
            </div>

            <div class="form-group d-flex flex-row-reverse">
              <button class="btn btn-primary" id="sendMessageButton" type="submit">Add Restaurant
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
  <script src="js/add-restaurant.js"></script>
  <!-- Maps library -->
  <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDxqwXj4lUG5Vpts3ZiXA2UksJbtDegeC4&libraries=places&callback=initMap"
    async defer></script>
</body>

</html>
