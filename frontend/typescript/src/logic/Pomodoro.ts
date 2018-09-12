import {States} from "../model/States";
import {HttpClient} from "../HttpClient";
import {drawTimer, setButtons} from "../UI";
import { PomodoroStats } from "./PomodoroStats";
import { UserData } from "./UserData";

export class Pomodoro {
    private readonly client: HttpClient;
    private timer: number;
    private pomodoroState: any;
    userData: UserData;
    private readonly pStats: PomodoroStats;

    constructor(client: HttpClient, userData: UserData, pStats: PomodoroStats) {
        this.client = client;
        this.userData = userData;
        this.pStats = pStats;
    }

    private createTimer(pomodoroLength: number) {
        if (this.timer != null) {
            clearInterval(this.timer);
        }

        let secondsElapsed = 1;
        const interval = window.setInterval(() => {
            drawTimer(pomodoroLength - secondsElapsed);
            if (secondsElapsed >= pomodoroLength) {
                clearInterval(interval);
                // playSound();
                this.stopAll();
            }
            secondsElapsed += 1;
        }, 1000);
        return interval;
    }

    private stateToIdle() {
        this.pomodoroState = States.Idle;
        setButtons(this.pomodoroState, this);
    }

    private stateToPomodoro() {
        this.pomodoroState = States.Running;
        setButtons(this.pomodoroState, this);
    }

    private stateToBreak() {
        this.pomodoroState = States.Break;
        setButtons(this.pomodoroState, this);
    }

    private resetTimer(seconds: number) {
        console.debug("Resetting timer");
        drawTimer(seconds);
        if (this.timer != null) {
            clearInterval(this.timer);
        }
    }

    startPomodoro() {
        this.stateToPomodoro();

    }

    endPomodoro() {
        this.stateToBreak();
        this.resetTimer(this.userData.getUser().breakSeconds);
    
    }
    
    stopAll() {
        const previousState = this.pomodoroState;
        this.stateToIdle();
        this.resetTimer(this.userData.getUser().pomodoroSeconds);
        if(previousState == States.Running) {
            console.log("Stop all save pomodoro");
        
        }
    }
}