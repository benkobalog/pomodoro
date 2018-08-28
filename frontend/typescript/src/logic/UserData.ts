import { User } from "../model/User";
import {drawSettings} from "../UI";
import {HttpClient} from "../HttpClient"

export class UserData {
    readonly client: HttpClient
    private user: User;

    constructor(client: HttpClient) {
        this.client = client;
    }

    loadSettings() {
        this.client.httpGet("/user", data => {
            console.log("Update Settings: " + data);
            try {
                this.user = <User>data;
                drawSettings(<User>data);
            } catch(e) {
                console.log("Couldn't parse user settings: " + e);
            }
            return <User>data;
        });
    }

    saveSettings() {
        const pomodorLengthStr = $("#pomodoro-length").val();
        const breakLengthStr =$("#break-length").val();
        this.user.pomodoroSeconds = Number(pomodorLengthStr) * 60;
        this.user.breakSeconds = Number(breakLengthStr) * 60;

        console.log(this.user);
        this.client.httpPut("/user",() => 1 , this.user );
    }
}