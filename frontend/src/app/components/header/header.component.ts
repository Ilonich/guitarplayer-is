import { Component, AfterViewInit, ViewChildren, QueryList, ChangeDetectorRef } from '@angular/core';
import { LoginState } from '../../classes/login-state';
import { LoginingResolverService } from '../../services/logining-resolver.service';
import { RegisterFormComponent } from './register-form/register-form.component';
import { LoginFormComponent } from './login-form/login-form.component';
import { AuthenticationService, INTERNAL_SERVER_ERROR } from '../../services/authentication.service';
import { Observable } from 'rxjs/Rx';
import { ErrorInfo } from '../../classes/error-info';
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
  isResetVisible: boolean = false;

  errors = {
    'login': '',
    'register': ''
  };

  constructor(private logger: LoginingResolverService, private authService: AuthenticationService, private cdref: ChangeDetectorRef) {
    this.logger.stateFeed.subscribe(loginState => {
      console.log('SUBSCRIPTION INVOKE');
      this.closeModals();
      this.state = loginState;
    });
  }

  ngAfterViewInit() {
    this.initForms();
    this.formsDetector = Observable.merge(this.someLoginForms.changes, this.someRegisterForms.changes);
    this.formsDetector.subscribe(() => {
      this.initForms();
    });
  }

  openSidebar(): void {
    jQuery('#leftsidebar').toggleClass('toggled');
    jQuery('#page-content-wrapper').toggleClass('moved');
  }

  auth(): void {
      this.authService.authenticate(this.loginComp.loginForm.value).subscribe(
          empty => {
              this.errors['login'] = '';
          },
          error => {
              this.handleError('login', error);
          }
      );
  }

  reg(): void {
      this.authService.register(this.registerComp.registerForm.value).subscribe(
          empty => {
              this.errors['register'] = '';
          },
          error => {
              this.handleError('register', error);
          }
      );

  }

  reset(): void {
      this.authService.resetPassword(this.loginComp.email.value).subscribe(
          empty => {
              this.isResetVisible = false;
              this.errors['login'] = 'Письмо с инструкцией отправлено на указанный Email';
          },
          error => {
              this.handleError('login', error);
          }
      )
  }

  logout(): void {
      this.authService.logout().subscribe();
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

  canLogin(): boolean {
    return false;
  }

  canRegister(): boolean {
    return false;
  }

  canReset(): boolean {
    return false;
  }

  private initForms(): void {
    if (this.someLoginForms.length === 1 && this.someRegisterForms.length === 1) {
      this.loginComp = this.someLoginForms.first;
      this.registerComp = this.someRegisterForms.first;
      setTimeout(() => this.canLogin = () => this.loginComp.loginForm.valid, 0);
      setTimeout(() => this.canRegister = () => this.registerComp.registerForm.valid, 0);
      setTimeout(() => this.canReset = () => this.loginComp.email.valid, 0);
      this.cdref.detectChanges(); // ExpressionChangedAfterItHasBeenCheckedError
    }
  }

  private handleError(formName: string, error: ErrorInfo): void {
      this.isResetVisible = false;
      if (error.cause === 'Unknown'){
          this.errors[formName] = 'Нет соединения с сервером';
          console.log(error);
      } else if (error.status === 500) {
          this.errors[formName] = INTERNAL_SERVER_ERROR;
          console.log(error);
      } else if (error.status === 401) {
          this.errors[formName] = error.details.join('. ');
          this.isResetVisible = true;
          console.log(error);
      } else {
          console.log(error);
          this.errors[formName] = error.details.join('. ');
      }
  }
}
