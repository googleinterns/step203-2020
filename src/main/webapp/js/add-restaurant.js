/*
 * Preview the image when a file is uploaded
 */
const reader = new FileReader();
reader.onload = function(e) {
  $('#img-preview').attr('src', e.target.result);
};
$('#img-input').change((event) => {
  const input = event.currentTarget;
  if (input.files && input.files[0]) {
    reader.readAsDataURL(input.files[0]);
    $('#img-preview').show();
  } else {
    $('#img-preview').hide();
  }
});

/*
 * Form validation
 */
const form = document.getElementsByClassName('needs-validation')[0];
form.addEventListener('submit', (event) => {
  if (form.checkValidity() === false) {
    event.preventDefault();
    event.stopPropagation();
  }
  const validateGroup = form.querySelectorAll('.validate-me');
  validateGroup.forEach((element) => {
    element.classList.add('was-validated');
  });
});
