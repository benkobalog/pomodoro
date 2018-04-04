let timer;
let sound;

function updateLastPomodoros() {
    $.get("/pomodoro", function(data, status){
        const tableRow = (pomodoroData) => { 
            return `<tr>` +
                `<td>${pomodoroData.duration}</td>` +
                `<td>${pomodoroData.started}</td>` +
                `<td>${pomodoroData.finished}</td>` +
                `</tr>`
        }
        const tableContent = data.map(tableRow).join("");
        $("#lastPomodorosTable").html(tableContent);
    });
}

function createTimer(elementName, pomodoroLength) {
    let secondsElapsed = 0;
    const interval = setInterval(() => {
        drawTimer(pomodoroLength - secondsElapsed, elementName);
        if (secondsElapsed >= pomodoroLength) {
            clearInterval(interval);
            playSound();
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
    timer = createTimer('pomodoro-timer', 25 * 60);
    $("#stop-button" ).prop("disabled", false);    
    $("#start-button").prop("disabled", true);
}

function stopTimer() {
    resetTimer("pomodoro-timer", 25*60);
    sound.pause();
    $("#stop-button" ).prop("disabled", true);
    $("#start-button").prop("disabled", false);
}

window.onload = () => {
    resetTimer("pomodoro-timer", 25*60);
    sound = new Audio("assets/sounds/tool.mp3");
    $("#stop-button").prop("disabled", true);
    updateLastPomodoros();
}

function playSound() {
    sound.play();
}