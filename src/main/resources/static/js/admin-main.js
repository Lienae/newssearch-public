document.addEventListener("DOMContentLoaded", () => {

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

                // 관리자가 작업 목록 확인 완료 -> 작업 수 업데이트
                lastJobCount = data.length;

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
                // 확인 했으니 강조도 제거
                openBtn.classList.remove("has-jobs");
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

    let lastJobCount = 0;

    // 작업이 새로 생기면 float 버튼에 디자인 추가
    function checkPendingJobs() {
        fetch("/api/v1/admin-jobs")
            .then(res => {
                if (res.status === 204) return [];
                return res.json();
            })
            .then(data => {
                const currentCount = data.length;

                if (currentCount > lastJobCount) {
                    openBtn.classList.add("has-jobs");
                } else {
                    openBtn.classList.remove("has-jobs");
                }

                lastJobCount = currentCount;

            })
            .catch(err => console.error("작업 상태 확인 실패:", err));
    }

    // 페이지 진입 후 1회 초기화
    checkPendingJobs();

    // 이후 주기적으로 비교
    setInterval(checkPendingJobs, 30000);


});
