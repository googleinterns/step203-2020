<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
  <meta name="description" content="" />
  <meta name="author" content="" />
  <title>Restaurant</title>
  <script src="/js/util.js"></script>
  <script src="/js/restaurant-page.js"></script>
  <link href="/css/deal.css" rel="stylesheet" />
  <link href="/tagsinput/tagsinput.css" rel="stylesheet" />
  <!-- Font Awesome icons (free version)-->
  <script src="https://use.fontawesome.com/releases/v5.13.0/js/all.js" crossorigin="anonymous"></script>
  <!-- Google fonts-->
  <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" />
  <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" />
  <!-- Core theme CSS (includes Bootstrap)-->
  <link href="/css/styles.css" rel="stylesheet" />
</head>


<body id="page-t">
  <%@include file="/WEB-INF/components/header.html"%>
  <div class="page-section">
    <div class="container">
      <div class="row mb-5">
        <div class="col-sm-4 col-lg-3">
          <img id="restaurant-photo" class="img-fluid mb-4" alt="restaurant-photo" />
          <button id="follow-btn" type="button" class="btn btn-primary" hidden>Follow</button>
        </div>
        <div class="col-sm-8 col-lg-9">
          <h3 id="restaurant-name">
          </h3>
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
    <script src="/tagsinput/tagsinput.js"></script>
    <!-- Core theme JS-->
    <script src="/js/scripts.js"></script>
</body>

</html>