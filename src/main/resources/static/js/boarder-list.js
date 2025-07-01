document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".category-buttons a").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      document
        .querySelectorAll(".category-buttons a")
        .forEach((el) => el.classList.remove("active"));
      btn.classList.add("active");
    });
  });
});
