import {Injectable, OnInit} from '@angular/core';
import {LoginState} from '../classes/login-state';
import {Authentication} from '../classes/authentication';
import {AuthenticationService} from './authentication-service.service';
import { Observable } from 'rxjs/Observable';
import {Observer} from 'rxjs/Observer';


@Injectable()
export class LoggingResolverService {
  private storage: Storage;
  stateFeed: Observable<LoginState>;
  private stateObserver: Observer<LoginState>;

  constructor(private authservice: AuthenticationService) {
    this.storage = window.localStorage;
    this.stateFeed = new Observable<LoginState>(observer => {
      this.stateObserver = observer;
      this.refresh();
    });
    /*setInterval( () => this.stateObserver.next(new LoginState(true, 'ABRAHAM')), 15000);*/
  }

  login(pair: {email: String, password: String}): Observable<String> {
    return this.authservice.authenticate(pair.email.toLowerCase(), pair.password)
      .map(auth => {
        console.log('COOL');
        this.storage.setItem('igpsAcc', JSON.stringify(auth));
        this.stateObserver.next(new LoginState(true, auth.username));
        return '';
      });
  }

  refresh(): void {
    const auth: Authentication = JSON.parse(this.storage.getItem('igpsAcc'));
    if (auth !== null) {
      this.stateObserver.next(new LoginState(true, auth.username));
    } else {
      this.stateObserver.next(new LoginState(false, null));
    }
  }

}
