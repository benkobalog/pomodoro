import {HttpClient} from "./HttpClient";
import {Pomodoro} from "./logic/Pomodoro";
import {PomodoroStats} from "./logic/PomodoroStats";
import {TokenData} from "./model/TokenData";

let sound: HTMLAudioElement;

const pomodoroLength = 10;
const backendAddress = "http://localhost:9001";

function parseJwt (token: string) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace('-', '+').replace('_', '/');
    return JSON.parse(window.atob(base64)).data;
};

window.onload = () => {
    const tokenData = new TokenData(parseJwt(document.cookie.split('=')[1]));
    const authHeader = { Authorization : 'Basic ' + btoa(tokenData.email + ":" + tokenData.token) };
    const client = new HttpClient(backendAddress, authHeader);
    const pStats = new PomodoroStats(client);
    const pomodoro = new Pomodoro(client, pomodoroLength, pStats);

    console.log(tokenData);
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