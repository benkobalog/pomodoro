import {HttpClient} from "./HttpClient";
import {Pomodoro} from "./logic/Pomodoro";
import {PomodoroStats} from "./logic/PomodoroStats";
import {TokenData} from "./model/TokenData";
import {UserData} from "./logic/UserData";
import { User } from "./model/User";

let sound: HTMLAudioElement;

const pomodoroLength = 10;
const backendAddress = "http://localhost:9001";

function parseJwt<T> (token: string) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace('-', '+').replace('_', '/');
    return <T>JSON.parse(window.atob(base64)).data;
};

window.onload = () => {
    const tokenData = parseJwt<TokenData>(document.cookie.split('=')[1]);
    const authHeader = { Authorization : 'Basic ' + btoa(tokenData.email + ":" + tokenData.token) };
    const client = new HttpClient(backendAddress, authHeader);
    const pStats = new PomodoroStats(client);
    const pomodoro = new Pomodoro(client, pomodoroLength, pStats);
    const userData = new UserData(client);
    userData.updateSettings();

    console.log(tokenData);
    pomodoro.loadState();
    sound = new Audio("assets/sounds/tool.mp3");
    pStats.updateLastPomodoros();
    bindButtonFunctions(userData, pomodoro);
}

function bindButtonFunctions(userData: UserData, pomodoro: Pomodoro) {
    document.getElementById("start-button").onclick =
        () => pomodoro.startTimer();
    document.getElementById("stop-button").onclick = 
        () => pomodoro.stopTimer();
    document.getElementById("settings-save").onclick = 
        () => userData.updateSettings();
}

function playSound() {
    sound.play();
}