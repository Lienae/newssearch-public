document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("newsModal");
  const modalIframe = document.getElementById("modalIframe");
  const closeBtn = document.getElementById("closeModal");
  const writePostBtn = document.getElementById("writePostBtn");

  // 뉴스 카드 클릭 -> 모달 열기
  document.querySelectorAll(".news-card").forEach((card) => {
    card.addEventListener("click", () => {
      const link = card.dataset.link;
      modalIframe.src = link;
      modal.style.display = "flex";

      // Spring Boot에서 boarder-write URL 절대경로 권장
      writePostBtn.href = `/boarder/write?newsUrl=${encodeURIComponent(link)}`;
    });
  });

  // 모달 닫기 (닫기 버튼)
  closeBtn.addEventListener("click", () => {
    closeModal(modal, modalIframe);
  });

  // 모달 닫기 (배경 클릭)
  window.addEventListener("click", (e) => {
    if (e.target === modal) {
      closeModal(modal, modalIframe);
    }
  });

  // 카테고리 버튼 활성화
  document.querySelectorAll(".category-buttons a").forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      document.querySelectorAll(".category-buttons a").forEach((el) => el.classList.remove("active"));
      btn.classList.add("active");
    });
  });
});

function closeModal(modal, iframe) {
  modal.style.display = "none";
  iframe.src = "";
}
