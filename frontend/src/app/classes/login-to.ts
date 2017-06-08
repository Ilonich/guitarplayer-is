export class LoginTo {
  public login: String;
  password: String;
  constructor(mail: String, pass: String) {
    this.login = mail.toLowerCase();
    this.password = pass;
  }
}
