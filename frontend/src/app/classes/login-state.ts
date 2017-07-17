export class LoginState {
  logged: boolean;
  username: string;
  constructor(logged: boolean, username: string){
    this.logged = logged;
    this.username = username;
  }
}
