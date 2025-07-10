document.addEventListener("DOMContentLoaded", () => {
  const tabButtons = document.querySelectorAll(".filter-tabs li");
  const issueTab = document.querySelector(".news-section"); // 이슈별 영역
  const companyTab = document.getElementById("company-tab");

  // 탭 클릭 시 active 클래스 전환 + 콘텐츠 표시 전환
  tabButtons.forEach((btn, idx) => {
    btn.addEventListener("click", () => {
      tabButtons.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");

      if (idx === 0) {
        issueTab.style.display = "block";
        companyTab.style.display = "none";
      } else {
        issueTab.style.display = "none";
        companyTab.style.display = "block";
      }
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
