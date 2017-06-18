import { Component, AfterViewInit, ViewChildren, QueryList, ChangeDetectorRef } from '@angular/core';
import { LoginState } from '../classes/login-state';
import { LoginingResolverService } from '../services/logining-resolver.service';
import { RegisterFormComponent } from './register-form/register-form.component';
import { LoginFormComponent } from './login-form/login-form.component';
import { AuthenticationService } from '../services/authentication.service';
import { Observable } from 'rxjs/Rx';
declare var jQuery: any;

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements AfterViewInit {

  @ViewChildren(LoginFormComponent)
  public someLoginForms: QueryList<LoginFormComponent>;
  
  @ViewChildren(RegisterFormComponent)
  public someRegisterForms: QueryList<RegisterFormComponent>;
  
  private loginComp: LoginFormComponent;
  private registerComp: RegisterFormComponent;
  
  private formsDetector: Observable<any>;

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

  constructor(private logger: LoginingResolverService, private authService: AuthenticationService, private cdref: ChangeDetectorRef) {
    this.logger.stateFeed.subscribe(loginState => {
      console.log('SUBSCRIPTION INVOKE');
      this.closeModals();
      this.state = loginState;
    });
    this.logger.stateFeed.connect();
  }

  ngAfterViewInit() {
    this.initForms();
    this.formsDetector = Observable.merge(this.someLoginForms.changes, this.someRegisterForms.changes);
    this.formsDetector.subscribe(() => {
      this.initForms();
    });
  }

  auth(): void {
    this.authService.authenticate(this.loginComp.loginForm.value).subscribe(
      empty => {
        this.errors['login'] = empty;
        console.log('auth complete');
      },
      error => {
        console.log(error);
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
    console.log(this.registerComp.registerForm.value);
  }

  closeModals(): void {
    jQuery('.modal').modal('hide');
  }

  openModal(id: string): void {
    jQuery('#'.concat(id)).modal();
    this.loginComp.loginForm.reset();
    this.registerComp.registerForm.reset();
    this.errors.login = '';
    this.errors.register = '';
  }

  canLogin(): Boolean {
    return false;
  }

  canRegister(): Boolean {
    return false;
  }

  private initForms(): void {
    if (this.someLoginForms.length === 1 && this.someRegisterForms.length === 1) {
      this.loginComp = this.someLoginForms.first;
      this.registerComp = this.someRegisterForms.first;
      setTimeout(() => this.canLogin = () => this.loginComp.loginForm.valid, 0);
      setTimeout(() => this.canRegister = () => this.registerComp.registerForm.valid, 0);
      this.cdref.detectChanges(); //ExpressionChangedAfterItHasBeenCheckedError
    }
  }
}
