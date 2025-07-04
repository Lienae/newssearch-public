document.addEventListener("DOMContentLoaded", () => {
  // 카테고리 버튼 active 토글
  document.querySelectorAll(".category-buttons a").forEach((btn) => {
    btn.addEventListener("click", () => {
      document
        .querySelectorAll(".category-buttons a")
        .forEach((el) => el.classList.remove("active"));
      btn.classList.add("active");
    });
  });

  const toggleBtn = document.getElementById("toggleFilterBtn");
  if (!toggleBtn) return;

  const baseUrl = "/boarder/list"; // localhost 생략 가능

  // 현재 URL 파라미터 읽기
  const currentUrl = new URL(window.location.href);
  const currentParams = currentUrl.searchParams;
  const currentFilter = currentParams.get("filter");

  // 초기 버튼 텍스트 설정
  toggleBtn.textContent = currentFilter === "admin" ? "일반 커뮤니티" : "Editor's Note";

  toggleBtn.addEventListener("click", () => {
    const newUrl = new URL(baseUrl, window.location.origin);
    const category = currentParams.get("category");

    // category가 있으면 같이 붙여줌
    if (category && category !== "ALL") {
      newUrl.searchParams.set("category", category);
    }

    // 필터 전환
    if (currentFilter === "admin") {
      // filter 파라미터 제거: 일반 커뮤니티
      // 아무 것도 안붙임
    } else {
      // filter=admin 추가: 에디터 게시판
      newUrl.searchParams.set("filter", "admin");
    }

    // 최종 이동
    window.location.href = newUrl.toString();
  });
});
