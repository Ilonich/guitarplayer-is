import { Component, OnInit } from '@angular/core';
import {ModalService} from '../services/modal.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  logged: Boolean = false;

  constructor(private modalService: ModalService) {}

  ngOnInit() {

  }

  openModal(id: string) {
    console.log('openModal(id: string)');
    this.modalService.open(id);
  }

  closeModal(id: string) {
    this.modalService.close(id);
  }

}
