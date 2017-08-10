import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  constructor(private route: ActivatedRoute,
              private router: Router,
              private title: Title) { }

    ngOnInit() {
        this.route.data.map(data => data.title).subscribe(
            title => this.title.setTitle(title)
        );
    }

}
