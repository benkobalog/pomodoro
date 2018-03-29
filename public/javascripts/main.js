let timer;
let sound;

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

String.prototype.lpad = function(padString, length) {
    var str = this;
    while (str.length < length)
        str = padString + str;
    return str;
}

function secondsToTime(seconds) {
    const minutes = Math.floor(seconds / 60);
    return {
        minutes: minutes.toString().lpad("0", 2),
        seconds: (seconds - minutes * 60).toString().lpad("0", 2)
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
    console.debug("asd")
}

function playSound() {
    sound.play();
}