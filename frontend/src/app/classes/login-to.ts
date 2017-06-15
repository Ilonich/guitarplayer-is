export class LoginTo {
  public login: string;
  password: string;
  constructor(mail: string, pass: string) {
    this.login = mail.toLowerCase();
    this.password = pass;
  }
}
