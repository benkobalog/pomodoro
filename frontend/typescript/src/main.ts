let timer: number;
let sound: HTMLAudioElement;
let pomodoroState:States;
const pLength = 25 * 60;

enum States {
    Idle, 
    Running,
    Break
}

const backendAddress = "http://localhost:9001";

function updateLastPomodoros() {
    httpGet("/pomodoro", data => {
            const tableRow = (pomodoroData: any) => {
                return `<tr>` +
                    `<td>${pomodoroData.duration}</td>` +
                    `<td>${pomodoroData.started}</td>` +
                    `<td>${pomodoroData.finished}</td>` +
                    `</tr>`;
            }

            const tableContent = data.map(tableRow).join("");
            $("#lastPomodorosTable").html(tableContent);
            console.log("Updated pomodoros");
    });
}

function savePomodoroStart() {
    httpPost("/pomodoroStart", response => console.info("Result of pomodoro start: " + response));
}

function savePomodoroFinish() {
    httpPatch("/pomodoroFinish", response => console.info("Result of pomodoro finish: " + response));
}

function createTimer(elementName: string, pomodoroLength: number) {
    let secondsElapsed = 0;
    const interval = window.setInterval(() => {
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

function drawTimer(remainingSeconds: number, elementName: string) {
    const {minutes, seconds} = secondsToTime(remainingSeconds);
    const timerStr = `${minutes}:${seconds}`;
    $("#" + elementName).html(timerStr);
    document.title = timerStr;
}

function secondsToTime(seconds: number) {
    const minutes = Math.floor(seconds / 60);
    return {
        minutes: minutes.toString().padStart(2, "0"),
        seconds: (seconds - minutes * 60).toString().padStart(2, "0")
    };
}

function resetTimer(elementName:string, seconds: number) {
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
    updateLastPomodoros();
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
    httpGet("/pomodoroState", (data: any) => {
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

const username = "dev2@mail.com";
const password = "1234";
const authHeader = { Authorization : 'Basic ' + btoa(username + ":" + password) };

function httpGet(path: string, fn: (a: any) => void) {
    const options: RequestInit = { method: "GET", headers: authHeader };
    console.log(options);
    return fetch(backendAddress + path, options)
        .then(r => r.json() )
        .then(data => fn(data))
        .catch(err => console.log(err));
}

function httpPost(path: string, fn: (r: Response) => void) {
    const options: RequestInit = { method: "POST", mode: "cors", headers: authHeader };
    return fetch(backendAddress + path, options)
        .then(fn)
        .catch(err => console.log(err));
}

function httpPatch(path: string, fn: (r: Response) => void) {
    const options: RequestInit = { method: "PATCH", mode: "cors", headers: authHeader };
    return fetch(backendAddress + path, options)
        .then(fn)
        .catch(err => console.log(err));
}

window.onload = () => {
    loadStateFromBackend();
    sound = new Audio("assets/sounds/tool.mp3");
    updateLastPomodoros();
    document.getElementById("start-button").onclick = startTimer;
    document.getElementById("stop-button").onclick = stopTimer;
}

function playSound() {
    sound.play();
}