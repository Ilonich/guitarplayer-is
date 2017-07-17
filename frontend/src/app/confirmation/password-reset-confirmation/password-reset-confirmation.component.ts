import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { ConfirmationService } from '../../services/confirmation.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-password-reset-confirmation',
  templateUrl: './password-reset-confirmation.component.html',
  styleUrls: ['./password-reset-confirmation.component.css']
})
export class PasswordResetConfirmationComponent implements OnInit {

  private success: boolean;

  constructor(  private route: ActivatedRoute,
                private router: Router,
                private confService: ConfirmationService,
                private title: Title ) {
  }

  ngOnInit() {
    this.route.data.map(data => data.title).subscribe(
      title => this.title.setTitle(title)
    );
    const token = this.route.snapshot.paramMap.get('token');
    this.confService.confirmReset(token)
      .subscribe(
        (success: boolean) => {
          this.success = success;
        });
  }

  gotoMain() {
    this.router.navigate(['/']);
  }

}
