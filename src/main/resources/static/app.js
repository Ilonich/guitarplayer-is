webpackJsonp([1],{

/***/ 125:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = __webpack_require__(19);
var http_1 = __webpack_require__(90);
var login_to_1 = __webpack_require__(233);
__webpack_require__(167);
var authentication_1 = __webpack_require__(231);
var requestHeaders = new http_1.Headers({ 'Content-Type': 'application/json' });
var options = new http_1.RequestOptions({ headers: requestHeaders });
var AuthenticationService = (function () {
    function AuthenticationService(http) {
        this.http = http;
    }
    AuthenticationService.prototype.authenticate = function (email, password) {
        var loginTo = new login_to_1.LoginTo(email, password);
        return this.http.post('/api/authenticate', JSON.stringify(loginTo), options)
            .map(function (response) {
            var authTo = response.json();
            var headers = response.headers;
            if (headers.has('X-TokenAccess')) {
                return new authentication_1.Authentication(authTo.username, email, headers.get('X-Secret'), headers.get('X-HMAC-CSRF'), authTo.roles);
            }
        });
    };
    return AuthenticationService;
}());
AuthenticationService = __decorate([
    core_1.Injectable(),
    __metadata("design:paramtypes", [http_1.Http])
], AuthenticationService);
exports.AuthenticationService = AuthenticationService;


/***/ }),

/***/ 206:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var platform_browser_1 = __webpack_require__(47);
var core_1 = __webpack_require__(19);
var forms_1 = __webpack_require__(70);
var http_1 = __webpack_require__(90);
var app_component_1 = __webpack_require__(230);
var header_1 = __webpack_require__(235);
var authentication_service_service_1 = __webpack_require__(125);
var AppModule = (function () {
    function AppModule() {
    }
    return AppModule;
}());
AppModule = __decorate([
    core_1.NgModule({
        declarations: [
            app_component_1.AppComponent,
            header_1.HEADER_COMPONENTS
        ],
        imports: [
            platform_browser_1.BrowserModule,
            forms_1.ReactiveFormsModule,
            http_1.HttpModule
        ],
        bootstrap: [app_component_1.AppComponent],
        providers: [authentication_service_service_1.AuthenticationService]
    })
], AppModule);
exports.AppModule = AppModule;


/***/ }),

/***/ 230:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = __webpack_require__(19);
var AppComponent = (function () {
    function AppComponent() {
    }
    return AppComponent;
}());
AppComponent = __decorate([
    core_1.Component({
        selector: 'app-root',
        template: __webpack_require__(400),
        styles: [__webpack_require__(404)],
    })
], AppComponent);
exports.AppComponent = AppComponent;


/***/ }),

