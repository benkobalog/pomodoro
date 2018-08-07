import {HttpClient} from "./HttpClient";
import {Pomodoro} from "./logic/Pomodoro";
import {PomodoroStats} from "./logic/PomodoroStats";

let sound: HTMLAudioElement;

const pomodoroLength = 25 * 60;
const backendAddress = "http://localhost:9001";
const username = "dev@mail.com";
const password = "1234";
const authHeader = { Authorization : 'Basic ' + btoa(username + ":" + password) };
const client = new HttpClient(backendAddress, authHeader);
const pStats = new PomodoroStats(client);
const pomodoro = new Pomodoro(client, pomodoroLength, pStats);

window.onload = () => {
    pomodoro.loadState();
    sound = new Audio("assets/sounds/tool.mp3");
    pStats.updateLastPomodoros();
    document.getElementById("start-button").onclick =
        () => pomodoro.startTimer();
    document.getElementById("stop-button").onclick = 
        () => pomodoro.stopTimer();
}

function playSound() {
    sound.play();
}