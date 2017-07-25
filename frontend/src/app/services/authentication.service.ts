import { Injectable } from '@angular/core';
import { LoginTo } from '../classes/login-to';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import { Authentication } from '../classes/authentication';
import { Observable } from 'rxjs/Observable';
import { TokenHttpInterceptor } from '../interceptor/token-http-interceptor';
import { ErrorInfo } from '../classes/error-info';

export const INTERNAL_SERVER_ERROR: string = 'Внутренняя ошибка сервера';

@Injectable()
export class AuthenticationService {

  constructor(private http: TokenHttpInterceptor) {
      console.log('AuthenticationService CREATED');
  }

  public authenticate(pair: any): Observable<void | ErrorInfo> {
    const loginTo: LoginTo = new LoginTo(pair.email, pair.password);
    return this.http.post('/api/authenticate', JSON.stringify(loginTo))
        .map(response => {
            this.http.switchState(AuthenticationService.processAuthenticationResponse(response, loginTo.login));
        })
        .catch(err => {
            return ErrorInfo.mapAnyToErrorInfo(err);
        });
  }

  public register(regTo: any): Observable<void | ErrorInfo> {
      return this.http.put('/api/register', JSON.stringify(regTo))
          .map(response => {
              this.http.switchState(AuthenticationService.processAuthenticationResponse(response, regTo.email));
          })
          .catch(err => ErrorInfo.mapAnyToErrorInfo(err));
  }

  public logout(): Observable<void | ErrorInfo> {
      return this.http.get('/api/logout')
          .map(response => {
              if (response.status !== 200) {
                  console.error("Response status was '"+ response.status + "', expected '200'");
                  throw new Error(INTERNAL_SERVER_ERROR);
              }
          })
          .catch(err => {
              this.http.switchState();
              return ErrorInfo.mapAnyToErrorInfo(err);
          });
  }

  public resetPassword(email: string): Observable <void | ErrorInfo> {
      return this.http.post('/api/reset', JSON.stringify(email))
          .map(response => {
              if (response.status !== 202) {
                  console.error("Response status was '"+ response.status + "', expected '202'");
                  throw new Error(INTERNAL_SERVER_ERROR);
              }
          }).catch(err => {
              return ErrorInfo.mapAnyToErrorInfo(err);
          })
  }

  private static processAuthenticationResponse(response: any | Response, userMail: string): Authentication {
      const authTo = response.json();
      const headers = response.headers;
      if (headers.has('X-TokenAccess')) {
          return new Authentication(authTo.username, userMail,
              headers.get('X-Secret'), headers.get('WWW-Authenticate'), headers.get('X-HMAC-CSRF'), authTo.roles);
      } else {
          console.error('Missing \'X-TokenAccess\' header from response');
          throw new Error(INTERNAL_SERVER_ERROR);
      }
  }
}
