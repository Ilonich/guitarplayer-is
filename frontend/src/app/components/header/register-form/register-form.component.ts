import {Component, OnInit} from '@angular/core';
import {FormGroup, FormBuilder, Validators, AbstractControl} from '@angular/forms';
import {CustomAsyncValidators} from '../../../validators/unique-constraint.directive';

@Component({
  selector: 'register-form',
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.css']
})
export class RegisterFormComponent implements OnInit {

  username: AbstractControl;
  email: AbstractControl;
  password: AbstractControl;
  passwordcopy: AbstractControl;
  registerForm: FormGroup;

  formErrors = {
    'username': '',
    'email': '',
    'password': '',
    'passwordcopy': ''
  };

  validationMessages = {
    'username': {
      'required':        'Поле не должно быть пустым',
      'minlength':       'Ник не должен быть короче 2 символов',
      'maxlength':       'Ник не должен быть длиннее 24 символов',
      'pattern':         'Ник не должен содержать переносы и длинные пробелы',
      'notUnique':       'Пользователь с таким ником уже зарегистрирован'
    },
    'email': {
      'required':        'Поле не должно быть пустым',
      'pattern':         'Не верный формат',
      'notUnique':       'Пользователь с таким email уже зарегистрирован'
    },
    'password': {
      'required':        'Поле не должно быть пустым',
      'minlength':       'Пароль не должен быть короче 5 символов',
      'maxlength':       'Пароль не должен быть длиннее 24 символов',
      'pattern':         'Пароль не должен содержать переносы пробелы'
    },
    'passwordcopy': {
      'required':        'Поле не должно быть пустым',
      'notEquals':       'Пароли не совпадают'
    }
  };

  constructor(private fb: FormBuilder) { }

  ngOnInit() {
    this.buildForm();
    this.username = this.registerForm.get('username');
    this.email = this.registerForm.get('email');
    this.password = this.registerForm.get('password');
    this.passwordcopy = this.registerForm.get('passwordcopy');
  }

  buildForm(): void {
    this.registerForm = this.fb.group({
      'username': ['', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(24),
        Validators.pattern(/^((?![\t]|[\v]|[\r]|[\n]|[\f]|  )[\s\S])*$/i),
      ], [ CustomAsyncValidators.usernameValidator ]
      ],
      'email': ['', [
        Validators.required,
        Validators.pattern(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i),
      ], [ CustomAsyncValidators.emailValidator ]
      ],
      'password': ['', [
        Validators.required,
        Validators.minLength(5),
        Validators.maxLength(24),
        Validators.pattern(/^((?![\t]|[\v]|[\r]|[\n]|[\f]| )[\s\S])*$/i)
      ]
      ],
      'passwordcopy': ['', [
        Validators.required,
      ]
      ]
    }, {validator: this.matchingPasswords('password', 'passwordcopy')});
    /* https://github.com/sabrio/ng2-validation-manager переделать*/
    this.registerForm.valueChanges.debounceTime(650) //сообщение о невалидности от асинхронного валидатора не успевает возникнуть, потому задержка нужна
      .subscribe(data => this.onValueChanged(data));
    this.onValueChanged();
  }

  onValueChanged(data?: any) {
    if (!this.registerForm) { return; }
    const form = this.registerForm;
    for (const field in this.formErrors) {
      const control = form.get(field);
      this.formErrors[field] = '';
      if (control.dirty && !control.valid){
        const controlErrors = control.errors;
        for (let key in controlErrors){
          this.formErrors[field] = this.validationMessages[field][key];
        }
      }
    }
    if (form.hasError('notEquals')) {
      this.formErrors['passwordcopy'] = this.validationMessages['passwordcopy']['notEquals'];
    }
  }

  private matchingPasswords(passwordKey: string, confirmPasswordKey: string) {
    return (control: AbstractControl): { [key: string]: Boolean } => {
      const password = control.get(passwordKey);
      const confirmPassword = control.get(confirmPasswordKey);

      if (password.value !== confirmPassword.value) {
        return {
          'notEquals': true
        };
      }
      return null;
    };
  }
}
