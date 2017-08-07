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

import { AppComponent } from './app.component';
import { HEADER_COMPONENTS } from './header/header';
import { LeftsidebarComponent } from './leftsidebar/leftsidebar.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { UserProfileComponent } from './users/user-profile/user-profile.component';
import { UserPageComponent } from './users/user-page/user-page.component';
import { UsersListComponent } from './users/users-list/users-list.component';
import { EmailConfirmationComponent } from './confirmation/email-confirmation/email-confirmation.component';
import { PasswordResetConfirmationComponent } from './confirmation/password-reset-confirmation/password-reset-confirmation.component';
import { DialogsListComponent } from './dialogs/dialogs-list/dialogs-list.component';
import { DialogComponent } from './dialogs/dialog/dialog.component';
import { PostsPreviewComponent } from './posts/posts-preview/posts-preview.component';
import { PostFullComponent } from './posts/post-full/post-full.component';
import { ItemsListComponent } from './market/items-list/items-list.component';
import { ItemComponent } from './market/item/item.component';
import { MainPageComponent } from './main-page/main-page.component';


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
    AppComponent,
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
  bootstrap: [ AppComponent ],
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
