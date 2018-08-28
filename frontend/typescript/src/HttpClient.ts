export class HttpClient {
    readonly backendAddress: string;
    readonly authHeader: {Authorization : string};

    constructor(backendAddress: string, authHeader: {Authorization: string}){
        this.backendAddress = backendAddress;
        this.authHeader = authHeader;
    }

    httpGet<T>(path: string, fn: (a: any) => T) {
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

    httpPut(path: string, fn: (r: Response) => void, body: any) {
        const options: RequestInit = { 
            method: "PUT", mode: "cors",
            headers: { ...this.authHeader, "Content-Type": "application/json" },
            body: JSON.stringify(body)
        };

        return fetch(this.backendAddress + path, options)
            .then(fn)
            .catch(err => console.log(err));
    }
}