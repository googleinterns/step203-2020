/* eslint-disable no-unused-vars */
/**
 * Adds a function to window onload event.
 * @param {function} func The function to be executed.
 */
function addLoadEvent(func) {
  const oldonload = window.onload;
  if (typeof oldonload == 'function') {
    window.onload = function() {
      oldonload();
      func();
    };
  } else {
    window.onload = func;
  }
}
