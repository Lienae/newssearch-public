document.addEventListener("DOMContentLoaded", () => {
  const modal = document.getElementById("newsModal");
  const modalIframe = document.getElementById("modalIframe");
  const closeBtn = document.getElementById("closeModal");
  const writePostBtn = document.getElementById("writePostBtn");

  const commentForm = document.getElementById("commentForm");
  const commentInput = document.getElementById("commentInput");
  const commentList = document.getElementById("commentList");
  const commentCount = document.getElementById("commentCount");

  let currentUrl = "";

  // 뉴스 카드 클릭 이벤트
  document.querySelectorAll(".news-card").forEach((card) => {
    card.addEventListener("click", () => {
      const link = card.dataset.link;
      const baseUrl = link.split("?")[0];
      currentUrl = baseUrl;

      modalIframe.src = link;
      modal.style.display = "flex";
      writePostBtn.href = `/boarder/write?newsUrl=${encodeURIComponent(link)}`;
      loadComments(baseUrl);
    });
  });

  // URL 파라미터로 열기
  const params = new URLSearchParams(window.location.search);
  const popupUrl = params.get("url");
  if (popupUrl) {
    const baseUrl = popupUrl.split("?")[0];
    currentUrl = baseUrl;
    modalIframe.src = popupUrl;
    modal.style.display = "flex";
    writePostBtn.href = `/boarder/write?newsUrl=${encodeURIComponent(popupUrl)}`;
    loadComments(baseUrl);
  }

  // 댓글 등록
  commentForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const content = commentInput.value.trim();
    if (!content || !currentUrl) return;

    fetch("/api/v1/comment/create", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ url: currentUrl, content }),
      credentials: "include"
    })
      .then((res) => res.json())
      .then(() => {
        commentInput.value = "";
        loadComments(currentUrl);
      })
      .catch((err) => {
        console.error("댓글 등록 에러:", err);
        alert("로그인 사용자만 이용할 수 있습니다");
      });
  });

  // 댓글 목록 로드
  function loadComments(url) {
    fetch(`/api/v1/comment/list?url=${encodeURIComponent(url)}`)
      .then((res) => res.json())
      .then((data) => {
        commentList.innerHTML = "";
        commentCount.innerText = `총 ${data.length}개의 의견`;

        data.forEach((comment) => {
          const li = document.createElement("li");
          li.className = "comment-item";
          li.dataset.id = comment.id;

          const isAuthor = comment.writerEmail === currentUserEmail;
          const isAdmin = currentUserRoles.includes("ROLE_ADMIN");

          let buttonsHtml = "";
          if (isAuthor) {
            buttonsHtml = `
              <button class="edit-comment">수정</button>
              <button class="delete-comment">삭제</button>
            `;
          } else if (isAdmin) {
            buttonsHtml = `<button class="delete-comment">삭제</button>`;
          } else if (!isAuthor) {
            buttonsHtml = `<button class="report-comment">신고</button>`;
          }

          li.innerHTML = `
            <div class="comment-header">
              <span class="comment-writer">${comment.writerName || "작성자"}</span>
              <span class="comment-date">${comment.createdDate || ""}</span>
            </div>
            <div class="comment-body">
              <div class="comment-content">${comment.content}</div>
              <div class="button-group">${buttonsHtml}</div>
            </div>
          `;
          commentList.appendChild(li);
        });
      })
      .catch((err) => {
        console.error("댓글 불러오기 실패:", err);
        commentCount.innerText = "총 0개의 의견";
      });
  }

  // 댓글 삭제
  commentList.addEventListener("click", async (e) => {
    if (e.target.classList.contains("delete-comment")) {
      const li = e.target.closest("li");
      const commentId = li.dataset.id;
      if (!commentId || !confirm("댓글을 삭제하시겠습니까?")) return;

      try {
        const res = await fetch("/api/v1/comment/remove", {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body: `commentId=${commentId}`
        });
        if (res.ok) {
          loadComments(currentUrl);
        } else {
          alert("삭제 실패");
        }
      } catch (err) {
        alert("요청 중 오류 발생");
      }
    }
  });

  // 댓글 수정 시작
  commentList.addEventListener("click", (e) => {
    if (e.target.classList.contains("edit-comment")) {
      const li = e.target.closest("li");
      const contentDiv = li.querySelector(".comment-content");
      const buttonGroup = li.querySelector(".button-group");
      const originalContent = contentDiv.innerText;

      if (li.querySelector(".edit-input")) return;

      const input = document.createElement("input");
      input.type = "text";
      input.value = originalContent;
      input.className = "edit-input";
      input.style.width = "70%";

      contentDiv.style.display = "none";
      contentDiv.insertAdjacentElement("afterend", input);

      buttonGroup.innerHTML = `
        <button class="save-edit">저장</button>
        <button class="cancel-edit">취소</button>
      `;
    }
  });

  // 댓글 수정 저장 또는 취소
  commentList.addEventListener("click", async (e) => {
    const li = e.target.closest("li");
    const commentId = li.dataset.id;
    const input = li.querySelector(".edit-input");
    const contentDiv = li.querySelector(".comment-content");
    const buttonGroup = li.querySelector(".button-group");

    if (e.target.classList.contains("save-edit")) {
      const newContent = input.value.trim();
      if (!newContent) {
        alert("내용이 비어있습니다.");
        return;
      }

      try {
        const res = await fetch("/api/v1/comment/update", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ id: commentId, content: newContent })
        });
        if (res.ok) {
          loadComments(currentUrl);
        } else {
          alert("수정 실패");
        }
      } catch (err) {
        alert("요청 중 오류 발생");
      }
    }

    if (e.target.classList.contains("cancel-edit")) {
      input.remove();
      contentDiv.style.display = "";
      buttonGroup.innerHTML = `
        <button class="edit-comment">수정</button>
        <button class="delete-comment">삭제</button>
      `;
    }
  });

    // 댓글 신고 버튼 처리
    commentList.addEventListener("click", async (e) => {
      if (e.target.classList.contains("report-comment")) {
        const li = e.target.closest("li");
        const commentId = li.dataset.id;
        if (!commentId) return;

        const confirmed = confirm("이 댓글을 신고하시겠습니까?");
        if (!confirmed) return;

        try {
          const res = await fetch("/api/v1/comment/report", {
            method: "POST",
            headers: {
              "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `commentId=${commentId}`
          });

          if (res.ok) {
            alert("신고가 접수되었습니다.");
          } else {
            const msg = await res.text();
            alert("신고 실패: " + msg);
          }
        } catch (err) {
          alert("요청 중 오류가 발생했습니다.");
        }
      }
    });


  // 모달 닫기
  closeBtn.addEventListener("click", () => {
    modal.style.display = "none";
    modalIframe.src = "";
    clearComments();
  });

  window.addEventListener("click", (e) => {
    if (e.target === modal) {
      modal.style.display = "none";
      modalIframe.src = "";
      clearComments();
    }
  });

  function clearComments() {
    commentList.innerHTML = "";
    commentCount.innerText = "총 0개의 의견";
    commentInput.value = "";
    currentUrl = "";
  }
});
