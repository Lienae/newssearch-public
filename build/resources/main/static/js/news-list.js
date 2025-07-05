document.addEventListener("DOMContentLoaded", function () {
  const modal = document.getElementById("newsModal");
  const modalIframe = document.getElementById("modalIframe");
  const closeBtn = document.getElementById("closeModal");
  const writePostBtn = document.getElementById("writePostBtn");

  const commentForm = document.getElementById("commentForm");
  const commentInput = document.getElementById("commentInput");
  const commentList = document.getElementById("commentList");
  const commentCount = document.getElementById("commentCount");

  let currentUrl = ""; // 현재 선택된 뉴스 기사 URL

  // 뉴스 카드 클릭 시
  document.querySelectorAll(".news-card").forEach(function (card) {
    card.addEventListener("click", function () {
      const link = card.dataset.link;
      const baseUrl = link.split('?')[0]; // URL 정제

      currentUrl = baseUrl;

      // 1. 기사 iframe 설정
      modalIframe.src = link;
      modal.style.display = "flex";

      // 2. 관련 글 작성 링크 설정
      writePostBtn.href = `/boarder/write?newsUrl=${encodeURIComponent(link)}`;

      // 3. 댓글 목록 로드
      loadComments(baseUrl);
    });
  });

  // 댓글 등록 이벤트 (중복 방지)
  commentForm.addEventListener("submit", function (e) {
    e.preventDefault();
    const content = commentInput.value.trim();
    if (!content || !currentUrl) return;

    fetch("/api/v1/comment/create", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ url: currentUrl, content: content })
    })
      .then(res => {
        if (!res.ok) throw new Error("댓글 등록 실패");
        return res.json();
      })
      .then(() => {
        commentInput.value = "";
        loadComments(currentUrl);
      })
      .catch(err => {
        console.error("댓글 등록 에러:", err);
        alert("댓글 등록 중 오류가 발생했습니다.");
      });
  });

  // 댓글 목록 불러오기
  function loadComments(url) {
    fetch(`/api/v1/comment/list?url=${encodeURIComponent(url)}`)
      .then(res => res.json())
      .then(data => {
        commentList.innerHTML = "";
        commentCount.innerText = `총 ${data.length}개의 의견`;

        data.forEach(comment => {
          const li = document.createElement("li");
          li.dataset.id = comment.id;

          li.innerHTML = `
                    <span><strong>${comment.writerName || "작성자"}</strong> : ${comment.content}</span>
                    <button class="delete-comment" style="margin-left: 10px;">삭제</button>
                  `;
          commentList.appendChild(li);
        });
      })
      .catch(err => {
        console.error("댓글 불러오기 실패:", err);
        commentCount.innerText = "총 0개의 의견";
      });
  }

    // 댓글 삭제 이벤트 (댓글 내 삭제 버튼 클릭 시)
    commentList.addEventListener("click", async (e) => {
      if (e.target.classList.contains("delete-comment")) {
        const li = e.target.closest("li");
        const commentId = li.dataset.id;

        if (!commentId) return;

        const confirmed = confirm("댓글을 삭제하시겠습니까?");
        if (!confirmed) return;

        try {
          const res = await fetch("/api/v1/comment/remove", {
            method: "POST",
            headers: {
              "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `commentId=${commentId}`
          });

          if (res.ok) {
            li.remove(); // 삭제 성공 시 UI에서 제거
          } else {
            const text = await res.text();
            alert("삭제 실패: " + text);
          }
        } catch (err) {
          alert("요청 중 오류 발생");
        }
      }
    });

  // 모달 닫기
  closeBtn.addEventListener("click", function () {
    modal.style.display = "none";
    modalIframe.src = "";
    clearComments();
  });

  window.addEventListener("click", function (e) {
    if (e.target === modal) {
      modal.style.display = "none";
      modalIframe.src = "";
      clearComments();
    }
  });

  // 댓글 영역 초기화
  function clearComments() {
    commentList.innerHTML = "";
    commentCount.innerText = "총 0개의 의견";
    commentInput.value = "";
    currentUrl = "";
  }
});