/***/ 231:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var Authentication = (function () {
    function Authentication(username, email, publicKey, csrf) {
        var roles = [];
        for (var _i = 4; _i < arguments.length; _i++) {
            roles[_i - 4] = arguments[_i];
        }
        this._username = username;
        this._email = email;
        this._publicKey = publicKey;
        this._csrf = csrf;
        this._roles = roles;
    }
    Object.defineProperty(Authentication.prototype, "username", {
        get: function () {
            return this._username;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Authentication.prototype, "email", {
        get: function () {
            return this._email;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Authentication.prototype, "publicKey", {
        get: function () {
            return this._publicKey;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(Authentication.prototype, "csrf", {
        get: function () {
            return this._csrf;
        },
        enumerable: true,
        configurable: true
    });
    Authentication.prototype.hasRole = function (role) {
        return undefined !== this._roles.find(function (s) { return s === role; });
    };
    return Authentication;
}());
exports.Authentication = Authentication;


/***/ }),

/***/ 232:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var LoginState = (function () {
    function LoginState(logged, username) {
        this.logged = logged;
        this.username = username;
    }
    return LoginState;
}());
exports.LoginState = LoginState;


/***/ }),

/***/ 233:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var LoginTo = (function () {
    function LoginTo(mail, pass) {
        this.login = mail.toLowerCase();
        this.password = pass;
    }
    return LoginTo;
}());
exports.LoginTo = LoginTo;


/***/ }),

/***/ 234:
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(jQuery) {
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = __webpack_require__(19);
var logging_resolver_service_1 = __webpack_require__(238);
var HeaderComponent = (function () {
    function HeaderComponent(logger) {
        var _this = this;
        this.logger = logger;
        this.errors = {
            'login': '',
            'register': ''
        };
        this.errorMessages = {
            'login': {
                '404': 'Не в ту степь',
                '403': 'Не верный логин или пароль',
                '500': 'Сервер подавился',
                '200': 'HMAC-token потерялся'
            },
            'register': {
                'required': 'Поле не должно быть пустым',
                'minlength': 'Пароль не должен быть короче 5 символов',
                'maxlength': 'Пароль не должен быть длиннее 24 символов',
            }
        };
        this.logger.stateFeed.subscribe(function (loginState) {
            _this.state = loginState;
        });
    }
    HeaderComponent.prototype.ngOnInit = function () {
    };
    HeaderComponent.prototype.auth = function (pair) {
        var _this = this;
        this.logger.login(pair).subscribe(function (empty) {
            _this.errors['login'] = '';
        }, function (error) {
            var loginError = _this.errorMessages['login'];
            _this.errors['login'] = loginError[error.status.toString()];
            console.log(_this.errors['login']);
        });
    };
    HeaderComponent.prototype.reg = function (ass) {
        console.log(ass);
    };
    HeaderComponent.prototype.openModal = function (id) {
        jQuery('#'.concat(id)).modal();
    };
    HeaderComponent.prototype.logout = function () {
        console.log('LOGOUT');
    };
    return HeaderComponent;
}());
HeaderComponent = __decorate([
    core_1.Component({
        selector: 'app-header',
        template: __webpack_require__(401),
        styles: [__webpack_require__(405)],
        viewProviders: [logging_resolver_service_1.LoggingResolverService]
    }),
    __metadata("design:paramtypes", [logging_resolver_service_1.LoggingResolverService])
], HeaderComponent);
exports.HeaderComponent = HeaderComponent;

/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(91)))

/***/ }),

/***/ 235:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var header_component_1 = __webpack_require__(234);
var login_form_component_1 = __webpack_require__(236);
var register_form_component_1 = __webpack_require__(237);
var HEADER_COMPONENTS = [
    header_component_1.HeaderComponent,
    login_form_component_1.LoginFormComponent,
    register_form_component_1.RegisterFormComponent
];
exports.HEADER_COMPONENTS = HEADER_COMPONENTS;


/***/ }),

/***/ 236:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = __webpack_require__(19);
var forms_1 = __webpack_require__(70);
var LoginFormComponent = (function () {
    function LoginFormComponent(fb) {
        this.fb = fb;
        this.formErrors = {
            'email': '',
            'password': ''
        };
        this.validationMessages = {
            'email': {
                'required': 'Поле не должно быть пустым',
                'pattern': 'Не верный формат'
            },
            'password': {
                'required': 'Поле не должно быть пустым',
                'minlength': 'Пароль не должен быть короче 5 символов',
                'maxlength': 'Пароль не должен быть длиннее 24 символов',
            }
        };
    }
    LoginFormComponent.prototype.ngOnInit = function () {
        this.buildForm();
        this.email = this.loginForm.get('email');
        this.password = this.loginForm.get('password');
    };
    LoginFormComponent.prototype.buildForm = function () {
        var _this = this;
        this.loginForm = this.fb.group({
            'email': ['', [
                    forms_1.Validators.required,
                    forms_1.Validators.pattern(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i),
                ]
            ],
            'password': ['', [
                    forms_1.Validators.required,
                    forms_1.Validators.minLength(5),
                    forms_1.Validators.maxLength(24),
                ]
            ]
        });
        this.loginForm.valueChanges
            .subscribe(function (data) { return _this.onValueChanged(data); });
        this.onValueChanged();
    };
    LoginFormComponent.prototype.onValueChanged = function (data) {
        if (!this.loginForm) {
            return;
        }
        var form = this.loginForm;
        for (var field in this.formErrors) {
            if (field !== null) {
                this.formErrors[field] = '';
                var control = form.get(field);
                if (control && control.dirty && !control.valid) {
                    var messages = this.validationMessages[field];
                    for (var key in control.errors) {
                        if (key !== null) {
                            this.formErrors[field] += messages[key] + ' ';
                        }
                    }
                }
            }
        }
    };
    return LoginFormComponent;
}());
LoginFormComponent = __decorate([
    core_1.Component({
        selector: 'login-form',
        template: __webpack_require__(402),
        styles: [__webpack_require__(406)]
    }),
    __metadata("design:paramtypes", [forms_1.FormBuilder])
], LoginFormComponent);
exports.LoginFormComponent = LoginFormComponent;


