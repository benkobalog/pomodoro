export class User {
    readonly id: string;
    readonly email: string;
    readonly createdAt: string;
    readonly pomodoroSeconds: number;
    readonly breakSeconds: number;

    constructor(object: any) {
        this.id = object.id
        this.email = object.email
        this.createdAt = object.createdAt
        this.pomodoroSeconds = object.pomodoroSeconds
        this.breakSeconds = object.breakSeconds
    }
}