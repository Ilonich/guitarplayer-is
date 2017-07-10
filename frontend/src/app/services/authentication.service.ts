import { Injectable } from '@angular/core';
import { LoginTo } from '../classes/login-to';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { Authentication } from '../classes/authentication';
import { Observable } from 'rxjs/Observable';
import { TokenHttpInterceptor } from '../interceptor/token-http-interceptor';
import { ErrorInfo } from '../classes/error-info';

@Injectable()
export class AuthenticationService {

  constructor(private http: TokenHttpInterceptor) {
      console.log('AuthenticationService CREATED');
  }

  public authenticate(pair: any): Observable<any> {
    const loginTo: LoginTo = new LoginTo(pair.email, pair.password);
    return this.http.post('/api/authenticate', JSON.stringify(loginTo))
        .map(response => {
              const authTo = response.json();
              const headers = response.headers;
            if (headers.has('X-TokenAccess')) {
                this.http.switchState(new Authentication(authTo.username, loginTo.login,
                    headers.get('X-Secret'), headers.get('WWW-Authenticate'), headers.get('X-HMAC-CSRF'), authTo.roles));
                return '';
            } else {
                console.error("Missing 'X-TokenAccess' header from response");
                return 'Сервер в неадеквате';
            }
        })
        .catch(err => {
            return ErrorInfo.mapAnyToErrorInfo(err);
        });
  }

  public register(regTo: any): Observable<any> {
      return this.http.post('/api/register', JSON.stringify(regTo))
          .map(response => {
              const authTo = response.json();
              const headers = response.headers;
              if (headers.has('X-TokenAccess')) {
                  this.http.switchState(new Authentication(authTo.username, regTo.email,
                      headers.get('X-Secret'), headers.get('WWW-Authenticate'), headers.get('X-HMAC-CSRF'), authTo.roles));
                  return '';
              } else {
                  console.error("Missing 'X-TokenAccess' header from response");
                  return 'Сервер в неадеквате';
              }
          })
          .catch(err => {
              return ErrorInfo.mapAnyToErrorInfo(err);
          });
  }

  public logout(): Observable<any> {
      return this.http.get('/api/logout')
          .map(response => {
              this.http.switchState();
              if (response.status === 200) {
                  return 'Logout success';
              } else {
                  console.error("Response status was '"+ response.status + "', expected '200'");
                  return 'Probably, logout was not successful on server side';
              }
          })
          .catch(err => {
              return ErrorInfo.mapAnyToErrorInfo(err);
          });
  }
}
