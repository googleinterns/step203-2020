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
  <script src="/js/all-section-deals.js"></script>
  <!-- Google fonts-->
  <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css" />
  <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet"
    type="text/css" />
  <!-- Core theme CSS (includes Bootstrap)-->
  <link href="/css/styles.css" rel="stylesheet" />
</head>

<body id="page-top">
  <%@include file="/WEB-INF/components/header.html"%>
  <div class="container">
    <div class="page-section">
      <div class="dropdown-sort float-right">
        <a class="nav-link py-3 px-0 px-lg-3 rounded dropdown-toggle" href="#" id="dealDropdown" role="button"
            data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            Sort by
        </a>
        <div class="dropdown-menu" id='sort' aria-labelledby="dealDropdown">
          <a class="dropdown-item" href="#">Trending</a>
          <a class="dropdown-item" href="#">Votes</a>
          <a class="dropdown-item" href="#">New</a>
        </div>
      </div>
      <div class="row row-deals"></div>
      <div class="row row-deals"></div>
      <div class="row row-deals"></div>
      <div class="row row-deals"></div>
      <div class="row row-deals"></div>
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
