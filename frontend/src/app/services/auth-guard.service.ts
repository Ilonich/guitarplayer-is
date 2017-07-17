import { Injectable }     from '@angular/core';
import { CanActivate, Router }    from '@angular/router';
import { LoginingResolverService } from '../services/logining-resolver.service';

@Injectable()
export class AuthGuardService implements CanActivate {

  private isLogged: boolean;

  constructor(private logger: LoginingResolverService, private router: Router) {
    console.log('AuthGuardService created');
  }

  canActivate() {
    if (this.logger.subject.getValue().logged) return true;
    this.router.navigate(['']);
    return false;
  }
}
