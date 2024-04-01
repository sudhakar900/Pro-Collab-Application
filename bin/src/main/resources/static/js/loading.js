document.querySelectorAll("form").forEach(function (form) {
  form.addEventListener("submit", function (event) {
    event.preventDefault();
    loadCSS("/css/loadingAnimation.css");
    showLoadingAnimation();
    setTimeout(() => {
      form.submit();
    }, 1000);
  });
});
console.log("Testing")
function loadCSS(url) {
  var link = document.createElement("link");
  link.rel = "stylesheet";
  link.type = "text/css";
  link.href = url;
  document.head.appendChild(link);
}

function showLoadingAnimation() {
  var overlay = document.createElement("div");
  overlay.className = "overlay";
  var loadingAnimation = document.createElement("div");
  loadingAnimation.className = "pan-loader";
  loadingAnimation.innerHTML = `
  <div class="cube-wrapper">
    <div class="cube-folding">
      <span class="leaf1"></span>
      <span class="leaf2"></span>
      <span class="leaf3"></span>
      <span class="leaf4"></span>
    </div>
    <span class="loading" data-name="Loading">Loading</span>
        `;
  overlay.appendChild(loadingAnimation);
  document.body.appendChild(overlay);
}