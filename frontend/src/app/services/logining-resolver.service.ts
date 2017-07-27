import { Injectable } from '@angular/core';
import { LoginState } from '../classes/login-state';
import { Authentication } from '../classes/authentication';
import {BehaviorSubject, Observable, AnonymousSubject, ConnectableObservable} from 'rxjs/Rx';

const NO_LOGIN: LoginState = new LoginState(false, null);
const SOME_LOGIN: LoginState = new LoginState(true, 'Тестовый юзер');

@Injectable()
export class LoginingResolverService {
    private storage: Storage;
    private currentToken: Authentication;
    private subject: BehaviorSubject<LoginState>;
    stateFeed: ConnectableObservable<LoginState>;

    constructor() {
        this.storage = window.localStorage;
        const auth = this.getAuthentication();
        this.currentToken = auth;
        this.subject = new BehaviorSubject<LoginState>(auth !== null ? new LoginState(true, auth.username) : NO_LOGIN); // SOME_LOGIN for different view
        this.stateFeed = this.subject.publish();
        console.log('LoginingResolverService CREATED');
    }

    getAuthentication(): Authentication {
        if (this.currentToken === undefined || this.currentToken === null) {
            const json = this.storage.getItem('igpsAcc');
            return json !== null ? Authentication.fromJsonString(json) : null;
        } else {
            return this.currentToken;
        }
    }

    resolve(auth?: Authentication): void {
        if (auth == null) {
            this.currentToken = null;
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
            this.subject.next(new LoginState(true, auth.username));
        } else {
            console.log('NO LOGIN');
            if (this.subject.getValue() !== NO_LOGIN){
                this.subject.next(NO_LOGIN);
            }
        }
    }
}
