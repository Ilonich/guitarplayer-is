import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { UnloggedComponent } from './header/unlogged/unlogged.component';
import { LoggedComponent } from './header/logged/logged.component';
import { ModalService } from './services/modal.service';
import { ModalComponent } from './directives/modal/modal.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    UnloggedComponent,
    LoggedComponent,
    ModalComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule
  ],
  providers: [ModalService],
  bootstrap: [AppComponent]
})
export class AppModule { }
