import {Injectable, OnInit} from '@angular/core';
import {LoginState} from '../classes/login-state';
import {Authentication} from '../classes/authentication';
import { Observable } from 'rxjs/Observable';
import {Observer} from 'rxjs/Observer';

const NO_LOGIN: LoginState = new LoginState(false, null);

@Injectable()
export class LoginingResolverService {
  private storage: Storage;
  stateFeed: Observable<LoginState>;
  private stateObserver: Observer<LoginState>;

  constructor() {
    this.storage = window.localStorage;
    this.stateFeed = new Observable<LoginState>(observer => {
      this.stateObserver = observer;
      console.log(this.stateObserver);
      this.refresh();
    });
    /*setInterval( () => this.stateObserver.next(new LoginState(true, 'ABRAHAM')), 15000);*/
  }

  getAuthentication(): Authentication {
    return JSON.parse(this.storage.getItem('igpsAcc'));
  }

  login(auth: Authentication): void {
    console.log(auth);
    this.storage.setItem('igpsAcc', JSON.stringify(auth));
    console.log(this.stateObserver);
    this.refresh();
  }

  erase(): void {
    this.storage.removeItem('igpsAcc');
    this.stateObserver.next(NO_LOGIN);
  }

  refresh(): void {
    const auth: Authentication = this.getAuthentication();
    if (auth !== null) {
      console.log(auth.username);
      console.log('LOGIN');
      console.log(this.stateObserver);
      this.stateObserver.next(new LoginState(true, auth.username));
    } else {
      console.log('NO LOGIN');
      console.log(this.stateObserver);
      this.stateObserver.next(NO_LOGIN);
    }
  }
}
