import { Observable } from "rxjs/Rx";
import { Response } from '@angular/http';
import 'rxjs/add/observable/throw';

export class ErrorInfo {
    status: number;
    url: string;
    cause: string;
    details: string[];
    constructor(status: number, url: string, cause: string, details: string[]){
        this.status = status;
        this.url = url;
        this.cause = cause;
        this.details = details;
    }

    public static mapAnyToErrorInfo(err: any): Observable<ErrorInfo> {
        let status: number;
        let url: string;
        let cause: string;
        let details: string[] = [];
        if (err instanceof Response) {
            const body = err.json() || '';
            if (body.cause === null || body.cause === undefined) {
                cause = 'Unknown';
                details.push(err.toString());
                details.push(err.text());
                url = err.url;
                console.error('Response body has wrong format, expected format - json [ErrorInfo(url, cause, details)]');
            } else {
                cause = body.cause;
                details = body.details;
                url = body.url;
            }
            status = err.status;
        } else {
            cause = err.constructor.name;
            details.push(err.message ? err.message : err.toString());
            url = null;
            status = null;
        }
        return Observable.throw(new ErrorInfo(status, url, cause, details));
    }
}