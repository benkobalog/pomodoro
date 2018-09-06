import {States} from "./model/States";
import {User} from "./model/User";
import { Pomodoro } from "./logic/Pomodoro";

export function setButtons(pomodoroState: any, pomodoro: Pomodoro) {
    console.info("Setting buttons: " + JSON.stringify(pomodoroState));
    switch(pomodoroState) {
        case States.Idle: {
            // $("#stop-button" ).prop("disabled", true);
            $("#start-button").html("Start Pomodoro");
            $("#stop-button" ).prop("disabled", true);
            // $("#start-button").prop("disabled", false);
            $("#pomodoro-timer-wrapper").css("background-color", 'White');
            $("#start-button").click(() => pomodoro.startPomodoro());
            break;
        }
        case States.Running: {
            // $("#stop-button" ).prop("disabled", false);
            // $("#start-button").prop("disabled", true);
            $("#stop-button" ).prop("disabled", false);
            $("#start-button").html("Start Break");
            $("#stop-button" ).html("Stop Session");
            $("#pomodoro-timer-wrapper").css("background-color", 'LightGrey');
            $("#start-button").click(() => pomodoro.endPomodoro());
            $("#stop-button").click(() => pomodoro.stopAll());
            break;
        }
        case States.Break: {
            $("#stop-button" ).prop("disabled", false);
            $("#stop-button" ).html("Stop Session");
            $("#pomodoro-timer-wrapper").css("background-color", 'GainsBoro');
            $("#stop-button").click(() => pomodoro.stopAll());
            break;
        }
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