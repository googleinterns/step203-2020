<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
  <meta name="description" content="" />
  <meta name="author" content="" />
  <title>Deal Finder</title>
  <!-- Font Awesome icons (free version)-->
  <script src="https://use.fontawesome.com/releases/v5.13.0/js/all.js" crossorigin="anonymous"></script>
  <script src="/js/util.js"></script>
  <script src="/js/home-page.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
  <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/js/bootstrap.bundle.min.js"></script>
  <!-- Third party plugin JS-->
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.4.1/jquery.easing.min.js"></script>
  <!-- Google fonts-->
  <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css" />
  <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet"
    type="text/css" />
  <!-- Core theme CSS (includes Bootstrap)-->
  <link href="css/styles.css" rel="stylesheet" />
  <link href="css/home-page.css" rel="stylesheet" />
</head>

<body id="page-top">
  <%@include file="/WEB-INF/components/header.html"%>
  <!-- Masthead-->
  <header class="header-bg-img masthead text-white text-center">
    <div class="container align-items-center">
      <!-- Masthead Heading-->
      <h1 class="masthead-heading text-uppercase mb-2">Deal Finder</h1>
      <!-- Search Bar-->
      <form class="mt-5" action="/search">
          <input id="search-input" type="text" placeholder="Search for deals..." name="query">
          <button id="search-btn" type="submit"><i class="fa fa-search"></i></button>
      </form> 
    </div>
  </header>
  <section class="page-section" id="popular-deals">
    <h2 class="page-section-heading text-center text-uppercase text-secondary mb-0">Trending Deals</h2>
    <%@include file="/WEB-INF/components/section-carousel.html"%>
  </section>
  <section class="page-section" id="restaurants-you-follow">
    <h2 class="page-section-heading text-center text-uppercase text-secondary mb-0">Restaurants You Follow</h2>
    <%@include file="/WEB-INF/components/section-carousel.html"%>
  </section>
  <section class="page-section" id="users-you-follow">
    <h2 class="page-section-heading text-center text-uppercase text-secondary mb-0">Users You Follow</h2>
    <%@include file="/WEB-INF/components//section-carousel.html"%>
  </section>
  <section class="page-section" id="tags-you-follow">
    <h2 class="page-section-heading text-center text-uppercase text-secondary mb-0">#Tags You Follow</h2>
    <%@include file="/WEB-INF/components/section-carousel.html"%>
  </section>
  <%@include file="/WEB-INF/components/footer.html"%>
</body>
<script src="js/scripts.js"></script>
</html>
