import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import { UsersService } from '../../services/users.service';

@Component({
  selector: 'app-user-page',
  templateUrl: './user-page.component.html',
  styleUrls: ['./user-page.component.css']
})
export class UserPageComponent implements OnInit {

  private id: number;
  private name: string;

  constructor(  private route: ActivatedRoute,
                private router: Router,
                private userService: UsersService ) {

  }

  ngOnInit() {
    this.route.paramMap
      .switchMap((params: ParamMap) =>
        this.userService.getUserPage(params.get('id')))
      .subscribe(
        (name: string) => {
          this.name = name;
        });
  }

  gotoList(){
    this.router.navigate(['../'], { relativeTo: this.route });
  }

}