/***/ }),

/***/ 237:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = __webpack_require__(19);
var forms_1 = __webpack_require__(70);
var RegisterFormComponent = (function () {
    function RegisterFormComponent(fb) {
        this.fb = fb;
        this.formErrors = {
            'username': '',
            'email': '',
            'password': '',
            'passwordcopy': ''
        };
        this.validationMessages = {
            'username': {
                'required': 'Поле не должно быть пустым',
                'minlength': 'Псевдоним не должен быть короче 2 символов',
                'maxlength': 'Псевдоним не должен быть длиннее 24 символов',
                'pattern': 'Повежливей плизки)))'
            },
            'email': {
                'required': 'Поле не должно быть пустым',
                'pattern': 'Не верный формат'
            },
            'password': {
                'required': 'Поле не должно быть пустым',
                'minlength': 'Пароль не должен быть короче 5 символов',
                'maxlength': 'Пароль не должен быть длиннее 24 символов',
            },
            'passwordcopy': {
                'required': 'Поле не должно быть пустым',
                'notEquals': 'Пароли не совпадают'
            }
        };
    }
    RegisterFormComponent.prototype.ngOnInit = function () {
        this.buildForm();
        this.username = this.registerForm.get('username');
        this.email = this.registerForm.get('email');
        this.password = this.registerForm.get('password');
        this.passwordcopy = this.registerForm.get('passwordcopy');
    };
    RegisterFormComponent.prototype.buildForm = function () {
        var _this = this;
        this.registerForm = this.fb.group({
            'username': ['', [
                    forms_1.Validators.required,
                    forms_1.Validators.minLength(2),
                    forms_1.Validators.maxLength(24),
                ]
            ],
            'email': ['', [
                    forms_1.Validators.required,
                    forms_1.Validators.pattern(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/i),
                ]
            ],
            'password': ['', [
                    forms_1.Validators.required,
                    forms_1.Validators.minLength(5),
                    forms_1.Validators.maxLength(24),
                ]
            ],
            'passwordcopy': ['', [
                    forms_1.Validators.required,
                ]
            ]
        }, { validator: this.matchingPasswords('password', 'passwordcopy') });
        /* https://github.com/sabrio/ng2-validation-manager переделать*/
        this.registerForm.valueChanges
            .subscribe(function (data) { return _this.onValueChanged(data); });
        this.onValueChanged();
    };
    RegisterFormComponent.prototype.onValueChanged = function (data) {
        if (!this.registerForm) {
            return;
        }
        var form = this.registerForm;
        for (var field in this.formErrors) {
            if (field !== null) {
                this.formErrors[field] = '';
                var control = form.get(field);
                if (control && control.dirty && !control.valid) {
                    var messages = this.validationMessages[field];
                    for (var key in control.errors) {
                        if (key !== null) {
                            this.formErrors[field] += messages[key] + ' ';
                        }
                    }
                }
                if (control && control.dirty && form.errors && control.valid) {
                    for (var key in form.errors) {
                        if (key !== null) {
                            var messages = this.validationMessages[field];
                            if (messages[key] !== undefined) {
                                this.formErrors[field] += messages[key] + ' ';
                            }
                        }
                    }
                }
            }
        }
    };
    RegisterFormComponent.prototype.matchingPasswords = function (passwordKey, confirmPasswordKey) {
        return function (control) {
            var password = control.get(passwordKey);
            var confirmPassword = control.get(confirmPasswordKey);
            if (password.value !== confirmPassword.value) {
                return {
                    'notEquals': true
                };
            }
            return null;
        };
    };
    return RegisterFormComponent;
}());
RegisterFormComponent = __decorate([
    core_1.Component({
        selector: 'register-form',
        template: __webpack_require__(403),
        styles: [__webpack_require__(407)]
    }),
    __metadata("design:paramtypes", [forms_1.FormBuilder])
], RegisterFormComponent);
exports.RegisterFormComponent = RegisterFormComponent;


/***/ }),

