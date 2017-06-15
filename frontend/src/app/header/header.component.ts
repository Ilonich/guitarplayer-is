import {Component, OnInit, ViewChild, AfterViewInit} from '@angular/core';
import { LoginState } from '../classes/login-state';
import { LoginingResolverService } from '../services/logining-resolver.service';
import { RegisterFormComponent } from './register-form/register-form.component';
import { LoginFormComponent } from './login-form/login-form.component';
import {AuthenticationService} from '../services/authentication.service';
declare var jQuery: any;

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  viewProviders: [ LoginingResolverService ]
})
export class HeaderComponent implements OnInit, AfterViewInit {

  @ViewChild(LoginFormComponent) private login: LoginFormComponent;
  @ViewChild(RegisterFormComponent) private register: RegisterFormComponent;

  state: LoginState;

  errors = {
    'login': '',
    'register': ''
  };

  errorMessages = {
    'login': {
      '404':        'Не в ту степь',
      '403':        'Не верный логин или пароль',
      '500':        'Сервер подавился',
    }
  };

  constructor(private logger: LoginingResolverService, private authService: AuthenticationService) {
    this.logger.stateFeed.subscribe(loginState => {
      console.log('STATE CHANGED');
      this.closeModals();
      this.state = loginState;
    });
  }

  ngOnInit() {}

  ngAfterViewInit() {
    setTimeout(() => this.canLogin = () => this.login.loginForm.valid, 0);
    setTimeout(() => this.canRegister = () => this.register.registerForm.valid, 0);
  }

  auth(): void {
    this.authService.authenticate(this.login.loginForm.value).subscribe(
      empty => {
        this.errors['login'] = empty;
        console.log('auth complete');
      },
      error => {
        console.log(error);
        console.log(error.status);
        const loginError = this.errorMessages['login'];
        this.errors['login'] = loginError[error.status];
        console.log(this.errors['login']);
    });
  }

  logout(): void {
    this.authService.logout().subscribe(
        message => {
          console.log(message);
        },
        error => {
          console.log(error);
        }
    );
  }

  reg(): void {
    console.log(this.register.registerForm.value);
  }

  closeModals(): void {
    jQuery('.modal').modal('hide');
  }

  openModal(id: string): void {
    jQuery('#'.concat(id)).modal();
    this.login.loginForm.reset();
    this.register.registerForm.reset();
    this.errors.login = '';
    this.errors.register = '';
  }

  canLogin(): Boolean {
    return false;
  }

  canRegister(): Boolean {
    return false;
  }


}
