import {Component, OnDestroy, OnInit} from '@angular/core';
import {slideInDownAnimation} from '../animations';
import { Title } from '@angular/platform-browser';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';

@Component({
  selector: 'app-page-not-found',
  templateUrl: './page-not-found.component.html',
  styleUrls: ['./page-not-found.component.css'],
  animations: [slideInDownAnimation]
})
export class PageNotFoundComponent implements OnInit, OnDestroy {

  constructor(private route: ActivatedRoute,
              private router: Router,
              private title: Title) { }

  ngOnInit() {
    this.route.data.map(data => data.title).subscribe(
      title => this.title.setTitle(title)
    );
  }

    ngOnDestroy(): void {
    }
}
