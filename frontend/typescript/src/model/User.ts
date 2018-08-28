export class User {
    readonly id: string;
    readonly email: string;
    readonly createdAt: string;
    pomodoroSeconds: number;
    breakSeconds: number;

    constructor(object: any) {
        this.id = object.id
        this.email = object.email
        this.createdAt = object.createdAt
        this.pomodoroSeconds = object.pomodoroSeconds
        this.breakSeconds = object.breakSeconds
    }
}