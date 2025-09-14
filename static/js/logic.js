// import {GraphDrawer} from "./net/graphCsv.js";

let selected = null;

document.addEventListener("DOMContentLoaded", () => {
    const getUrl = "http://localhost:2909/api/shoots"
    fetch(getUrl, {
        method: "GET"
    }).then(request => {
        return request.json()
    }).then(data => {
        data.forEach(shoot => {
            addToTable(shoot);
        })
    }).catch(error => {
        console.log(error)
    });


    const scrollTopPanelBtn = document.getElementById("scroll-top-panel-btn");
    const headers = document.getElementById("headers");
    const topPanel = document.getElementById('top-panel');
    scrollTopPanelBtn.addEventListener("click", () => {
        if (scrollTopPanelBtn.innerText === '↓') {
            scrollTopPanelBtn.innerText = '↑';
            headers.style.display = "flex";
        } else {
            scrollTopPanelBtn.innerText = '↓';
            headers.style.display = "none";
        }

        topPanel.classList.toggle('open');
    });

    const xButtons = document.querySelectorAll('.x-btn');

    xButtons.forEach(button => {
        button.addEventListener("click", () => {
            xButtons.forEach(b => b.classList.remove("selected"))
            button.classList.add("selected")
            selected = button.textContent;
        });
    });

    const rSelect = document.getElementById("r-select");

    const sendBtn = document.getElementById("send-btn");
    const sendError = document.getElementById("send-error");
    const xError = document.getElementById("x-error");
    const yInput = document.getElementById("y-input");
    const yError = document.getElementById("y-error");

    const url = "http://localhost:2909/api/shoot"
    sendBtn.addEventListener("click", () => {
        const xVal = selected;
        const yVal = yInput.value;
        const rVal = rSelect.value;

        const x = checkX(xError);
        const y = checkY(yVal, yError);

        if (!x || !y) return;

        const data = {
            x: xVal,
            y: yVal,
            r: rVal
        }

        fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }).then(response => {
            return response.json();
        }).then(json => {
            addToTable(json);
        }).catch(err => {
            sendError.style.display = "block";
            setTimeout(() => {
                sendError.style.display = "none";
            }, 5000);
            console.log(err);
        });
    });
})
;

function addToTable(data) {
    const resTable = document.getElementById("res-table");
    const row = resTable.insertRow(2);
    row.insertCell(0).textContent = data.x;
    row.insertCell(1).textContent = data.y;
    row.insertCell(2).textContent = data.r;
    row.insertCell(3).textContent = data.duration;
    row.insertCell(4).textContent = data.date;
    row.insertCell(5).textContent = data.check ? "Y" : "N";
}

function checkY(str, error) {
    if (typeof str !== "string" || str.trim() === "" || isNaN(str) || isNaN(Number(str))) {
        error.style.display = "block";
        error.textContent = "❌ Введите в формате числа (от -3 до 3)";
        return false;
    } else {
        if (-3 <= Number(str) && Number(str) <= 3) {
            const decimalPart = str.split('.')[1];
            if (decimalPart && decimalPart.length > 10) {
                error.style.display = "block";
                error.textContent = "❌ Слишком много цифр после точки (макс. 10)";
                return false;
            } else {
                error.style.display = "none";
                return true;
            }
        } else {
            error.style.display = "block";
            error.textContent = "❌ Введите в формате числа (от -3 до 3)";
            return false;
        }
    }
}

function checkX(error) {
    if (selected === null) {
        error.style.display = "block";
        return false;
    } else {
        error.style.display = "none";
        return true;
    }
}