/***/ 238:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = __webpack_require__(19);
var login_state_1 = __webpack_require__(232);
var authentication_service_service_1 = __webpack_require__(125);
var Observable_1 = __webpack_require__(0);
var LoggingResolverService = (function () {
    function LoggingResolverService(authservice) {
        var _this = this;
        this.authservice = authservice;
        this.storage = window.localStorage;
        this.stateFeed = new Observable_1.Observable(function (observer) {
            _this.stateObserver = observer;
            _this.refresh();
        });
        /*setInterval( () => this.stateObserver.next(new LoginState(true, 'ABRAHAM')), 15000);*/
    }
    LoggingResolverService.prototype.login = function (pair) {
        var _this = this;
        return this.authservice.authenticate(pair.email.toLowerCase(), pair.password)
            .map(function (auth) {
            console.log('COOL');
            _this.storage.setItem('igpsAcc', JSON.stringify(auth));
            _this.stateObserver.next(new login_state_1.LoginState(true, auth.username));
            return '';
        });
    };
    LoggingResolverService.prototype.refresh = function () {
        var auth = JSON.parse(this.storage.getItem('igpsAcc'));
        if (auth !== null) {
            this.stateObserver.next(new login_state_1.LoginState(true, auth.username));
        }
        else {
            this.stateObserver.next(new login_state_1.LoginState(false, null));
        }
    };
    return LoggingResolverService;
}());
LoggingResolverService = __decorate([
    core_1.Injectable(),
    __metadata("design:paramtypes", [authentication_service_service_1.AuthenticationService])
], LoggingResolverService);
exports.LoggingResolverService = LoggingResolverService;


/***/ }),

/***/ 239:
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var platform_browser_dynamic_1 = __webpack_require__(92);
var app_module_1 = __webpack_require__(206);
var platform = platform_browser_dynamic_1.platformBrowserDynamic();
platform.bootstrapModule(app_module_1.AppModule);


/***/ }),

/***/ 400:
/***/ (function(module, exports) {

module.exports = "<app-header></app-header>\r\n<!--<app-leftsidebar></app-leftsidebar>\r\n<app-content></app-content>-->\r\n\r\n";

/***/ }),

/***/ 401:
/***/ (function(module, exports) {

module.exports = "<nav class=\"navbar navbar-default navbar-fixed-top\">\r\n  <div class=\"container-fluid\">\r\n    <div class=\"navbar-header\">\r\n      <a class=\"navbar-brand\" href=\"#\">IGPS</a>\r\n    </div>\r\n    <div class=\"collapse navbar-collapse\">\r\n      <ul *ngIf=\"state.logged\" class=\"nav navbar-nav navbar-right\">\r\n        <li class=\"dropdown\">\r\n          <a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" role=\"button\" aria-haspopup=\"true\" aria-expanded=\"false\"> {{state.username}} <span class=\"caret\"></span></a>\r\n          <ul class=\"dropdown-menu\">\r\n            <li><a href=\"#\">Профиль</a></li>\r\n            <li><a href=\"#\">Сообщения</a></li>\r\n            <li role=\"separator\" class=\"divider\"></li>\r\n            <li><a href=\"#\">Написать обзор</a></li>\r\n            <li><a href=\"#\">Написать публикацию</a></li>\r\n            <li><a href=\"#\">Объявление о продаже</a></li>\r\n            <li role=\"separator\" class=\"divider\"></li>\r\n            <li (click)=\"logout()\"><a href=\"#\">Выход</a></li>\r\n          </ul>\r\n        </li>\r\n      </ul>\r\n      <ul *ngIf=\"!state.logged\" class=\"nav navbar-nav navbar-right\">\r\n        <li (click)=\"openModal('log-in');\"><a href=\"#\">Вход</a></li>\r\n        <li (click)=\"openModal('register');\"><a href=\"#\">Регистрация</a></li>\r\n      </ul>\r\n    </div>\r\n  </div>\r\n</nav>\r\n<div *ngIf=\"!state.logged\">\r\n  <div id=\"log-in\" class=\"modal fade\" role=\"dialog\">\r\n    <div class=\"modal-dialog\">\r\n      <div class=\"modal-content\">\r\n        <div class=\"modal-header\">\r\n          <button type=\"button\" class=\"close\" data-dismiss=\"modal\"></button>\r\n          <h4 class=\"modal-title\">Вход</h4><span *ngIf=\"errors.login\">{{errors.login}}</span>\r\n        </div>\r\n        <div class=\"modal-body\">\r\n          <login-form #login></login-form>\r\n        </div>\r\n        <div class=\"modal-footer\">\r\n          <button class=\"btn btn-primary\" type=\"button\" (click)=\"auth(login.loginForm.value)\" [disabled]=\"!login.loginForm.valid\">Войти</button>\r\n          <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Зыкрыть</button>\r\n        </div>\r\n      </div>\r\n    </div>\r\n  </div>\r\n  <div id=\"register\" class=\"modal fade\" role=\"dialog\">\r\n    <div class=\"modal-dialog\">\r\n      <div class=\"modal-content\">\r\n        <div class=\"modal-header\">\r\n          <button type=\"button\" class=\"close\" data-dismiss=\"modal\"></button>\r\n          <h4 class=\"modal-title\">Регистрация</h4>\r\n        </div>\r\n        <div class=\"modal-body\">\r\n          <register-form #register></register-form>\r\n        </div>\r\n        <div class=\"modal-footer\">\r\n          <button class=\"btn btn-primary\" type=\"button\" (click)=\"reg(register.registerForm.value)\" [disabled]=\"!register.registerForm.valid\">Зарегистрироваться</button>\r\n          <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Зыкрыть</button>\r\n        </div>\r\n      </div>\r\n    </div>\r\n  </div>\r\n</div>\r\n";

/***/ }),

