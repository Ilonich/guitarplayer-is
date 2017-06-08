import { Injectable } from '@angular/core';
import {Http, RequestOptions, Headers} from '@angular/http';
import {LoginTo} from '../classes/login-to';
import 'rxjs/add/operator/map';
import {Authentication} from '../classes/authentication';
import { Observable } from 'rxjs/Observable';

const requestHeaders = new Headers({ 'Content-Type': 'application/json' });
const options = new RequestOptions( { headers: requestHeaders } );

@Injectable()
export class AuthenticationService {

  constructor(private http: Http) { }

  public authenticate(email: String, password: String): Observable<Authentication> {
    const loginTo: LoginTo = new LoginTo(email, password);
    return this.http.post('/api/authenticate', JSON.stringify(loginTo), options)
      .map(response => {
        const authTo = response.json();
        const headers = response.headers;
        if (headers.has('X-TokenAccess')) {
          return new Authentication(authTo.username, email,
            headers.get('X-Secret'), headers.get('X-HMAC-CSRF'), authTo.roles);
        }
      });
  }
}
