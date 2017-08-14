import { Component } from '@angular/core';
import { MyUglyStompService } from '../services/my-ugly-stomp.service';

@Component({
  selector: 'app-root',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
})
export class MainComponent {

    constructor(socketService: MyUglyStompService){
        socketService._init();
    }
}
