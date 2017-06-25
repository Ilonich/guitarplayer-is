import {Component, OnInit} from '@angular/core';
import {FormGroup, FormBuilder, Validators, AbstractControl} from '@angular/forms';

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
      'pattern':         'Ник не должен содержать переносы и длинные пробелы'
    },
    'email': {
      'required':        'Поле не должно быть пустым',
      'pattern':         'Не верный формат'
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
        Validators.pattern(/^((?![\t]|[\v]|[\r]|[\n]|[\f]|  )[\s\S])*$/i)
      ]
      ],
      'email': ['', [
        Validators.required,
        Validators.pattern(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i),
      ]
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
    this.registerForm.valueChanges
      .subscribe(data => this.onValueChanged(data));

    this.onValueChanged();
  }

  onValueChanged(data?: any) {
    if (!this.registerForm) { return; }
    const form = this.registerForm;
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

        if (control && control.touched && control.dirty && form.errors && control.valid) {
          for (const key in form.errors) {
            if (key !== null) {
              const messages = this.validationMessages[field];
              if (messages[key] !== undefined) {
                this.formErrors[field] += messages[key] + ' ';
              }
            }
          }
        }
      }
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
