export class Authentication {
  private _username: string;
  private _email: string;
  private _roles: string[];
  private _publicKey: string;
  private _encodingLvl: string;
  private _csrf: string;

  public constructor(username: string, email: string, publicKey: string, encodingLvl: string, csrf: string, ...roles: string[]) {
    this._username = username;
    this._email = email;
    this._publicKey = publicKey;
    this._encodingLvl = encodingLvl;
    this._csrf = csrf;
    this._roles = roles;
  }

  public get username(): string {
    return this._username;
  }

  public get email(): string {
    return this._email;
  }

  public get publicKey(): string {
    return this._publicKey;
  }

  public get encodingLvl(): string {
    return this._encodingLvl;
  }

  public get csrf(): string {
    return this._csrf;
  }

  public hasRole(role: string): boolean {
    return undefined !== this._roles.find( s => s === role);
  }

}
