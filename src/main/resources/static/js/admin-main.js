document.addEventListener("DOMContentLoaded", () => {
    const openBtn = document.getElementById("openAdminJobsModal");
    const closeBtn = document.getElementById("closeModal");
    const modal = document.getElementById("adminJobsModal");
    const jobList = document.querySelector(".job-list");

    let lastJobCount = 0;

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

    // 모달 열기 및 작업 목록 불러오기
    openBtn.onclick = function () {
        modal.style.display = "block";

        $.ajax({
            url: "/api/v1/admin-jobs",
            method: "GET",
            dataType: "json",
            success: function (data) {
                jobList.innerHTML = "";
                if (!data || data.length === 0) {
                    jobList.innerHTML = "<li>처리해야 할 작업이 없습니다.</li>";
                    return;
                }
                lastJobCount = data.length;
                data.forEach((job) => {
                    const li = document.createElement("li");
                    li.innerHTML = `
            <a href="/admin/main/job?jobId=${job.id}&filter=UNRESOLVED">
              <strong>${job.job}</strong> - <span>${job.targetId}</span>
            </a>
            <span class="meta">${formatDate(job.recordedTime)}</span>
          `;
                    jobList.appendChild(li);
                });
                openBtn.classList.remove("has-jobs");
            },
            statusCode: {
                204: function () {
                    jobList.innerHTML = "<li>처리해야 할 작업이 없습니다.</li>";
                },
            },
            error: function (xhr, status, error) {
                console.error("작업 목록 로드 실패:", error);
                jobList.innerHTML = "<li>목록을 불러오는 데 실패했습니다.</li>";
            },
        });
    };

    // 모달 닫기
    closeBtn.onclick = function () {
        modal.style.display = "none";
    };

    window.onclick = function (event) {
        if (event.target === modal) {
            modal.style.display = "none";
        }
    };

    // 작업 존재 여부 확인 후 플로팅 버튼 상태 반영
    function checkPendingJobs() {
        $.ajax({
            url: "/api/v1/admin-jobs",
            method: "GET",
            dataType: "json",
            success: function (data) {
                const currentCount = data.length;

                if (currentCount > lastJobCount) {
                    openBtn.classList.add("has-jobs");
                } else {
                    openBtn.classList.remove("has-jobs");
                }

                lastJobCount = currentCount;
            },
            statusCode: {
                204: function () {
                    openBtn.classList.remove("has-jobs");
                    lastJobCount = 0;
                },
            },
            error: function (xhr, status, error) {
                console.error("작업 상태 확인 실패:", error);
            },
        });
    }

    checkPendingJobs();
    setInterval(checkPendingJobs, 30000);
});
