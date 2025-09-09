document.addEventListener("DOMContentLoaded", () => {
    const scrollTopPanelBtn = document.getElementById("scroll-top-panel-btn");
    const headers = document.getElementById("headers");
    const topPanel = document.getElementById('top-panel');
    scrollTopPanelBtn.addEventListener("click", () => {
        if (scrollTopPanelBtn.innerText === 'v') {
            scrollTopPanelBtn.innerText = '^';
            headers.style.display = "flex"  ;
        } else {
            scrollTopPanelBtn.innerText = 'v';
            headers.style.display = "none";
        }

        topPanel.classList.toggle('open');
    });
});