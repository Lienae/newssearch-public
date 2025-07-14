function search() {
  const query = document.querySelector("input[name='query']").value;
  const category = "ALL"; // 기본값
  const mediaCompany = "ALL"; // 기본값
  const searchUrl = `/news/list?query=${encodeURIComponent(query)}&category=${category}&mediaCompany=${mediaCompany}`;
  window.location.href = searchUrl;
}

document.addEventListener("DOMContentLoaded", () => {
  //  탭 전환: 필터 탭 클릭 시 콘텐츠 전환
  const tabButtons = document.querySelectorAll(".filter-tabs li");
  const issueTab = document.querySelector(".news-section");
  const companyTab = document.getElementById("company-tab");

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

  //  카테고리 버튼 클릭 시 active 클래스 처리
  document.querySelectorAll(".category-buttons a").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      document.querySelectorAll(".category-buttons a").forEach((el) => {
        el.classList.remove("active");
      });
      btn.classList.add("active");
    });
  });

  //  뉴스 카드 클릭 시 상세 페이지 이동
  document.querySelectorAll(".news-card").forEach((card) => {
    card.addEventListener("click", () => {
      const category = card.dataset.category;
      const media = card.dataset.media;
      const url = encodeURIComponent(card.dataset.url);

      if (category && media && url) {
        window.location.href = `/news/list?category=${category}&mediaCompany=${media}&url=${url}`;
      }
    });
  });

  //  검색 버튼 클릭 시 검색 실행
  const searchBtn = document.querySelector(".search-bar button");
  if (searchBtn) {
    searchBtn.addEventListener("click", search);
  }

});
