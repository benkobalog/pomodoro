import { User } from "../model/User";
import {drawSettings, drawTimer} from "../UI";
import {HttpClient} from "../HttpClient"

export class UserData {
    readonly client: HttpClient
    private user: User;

    constructor(client: HttpClient) {
        this.client = client;
    }

    loadSettings() {
        return this.client.httpGet("/user", data => {
            console.log("Update Settings: " + data);
            try {
                this.user = <User>data;
                drawSettings(<User>data);
            } catch(e) {
                console.log("Couldn't parse user settings: " + e);
            }
            return <User>data;
        }).then(user => {
            this.checkIfChanged();
            return user;
        });
    }

    getUser() {
        return this.user;
    }

    private getSettingsFromUI() {
        const pomodorLengthStr = $("#pomodoro-length").val();
        const breakLengthStr =$("#break-length").val();
        return {
            "pomodoroSeconds" : Number(pomodorLengthStr) * 60,
            "breakSeconds" : Number(breakLengthStr) * 60
        };
    }

    saveSettings() {
        const {pomodoroSeconds, breakSeconds} = this.getSettingsFromUI();
        this.user.pomodoroSeconds = pomodoroSeconds;
        this.user.breakSeconds = breakSeconds;

        console.log(this.user);
        this.client.httpPut("/user",() => 1 , this.user )
            .then(() => this.checkIfChanged());
    }

    checkIfChanged() {
        console.log("Checking settings equality")
        const {pomodoroSeconds, breakSeconds} = this.getSettingsFromUI();
        const saveButton = $("#settings-save");
        if(pomodoroSeconds == this.user.pomodoroSeconds && breakSeconds == this.user.breakSeconds) {
            saveButton.prop("disabled", true);
            return false;
        } else {
            saveButton.prop("disabled", false);
            return true;
        }
    }
}