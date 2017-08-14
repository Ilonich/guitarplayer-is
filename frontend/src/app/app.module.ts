import { BrowserModule, Title } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpModule, XHRBackend, RequestOptions } from '@angular/http';
import { AppRouterModule } from './app.router.module';

import { LoginingResolverService } from './services/logining-resolver.service';
import { AuthGuardService } from './services/auth-guard.service';
import { DocumentRef } from './services/document-ref.service';
import { AuthenticationService } from './services/authentication.service';
import { TokenHttpInterceptor } from './interceptor/token-http-interceptor';
import { UsersService } from './services/users.service';
import { ConfirmationService } from './services/confirmation.service';
import { MyUglyStompService } from './services/my-ugly-stomp.service';

import { MainComponent } from './components/main.component';
import { HEADER_COMPONENTS } from './components/header/header';
import { LeftsidebarComponent } from './components/leftsidebar/leftsidebar.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { UserProfileComponent } from './components/users/user-profile/user-profile.component';
import { UserPageComponent } from './components/users/user-page/user-page.component';
import { UsersListComponent } from './components/users/users-list/users-list.component';
import { EmailConfirmationComponent } from './components/confirmation/email-confirmation/email-confirmation.component';
import { PasswordResetConfirmationComponent } from './components/confirmation/password-reset-confirmation/password-reset-confirmation.component';
import { DialogsListComponent } from './components/dialogs/dialogs-list/dialogs-list.component';
import { DialogComponent } from './components/dialogs/dialog/dialog.component';
import { PostsPreviewComponent } from './components/posts/posts-preview/posts-preview.component';
import { PostFullComponent } from './components/posts/post-full/post-full.component';
import { ItemsListComponent } from './components/market/items-list/items-list.component';
import { ItemComponent } from './components/market/item/item.component';
import { MainPageComponent } from './components/main-page/main-page.component';


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

export function stompOverSockJSFactory(
    logining: LoginingResolverService,
    tokenHttpInterceptor: TokenHttpInterceptor) {
    return new MyUglyStompService(logining, tokenHttpInterceptor);
}
export let myUglyStompService = {
    provide: MyUglyStompService,
    useFactory: stompOverSockJSFactory,
    deps: [LoginingResolverService, TokenHttpInterceptor]
};

@NgModule({
  declarations: [
    MainComponent,
    HEADER_COMPONENTS,
    LeftsidebarComponent,
    PageNotFoundComponent,
    UserProfileComponent,
    EmailConfirmationComponent,
    UserPageComponent,
    UsersListComponent,
    PasswordResetConfirmationComponent,
    DialogsListComponent,
    DialogComponent,
    PostsPreviewComponent,
    PostFullComponent,
    ItemsListComponent,
    ItemComponent,
    MainPageComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    HttpModule,
    AppRouterModule
  ],
  bootstrap: [ MainComponent ],
  providers: [
    LoginingResolverService,
    AuthGuardService,
    Title,
    DocumentRef,
    tokenHttpInterceptor,
    myUglyStompService,
    AuthenticationService,
    ConfirmationService,
    UsersService
  ]
})
export class AppModule { }
