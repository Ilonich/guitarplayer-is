import { Component, OnInit } from '@angular/core';
import { LoginState } from '../classes/login-state';
import { LoggingResolverService } from '../services/logging-resolver.service';
declare var jQuery: any;

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  viewProviders: [ LoggingResolverService ]
})
export class HeaderComponent implements OnInit {
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
      '200':        'HMAC-token потерялся'
    },
    'register': {
      'required':        'Поле не должно быть пустым',
      'minlength':       'Пароль не должен быть короче 5 символов',
      'maxlength':       'Пароль не должен быть длиннее 24 символов',
    }
  };


  constructor(private logger: LoggingResolverService) {
    this.logger.stateFeed.subscribe(loginState => {
      this.state = loginState;
    });
  }

  ngOnInit() {

  }

  auth(pair: any): void {
    this.logger.login(pair).subscribe(
      empty => {
        this.errors['login'] = '';
      },
      error => {
        const loginError = this.errorMessages['login'];
        this.errors['login'] = loginError[error.status.toString()];
        console.log(this.errors['login']);
    });
  }

  reg(ass: any): void {
    console.log(ass);
  }

  openModal(id: string): void {
    jQuery('#'.concat(id)).modal();
  }

  logout(): void {
    console.log('LOGOUT');
  }

}
