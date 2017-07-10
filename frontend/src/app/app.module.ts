import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpModule, XHRBackend, RequestOptions } from '@angular/http';

import { AppComponent } from './app.component';
import { HEADER_COMPONENTS } from './header/header';
import { LoginingResolverService } from './services/logining-resolver.service';
import { DocumentRef } from './services/document-ref.service';
import { AuthenticationService } from './services/authentication.service';
import { TokenHttpInterceptor } from './interceptor/token-http-interceptor';

export function httpFactory(
  xhrBackend: XHRBackend,
  requestOptions: RequestOptions,
  logining: LoginingResolverService,
  documentRef: DocumentRef) {
  return new TokenHttpInterceptor(xhrBackend, requestOptions, logining, documentRef);
}
export let tokenHttpInterceptor = {
  provide: TokenHttpInterceptor,
  useFactory: httpFactory,
  deps: [XHRBackend, RequestOptions, LoginingResolverService, DocumentRef]
};

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
  providers: [
    LoginingResolverService,
    DocumentRef,
    tokenHttpInterceptor,
    AuthenticationService
  ]
})
export class AppModule { }
