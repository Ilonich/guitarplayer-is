import { Injectable } from '@angular/core';
import {LoginTo} from '../classes/login-to';
import 'rxjs/add/operator/map';
import {Authentication} from '../classes/authentication';
import { Observable } from 'rxjs/Observable';
import { TokenHttpInterceptor } from '../interceptor/token-http-interceptor';

@Injectable()
export class AuthenticationService {

  constructor(private http: TokenHttpInterceptor) { }

  public authenticate(pair: any): Observable<string> {
    const loginTo: LoginTo = new LoginTo(pair.email, pair.password);
    return this.http.post('/api/authenticate', JSON.stringify(loginTo))
      .map(response => {
          const authTo = response.json();
          console.log(authTo);
          const headers = response.headers;
          console.log(headers);
        if (headers.has('X-TokenAccess')) {

          this.http.switchState(new Authentication(authTo.username, loginTo.login,
            headers.get('X-Secret'), headers.get('WWW-Authenticate'), headers.get('X-HMAC-CSRF'), authTo.roles));
          return '';
        } else {
          return 'Error';
        }
      });
  }

  public logout(): Observable<string> {
    return this.http.get('/api/logout').map(response => {
      if (response.status === 200) {
        this.http.switchState();
          return '';
      }
      return 'error'
    });
  }
}
