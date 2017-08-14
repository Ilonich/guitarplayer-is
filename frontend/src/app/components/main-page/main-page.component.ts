import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  x: number = 0;

  constructor() {

  }

  ngOnInit() {
    this.test();
  }

  private test(): void {
    setTimeout( () => {
      this.x++;
      if (this.x > -1) {
        this.test();
      }
    }, 10)
  }

}
