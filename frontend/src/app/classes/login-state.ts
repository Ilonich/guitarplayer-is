export class LoginState {
  logged: Boolean;
  username: String;
  constructor(logged: Boolean, username: String){
    this.logged = logged;
    this.username = username;
  }
}
