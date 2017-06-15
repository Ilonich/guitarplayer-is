import { Component, OnInit } from '@angular/core';
import {FormGroup, FormBuilder, Validators, AbstractControl} from '@angular/forms';

@Component({
  selector: 'login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent implements OnInit {

  email: AbstractControl;
  password: AbstractControl;
  loginForm: FormGroup;

  formErrors = {
    'email': '',
    'password': ''
  };

  validationMessages = {
    'email': {
      'required':        'Поле не должно быть пустым',
      'pattern':         'Не верный формат'
    },
    'password': {
      'required':        'Поле не должно быть пустым',
      'minlength':       'Пароль не должен быть короче 5 символов',
      'maxlength':       'Пароль не должен быть длиннее 24 символов',
    }
  };

  constructor(private fb: FormBuilder) { }
  ngOnInit() {
    this.buildForm();
    this.email = this.loginForm.get('email');
    this.password = this.loginForm.get('password');
  }

  buildForm(): void {
    this.loginForm = this.fb.group({
      'email': ['', [
        Validators.required,
        Validators.pattern(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i),
        ]
      ],
      'password': ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(24),
        ]
      ]
    });

    this.loginForm.valueChanges
      .subscribe(data => this.onValueChanged(data));

    this.onValueChanged();
  }

  onValueChanged(data?: any) {
    if (!this.loginForm) { return; }
    const form = this.loginForm;

    for (const field in this.formErrors) {
      if (field !== null) {
      this.formErrors[field] = '';
      const control = form.get(field);

        if (control && control.touched && control.dirty && !control.valid) {
          const messages = this.validationMessages[field];
          for (const key in control.errors) {
            if (key !== null) {
              this.formErrors[field] += messages[key] + ' ';
            }
          }
        }
      }
    }
  }
}
