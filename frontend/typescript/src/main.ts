import {HttpClient} from "./HttpClient";
import {Pomodoro} from "./logic/Pomodoro";
import {PomodoroWs} from "./logic/PomodoroWs";
import {PomodoroStats} from "./logic/PomodoroStats";
import {TokenData} from "./model/TokenData";
import {UserData} from "./logic/UserData";
import {UserRequest, StartPomodoro} from "./model/UserRequest"

let sound: HTMLAudioElement;


function parseJwt<T> (token: string) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace('-', '+').replace('_', '/');
    return <T>JSON.parse(window.atob(base64)).data;
};

window.onload = () => {

    const s = '{"StartPomodoro":{}}';

    const ur = JSON.parse(s);

    const str = JSON.stringify(new StartPomodoro());

    console.log(str);
    console.log(ur.hasOwnProperty("StartPomodoro"));



    
    const backendAddress = 
        (<HTMLInputElement>document.getElementById("backend-address")).value;
    const tokenData = parseJwt<TokenData>(document.cookie.split('=')[1]);
    const authHeader = { Authorization : 'Basic ' + btoa(tokenData.email + ":" + tokenData.token) };
    const client = new HttpClient(backendAddress, authHeader);
    const pStats = new PomodoroStats(client);
    const userData = new UserData(client);
    const pomodoro = new Pomodoro(client, userData, pStats);

    const ws = new PomodoroWs(tokenData);
    ws.doStuff();

    pStats
        .updateLastPomodoros()
        .then(() => userData.loadSettings())
        .then(() => {
            sound = new Audio("assets/sounds/tool.mp3");
            bindButtonFunctions(userData, pomodoro);
            console.log("Initial data loaded");
        });
}

function bindButtonFunctions(userData: UserData, pomodoro: Pomodoro) {
    document.getElementById("settings-save").onclick = 
        () => userData.saveSettings();
    document.getElementById("pomodoro-length").onchange = 
        () => userData.checkIfChanged();
    document.getElementById("break-length").onchange = 
        () => userData.checkIfChanged();
}

function playSound() {
    sound.play();
}