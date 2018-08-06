export class HttpClient {
    readonly backendAddress: string;
    readonly authHeader: {Authorization : string};

    constructor(backendAddress: string, authHeader: {Authorization: string}){
        this.backendAddress = backendAddress;
        this.authHeader = authHeader;
    }

    httpGet(path: string, fn: (a: any) => void) {
        const options: RequestInit = 
            { method: "GET", headers: this.authHeader };

        return fetch(this.backendAddress + path, options)
            .then(r => r.json())
            .then(fn)
            .catch(err => console.log(err));
    }
    
    httpPost(path: string, fn: (r: Response) => void) {
        const options: RequestInit = 
            { method: "POST", mode: "cors", headers: this.authHeader };

        return fetch(this.backendAddress + path, options)
            .then(fn)
            .catch(err => console.log(err));
    }
    
    httpPatch(path: string, fn: (r: Response) => void) {
        const options: RequestInit = 
            { method: "PATCH", mode: "cors", headers: this.authHeader };

        return fetch(this.backendAddress + path, options)
            .then(fn)
            .catch(err => console.log(err));
    }
}