/***/ 402:
/***/ (function(module, exports) {

module.exports = "<form [formGroup]=\"loginForm\">\r\n  <div class=\"form-group\">\r\n    <label for=\"email\" class=\"\">Email</label>\r\n    <span *ngIf=\"formErrors.email\">{{formErrors.email}}</span>\r\n    <input type=\"text\" class=\"form-control\" id=\"email\" name=\"email\" placeholder=\"example@mail.com\"\r\n           formControlName=\"email\">\r\n  </div>\r\n  <div class=\"form-group\">\r\n    <label for=\"password\" class=\"\">Пароль</label>\r\n    <span *ngIf=\"formErrors.password\">{{formErrors.password}}</span>\r\n    <input type=\"password\" class=\"form-control\" id=\"password\" name=\"password\"\r\n           formControlName=\"password\">\r\n  </div>\r\n</form>\r\n";

/***/ }),

/***/ 403:
/***/ (function(module, exports) {

module.exports = "<form [formGroup]=\"registerForm\">\r\n  <div class=\"form-group\">\r\n    <label for=\"username\">Псевдоним</label>\r\n    <span *ngIf=\"formErrors.username\">{{formErrors.username}}</span>\r\n    <input type=\"text\" class=\"form-control\" id=\"username\" formControlName=\"username\">\r\n  </div>\r\n  <div class=\"form-group\">\r\n    <label for=\"email\">Email</label>\r\n    <span *ngIf=\"formErrors.email\">{{formErrors.email}}</span>\r\n    <input type=\"text\" class=\"form-control\" id=\"email\" placeholder=\"example@mail.com\" formControlName=\"email\">\r\n  </div>\r\n  <div class=\"form-group\">\r\n    <label for=\"password\">Пароль</label>\r\n    <span *ngIf=\"formErrors.password\">{{formErrors.password}}</span>\r\n    <input type=\"password\" class=\"form-control\" id=\"password\" placeholder=\"Не менее 6 символов\" formControlName=\"password\">\r\n  </div>\r\n  <div class=\"form-group\">\r\n    <label for=\"passwordcopy\">Повторите пароль</label>\r\n    <span *ngIf=\"formErrors.passwordcopy\">{{formErrors.passwordcopy}}</span>\r\n    <input type=\"password\" class=\"form-control\" id=\"passwordcopy\" formControlName=\"passwordcopy\">\r\n  </div>\r\n</form>\r\n";

/***/ }),

/***/ 404:
/***/ (function(module, exports) {

module.exports = ""

/***/ }),

/***/ 405:
/***/ (function(module, exports) {

module.exports = "\r\n\r\n"

/***/ }),

/***/ 406:
/***/ (function(module, exports) {

module.exports = ""

/***/ }),

/***/ 407:
/***/ (function(module, exports) {

module.exports = ""

/***/ })

},[239]);