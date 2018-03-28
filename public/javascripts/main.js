function createTimer(elementName, pomodoroLength) {
    let secondsElapsed = 0;
    const interval = setInterval(() => {
        showTimer(pomodoroLength - secondsElapsed, elementName);
        if (secondsElapsed >= pomodoroLength) {
            clearInterval(interval);
        }
        secondsElapsed += 1;
    }, 1000);
}

function showTimer(remainingSeconds, elementName) {
    const {minutes, seconds} = secondsToTime(remainingSeconds);
    const timerStr = `${minutes}:${seconds}`;
    document.getElementById(elementName).innerHTML = timerStr;
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

createTimer("timer", 25 * 60)