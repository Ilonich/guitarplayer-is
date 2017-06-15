import { Injectable } from '@angular/core';
import { LoginState } from '../classes/login-state';
import { Authentication } from '../classes/authentication';
import { BehaviorSubject } from 'rxjs/Rx';

const NO_LOGIN: LoginState = new LoginState(false, null);

@Injectable()
export class LoginingResolverService {
  private storage: Storage;
  stateFeed: BehaviorSubject<LoginState>;

  constructor() {
    this.storage = window.localStorage;
    const auth = this.getAuthentication();
    this.stateFeed = new BehaviorSubject<LoginState>(auth !== null ? new LoginState(true, auth.username) : NO_LOGIN);
    /*setInterval( () => this.stateFeed.next(new LoginState(true, 'ABRAHAM')), 15000);*/
  }

  getAuthentication(): Authentication {
    const json = this.storage.getItem('igpsAcc');
    return json !== null ? Authentication.fromJsonString(json) : null;
  }

  resolve(auth?: Authentication): void {
    if (auth == null) {
      this.storage.removeItem('igpsAcc');
      this.refresh();
      console.log('REFRESH AFTER ERASE');
    } else {
      this.storage.setItem('igpsAcc', JSON.stringify(auth));
      console.log('REFRESH AFTER LOGIN');
      this.refresh();
    }
  }

  refresh(): void {
    console.log('REFRESHING');
    const auth: Authentication = this.getAuthentication();
    if (auth !== null) {
      console.log('LOGIN ' + auth.username);
      this.stateFeed.next(new LoginState(true, auth.username));
    } else {
      console.log('NO LOGIN');
      this.stateFeed.next(NO_LOGIN);
    }
  }
}
