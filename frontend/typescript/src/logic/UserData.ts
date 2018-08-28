import { User } from "../model/User";
import {drawSettings} from "../UI";
import {HttpClient} from "../HttpClient"

export class UserData {
    readonly client: HttpClient

    constructor(client: HttpClient) {
        this.client = client;
    }

    updateSettings() {
        this.client.httpGet("/user", data => {
            console.log("Update Settings: " + data);
            try {
                drawSettings(<User>data);
            } catch(e) {
                console.log("Couldn't parse user settings: " + e);
            }
        });
    }
}