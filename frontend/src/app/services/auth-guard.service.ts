import {Injectable, OnInit} from '@angular/core';
import { CanActivate, Router }    from '@angular/router';
import { LoginingResolverService } from '../services/logining-resolver.service';

@Injectable()
export class AuthGuardService implements CanActivate {

    constructor(private logger: LoginingResolverService, private router: Router) {}

    canActivate() {
        if (this.logger.getAuthentication() !== null) return true;
        this.router.navigate(['']);
        return false;
    }
}
