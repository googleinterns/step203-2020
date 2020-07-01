<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
  <meta name="description" content="" />
  <meta name="author" content="" />
  <title>Profile</title>
  <script src="js/user.js"></script>
  <link href="css/deal.css" rel="stylesheet" />
  <link href="tagsinput/tagsinput.css" rel="stylesheet" />
  <!-- Font Awesome icons (free version)-->
  <script src="https://use.fontawesome.com/releases/v5.13.0/js/all.js" crossorigin="anonymous"></script>
  <!-- Google fonts-->
  <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet"/>
  <link href="https://fonts.googleapis.com/css?family=Lato:400,700,400italic,700italic" rel="stylesheet" />
  <!-- Core theme CSS (includes Bootstrap)-->
  <link href="css/styles.css" rel="stylesheet" />
</head>


<body id="page-t">
  <%@include file="/WEB-INF/components/header.html"%>
  <div id="user-profile" class="page-section">
    <div class="container">
      <div id="profile" class="row mb-5">
        <div class="col-sm-4 col-lg-3">
          <img id="profile-photo" class="img-fluid mb-4" alt="profile-photo"/>
          <button id="follow-btn" type="button" class="btn btn-primary">Follow</button>
          <button id="edit-profile-btn" type="button" class="btn btn-primary">
            Edit profile
          </button>
        </div>
        <div class="col-sm-8 col-lg-9">
          <h3 id="username">
          </h3>
          <h6 class="text-secondary">
            Name: <span id="name"></span>
          </h6>
          <h6 class="text-secondary">
            Email: <span id="email"></span>
          </65>
          <div class="mt-2">
            Bio: <span id="bio"></span>
          </div>

          <div id="tags" class="mt-2">
          </div>
        </div>
      </div>

      <form id="profile-form" class="row mb-5" enctype="multipart/form-data" hidden>
        <div class="col-sm-4 col-lg-3">
          <img id="profile-photo-preview" class="img-fluid mb-4" alt="profile-photo"/>
        </div>
        <div class="col-sm-8 col-lg-9">
          <div class="form-group row">
            <label for="username-input" class="col-sm-2 col-form-label">Username</label>
            <div class="col-sm-10">
              <input type="text" class="form-control" name="username" id="username-input" placeholder="Enter a username">
            </div>
          </div>
          <div class="form-group row">
            <label for="profile-photo-input" class="col-sm-2 col-form-label">Profile photo</label>
            <input type="file" 
                class="form-control-file col-sm-10""
                name="picture"
                id="profile-photo-file" 
                onchange="profilePhotoPreview(this);">
          </div>
          <div class="form-group row">
            <label for="name-input" class="col-sm-2 col-form-label">Name</label>
            <div class="col-sm-10">
              <input type="text" class="form-control" name="name" id="name-input">
            </div>
          </div>
          <div class="form-group row">
            <label for="email-input" class="col-sm-2 col-form-label">Email</label>
            <div class="col-sm-10">
              <input type="text" class="form-control" name="email" id="email-input" readonly>
            </div>
          </div>
          <div class="form-group row">
            <label for="bio-input" class="col-form-label col-sm-2">Bio</label>
            <div class="col-sm-10">
              <textarea class="form-control" name="bio" id="bio-input" maxlength="150"></textarea>
            </div>
          </div>
          <div class="form-group row">
            <label for="tags-input" class="col-form-label col-sm-2">Tags</label>
            <div class="col-sm-10">
              <input id="tags-input" class="form-control" name="tags" type="text" data-role="tagsinput">
              <small class="form-text text-muted">
                Start typing and hit enter to enter tags.
              </small>
            </div>
          </div>
          <button type="submit" class="btn btn-primary">Save</button>
          <button type="button" class="btn btn-primary mx-2" onclick="cancelProfileEditing();">Cancel</button>
        </div>
      </form>
      <nav>
        <div class="nav nav-tabs mb-2" id="nav-tab" role="tablist">
          <a class="nav-item nav-link active" id="nav-deals-tab" data-toggle="tab" href="#deals" role="tab" aria-controls="deals" aria-selected="true">Deals</a>
          <a class="nav-item nav-link" id="nav-followers-tab" data-toggle="tab" href="#followers" role="tab" aria-controls="followers" aria-selected="false">Followers</a>
          <a class="nav-item nav-link" id="nav-following-tab" data-toggle="tab" href="#following" role="tab" aria-controls="following" aria-selected="false">Following</a>
          <a class="nav-item nav-link" id="nav-restaurants-tab" data-toggle="tab" href="#restaurants" role="tab" aria-controls="restaurants" aria-selected="false">Restaurants Followed</a>
        </div>
      </nav>
      <div class="tab-content" id="nav-tabContent">
        <div class="tab-pane fade show active" id="deals" role="tabpanel" aria-labelledby="nav-deals-tab"></div>
        <div class="tab-pane fade" id="followers" role="tabpanel" aria-labelledby="nav-followers-tab"></div>
        <div class="tab-pane fade" id="following" role="tabpanel" aria-labelledby="nav-following-tab"></div>
        <div class="tab-pane fade" id="restaurants" role="tabpanel" aria-labelledby="nav-restaurants-tab"></div>
      </div>
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
</body>

</html>
