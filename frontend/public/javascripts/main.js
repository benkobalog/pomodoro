let timer;
let sound;
let pomodoroState;
const pLength = 25 * 60;

const States = Object.freeze({
    "Idle": "Idle",
    "Running": "Running",
    "Break": "Break"
});

const backendAddress = "http://localhost:9001";

function updateLastPomodoros() {
    httpGet("/pomodoro", data => {
            const tableRow = (pomodoroData) => {
                return `<tr>` +
                    `<td>${pomodoroData.duration}</td>` +
                    `<td>${pomodoroData.started}</td>` +
                    `<td>${pomodoroData.finished}</td>` +
                    `</tr>`;
            }

            const tableContent = data.map(tableRow).join("");
            $("#lastPomodorosTable").html(tableContent);
    });
}

function savePomodoroStart() {
    httpPost("/pomodoroStart", response => console.info("Result of pomodoro start: " + response));
}

function savePomodoroFinish() {
    httpPatch("/pomodoroFinish", response => console.info("Result of pomodoro finish: " + response));
}

function createTimer(elementName, pomodoroLength) {
    let secondsElapsed = 0;
    const interval = setInterval(() => {
        drawTimer(pomodoroLength - secondsElapsed, elementName);
        if (secondsElapsed >= pomodoroLength) {
            clearInterval(interval);
            playSound();
            stopTimer();
        }
        secondsElapsed += 1;
    }, 1000);
    return interval;
}

function drawTimer(remainingSeconds, elementName) {
    const {minutes, seconds} = secondsToTime(remainingSeconds);
    const timerStr = `${minutes}:${seconds}`;
    $("#" + elementName).html(timerStr);
    document.title = timerStr;
}

function secondsToTime(seconds) {
    const minutes = Math.floor(seconds / 60);
    return {
        minutes: minutes.toString().padStart(2, "0"),
        seconds: (seconds - minutes * 60).toString().padStart(2, "0")
    };
}

function resetTimer(elementName, seconds) {
    console.debug("Resetting timer");
    drawTimer(seconds, elementName);
    if (timer != null) {
        clearInterval(timer);
    }
}

function startTimer() {
    pomodoroState = States.Running;
    timer = createTimer('pomodoro-timer', pLength);
    savePomodoroStart();
    setButtons();
}

function stopTimer() {
    pomodoroState = States.Idle;
    resetTimer("pomodoro-timer", pLength);
    sound.pause();
    savePomodoroFinish();
    setButtons();
}

function setButtons() {
    console.info("Setting buttons: " + JSON.stringify(pomodoroState));
    if(pomodoroState == States.Idle) {
        $("#stop-button" ).prop("disabled", true);
        $("#start-button").prop("disabled", false);
    } else if(pomodoroState == States.Running) {
        $("#stop-button" ).prop("disabled", false);    
        $("#start-button").prop("disabled", true);
    }
}

function loadStateFromBackend() {
    httpGet("/pomodoroState", data => {
        if(data.Idle) {
            pomodoroState = States.Idle;
            resetTimer("pomodoro-timer", pLength);
        } else if (typeof(data.Running) == 'object') {
            pomodoroState = States.Running;
            resetTimer("pomodoro-timer", pLength - data.Running.secondsElapsed);
            timer = createTimer("pomodoro-timer", pLength - data.Running.secondsElapsed);
        }
        setButtons();
    });
}

function httpGet(path, fn) {
    const options = { method: "GET" };
    return fetch(backendAddress + path, options)
        .then(r => r.json())
        .then(data => fn(data))
        .catch(err => console.log(err));
}

function httpPost(path, fn) {
    const options = { method: "POST", mode: "cors" };
    return fetch(backendAddress + path, options)
        .then(response => fn(response))
        .catch(err => console.log(err));
}

function httpPatch(path, fn) {
    const options = { method: "PATCH", mode: "cors" };
    return fetch(backendAddress + path, options)
        .then(response => fn(response))
        .catch(err => console.log(err));
}

window.onload = () => {
    loadStateFromBackend();
    sound = new Audio("assets/sounds/tool.mp3");
    updateLastPomodoros();
}

function playSound() {
    sound.play();
}