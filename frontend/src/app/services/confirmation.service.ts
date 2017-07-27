import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { TokenHttpInterceptor } from '../interceptor/token-http-interceptor';
import { ErrorInfo } from '../classes/error-info';


@Injectable()
export class ConfirmationService {


  constructor(private http: TokenHttpInterceptor) {
    console.log('ConfirmationService CREATED');
  }

  confirmEmail(token: string|any): Observable<boolean> {
      return this.http.get('/api/confirm/email/'.concat(token)).map(
          response => response.status === 202,
      );
  }

  confirmReset(token: string|any): Observable<boolean> {
      return this.http.get('/api/confirm/email/'.concat(token)).map(
          response => response.status === 202,
      );
  }
}
