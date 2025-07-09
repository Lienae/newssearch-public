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

    // 모달 열기 및 Ajax로 작업 목록 불러오기
    const openBtn = document.getElementById("openAdminJobsModal");
    const closeBtn = document.getElementById("closeModal");
    const modal = document.getElementById("adminJobsModal");
    const jobList = document.querySelector(".job-list");

    openBtn.onclick = function () {
        modal.style.display = "block";
        fetch("/api/v1/admin-jobs")
            .then((res) => {
                    if (res.status === 204){
                        return null;
                    }
                    return res.json();
                })
            .then((data) => {
                jobList.innerHTML = ""; // 기존 리스트 초기화

                if (!data || data.length === 0) {
                    jobList.innerHTML = "<li>처리해야 할 작업이 없습니다.</li>";
                    return;
                }

                data.forEach((job) => {
                    const li = document.createElement("li");
                    li.innerHTML = `
                      <a href="/admin/main/job?jobId=${job.id}&filter=UNRESOLVED">
                        <strong>${job.job}</strong> - <span>${job.url}</span>
                      </a>
                      <span class="meta">${formatDate(job.recordedTime)}</span>
                    `;
                    jobList.appendChild(li);
                });
            })
            .catch((err) => {
                console.error("작업 목록 로드 실패:", err);
                jobList.innerHTML = "<li>목록을 불러오는 데 실패했습니다.</li>";
            });
    };


    // 모달 닫기
    closeBtn.onclick = function () {
        modal.style.display = "none";
    };

    // 모달 바깥 클릭 시 닫기
    window.onclick = function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    };

    // 날짜 포맷 함수
    function formatDate(dateTimeString) {
        const date = new Date(dateTimeString);
        const y = date.getFullYear();
        const m = `${date.getMonth() + 1}`.padStart(2, "0");
        const d = `${date.getDate()}`.padStart(2, "0");
        const h = `${date.getHours()}`.padStart(2, "0");
        const min = `${date.getMinutes()}`.padStart(2, "0");
        return `${y}-${m}-${d} ${h}:${min}`;
    }
});
