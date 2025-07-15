document.addEventListener("DOMContentLoaded", () => {
  // ⭐ 카테고리 버튼 클릭 시 category 파라미터만 바꾸고 나머지 유지
  document.querySelectorAll(".category-buttons a").forEach((btn) => {
    btn.addEventListener("click", (event) => {
      event.preventDefault();

      const category = btn.getAttribute("data-category");
      const url = new URL(window.location.href);
      const params = url.searchParams;

      params.set("category", category); // category만 교체

      // 페이지 번호 초기화
      params.set("page", 0);

      // 이동
      window.location.href = `${url.pathname}?${params.toString()}`;
    });
  });

  // 기존 Editor's Note 토글 버튼 로직 유지
  const toggleBtn = document.getElementById("toggleFilterBtnEN");
  if (toggleBtn) {
    const currentUrl = new URL(window.location.href);
    const currentParams = currentUrl.searchParams;
    const currentFilter = currentParams.get("filter");

    toggleBtn.textContent = currentFilter === "admin" ? "일반 커뮤니티" : "Editor's Note";

    toggleBtn.addEventListener("click", () => {
      const newUrl = new URL(currentUrl.pathname, window.location.origin);

      for (const [key, value] of currentParams.entries()) {
        if (key !== "filter") {
          newUrl.searchParams.set(key, value);
        }
      }

      newUrl.searchParams.set("page", 0); // 필터 바꿀 때도 page=0으로 초기화

      if (currentFilter !== "admin") {
        newUrl.searchParams.set("filter", "admin");
      } else {
        newUrl.searchParams.delete("filter");
      }

      window.location.href = newUrl.toString();
    });
  }
});
