export class Authentication {
  private _username: String;
  private _email: String;
  private _roles: String[];
  private _publicKey: String;
  private _csrf: String;

  public constructor(username: String, email: String, publicKey: String, csrf: String, ...roles: String[]) {
    this._username = username;
    this._email = email;
    this._publicKey = publicKey;
    this._csrf = csrf;
    this._roles = roles;
  }

  public get username(): String {
    return this._username;
  }

  public get email(): String {
    return this._email;
  }

  public get publicKey(): String {
    return this._publicKey;
  }

  public get csrf(): String {
    return this._csrf;
  }

  public hasRole(role: String): Boolean {
    return undefined !== this._roles.find( s => s === role);
  }

}
