import { Component } from '@angular/core';
import {MyUglyStompService} from "./services/my-ugly-stomp.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {

    constructor(socketService: MyUglyStompService){
        socketService._init();
    }
}
