import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { HEADER_COMPONENTS } from './header/header';
import { AuthenticationService } from './services/authentication-service.service';


@NgModule({
  declarations: [
    AppComponent,
    HEADER_COMPONENTS
  ],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpModule
  ],
  bootstrap: [ AppComponent ],
  providers: [ AuthenticationService ]
})
export class AppModule { }
