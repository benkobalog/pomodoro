import {States} from "./model/States";
import {User} from "./model/User";

export function setButtons(pomodoroState: any) {
    console.info("Setting buttons: " + JSON.stringify(pomodoroState));
    if(pomodoroState == States.Idle) {
        $("#stop-button" ).prop("disabled", true);
        $("#start-button").prop("disabled", false);
    } else if(pomodoroState == States.Running) {
        $("#stop-button" ).prop("disabled", false);
        $("#start-button").prop("disabled", true);
    }
}

export function drawTimer(remainingSeconds: number) {
    const {minutes, seconds} = secondsToTime(remainingSeconds);
    const timerStr = `${minutes}:${seconds}`;
    $("#pomodoro-timer").html(timerStr);
    document.title = timerStr;
}

function secondsToTime(seconds: number) {
    const minutes = Math.floor(seconds / 60);
    return {
        minutes: minutes.toString().padStart(2, "0"),
        seconds: (seconds - minutes * 60).toString().padStart(2, "0")
    };
}

export function drawSettings(user: User) {
    $("#pomodoro-length").val(user.pomodoroSeconds / 60);
    $("#break-length").val(user.breakSeconds / 60);
}