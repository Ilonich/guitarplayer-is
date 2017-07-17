import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { TokenHttpInterceptor } from '../interceptor/token-http-interceptor';
import { ErrorInfo } from '../classes/error-info';


@Injectable()
export class UsersService {


  constructor(private http: TokenHttpInterceptor) {
    console.log('UsersService CREATED');
  }


  getUserPage(id: number|any): Observable<string> {
    return Observable.of('user');
  }
}
