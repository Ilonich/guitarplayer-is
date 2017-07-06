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
          },
          error => {
              this.errors['login'] = error.details.join('. ');
          }
      );
  }

  reg(): void {
      this.authService.register(this.registerComp.registerForm.value).subscribe(
          empty => {
              this.errors['register'] = empty;
          },
          error => {
              this.errors['register'] = error.details.join('. ');
          }
      );

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
