/**
 * Checks if the dates of the form is ordered and displays error message.
 * @param {HTMLInputElement} start input element which contains the start date
 * @param {HTMLInputElement} end input element which contains the end date
 * @param {HTMLDivElement} errorMessage div of error message to display if date
 * is not ordered
 * @return {boolean}
 */
function checkDatesOrdered(start, end, errorMessage) {
  document.createElement('input');
  if (start.value && end.value && start.value > end.value) {
    errorMessage.style.display = 'block';
    return false;
  }
  errorMessage.style.display = 'none';
  return true;
}
