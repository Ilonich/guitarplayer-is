import {Component, OnInit, Output, EventEmitter} from '@angular/core';

@Component({
  moduleId: module.id.toString(),
  selector: 'app-unlogged',
  templateUrl: './unlogged.component.html',
  styleUrls: ['./unlogged.component.css']
})
export class UnloggedComponent implements OnInit {
  @Output() openModal = new EventEmitter();

  ngOnInit() {
  }

  onOpenModal(id: string) {
    console.log(id);
    this.openModal.emit(id);
  }
}
