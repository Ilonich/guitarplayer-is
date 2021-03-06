import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { ConfirmationService } from '../../../services/confirmation.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-email-confirmation',
  templateUrl: './email-confirmation.component.html',
  styleUrls: ['./email-confirmation.component.css']
})
export class EmailConfirmationComponent implements OnInit {

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
    this.confService.confirmEmail(token)
        .subscribe(success => this.success = success,
            error => this.success = false);
  }

  gotoMain() {
    this.router.navigate(['/']);
  }

}
