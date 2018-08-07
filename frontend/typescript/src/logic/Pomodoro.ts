import {States} from "../model/States";
import {HttpClient} from "../HttpClient";
import {drawTimer, setButtons} from "../UI";
import { PomodoroStats } from "./PomodoroStats";

export class Pomodoro {
    private readonly client: HttpClient;
    private timer: number;
    private pomodoroState: any;
    private readonly pomodoroLength: number;
    private readonly pStats: PomodoroStats;

    constructor(client: HttpClient, pomodoroLength: number, pStats: PomodoroStats) {
        this.client = client;
        this.pomodoroLength = pomodoroLength;
        this.pStats = pStats;
    }

    private savePomodoroStart() {
        return this
            .client
            .httpPost("/pomodoroStart", response => console.info("Result of pomodoro start: " + response));
    }
    
    private savePomodoroFinish() {
        return this
            .client
            .httpPatch("/pomodoroFinish", response => console.info("Result of pomodoro finish: " + response));
    }

    private createTimer(pomodoroLength: number) {
        let secondsElapsed = 0;
        const interval = window.setInterval(() => {
            drawTimer(pomodoroLength - secondsElapsed);
            if (secondsElapsed >= pomodoroLength) {
                clearInterval(interval);
                // playSound();
                this.stopTimer();
            }
            secondsElapsed += 1;
        }, 1000);
        return interval;
    }

    private resetTimer(seconds: number) {
        console.debug("Resetting timer");
        drawTimer(seconds);
        if (this.timer != null) {
            clearInterval(this.timer);
        }
    }

    loadState() {
        return this.client.httpGet("/pomodoroState", (data: any) => {
            if(data.Idle) {
                this.pomodoroState = States.Idle;
                this.resetTimer(this.pomodoroLength);
            } else if (typeof(data.Running) == 'object') {
                this.pomodoroState = States.Running;
                this.resetTimer(this.pomodoroLength - data.Running.secondsElapsed);
                this.timer = this.createTimer(this.pomodoroLength - data.Running.secondsElapsed);
            }
            setButtons(this.pomodoroState);
        });
    }

    startTimer() {
        this.pomodoroState = States.Running;
        this.timer = this.createTimer(this.pomodoroLength);
        this.savePomodoroStart();
        setButtons(this.pomodoroState);
    }
    
    stopTimer() {
        this.pomodoroState = States.Idle;
        this.resetTimer(this.pomodoroLength);
        // playSound();
        this.savePomodoroFinish()
        .then(_ => this.pStats.updateLastPomodoros());
        setButtons(this.pomodoroState);
    }
}