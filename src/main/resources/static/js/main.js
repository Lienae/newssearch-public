document.addEventListener("DOMContentLoaded", () => {
  // 필터 탭 클릭 시 active 클래스 처리
  document.querySelectorAll(".filter-tabs li").forEach((tab) => {
    tab.addEventListener("click", () => {
      document
          .querySelectorAll(".filter-tabs li")
          .forEach((el) => el.classList.remove("active"));
      tab.classList.add("active");
    });
  });

  // 카테고리 버튼 클릭 시 active 클래스 처리
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
