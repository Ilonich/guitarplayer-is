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
    return Observable.of(true);
  }

  confirmReset(token: string|any): Observable<boolean> {
    return Observable.of(true);
  }
}
