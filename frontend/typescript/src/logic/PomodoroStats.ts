import {HttpClient} from "../HttpClient";

export class PomodoroStats {
    readonly client: HttpClient;

    constructor(client: HttpClient) {
        this.client = client;
    }

    updateLastPomodoros() {
        return this.client.httpGet("/pomodoroStats", data => {
            const tableRow = (pomodoroData: any) => {
                return `<tr>` +
                    `<td>${pomodoroData.duration}</td>` +
                    `<td>${pomodoroData.started}</td>` +
                    `<td>${pomodoroData.finished}</td>` +
                    `</tr>`;
            }

            const tableContent = data.map(tableRow).join("");
            $("#lastPomodorosTable").html(tableContent);
            console.log("Updated pomodoros");
        });
    }
}