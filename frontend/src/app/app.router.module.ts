import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuardService } from './services/auth-guard.service';

import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { UserProfileComponent } from './users/user-profile/user-profile.component';
import { EmailConfirmationComponent } from './confirmation/email-confirmation/email-confirmation.component';
import { PasswordResetConfirmationComponent } from './confirmation/password-reset-confirmation/password-reset-confirmation.component';
import { UserPageComponent } from './users/user-page/user-page.component';
import { UsersListComponent } from './users/users-list/users-list.component';
import { MainPageComponent } from './main-page/main-page.component';
import { DialogsListComponent } from './dialogs/dialogs-list/dialogs-list.component';
import { DialogComponent } from './dialogs/dialog/dialog.component';
import { PostsPreviewComponent } from './posts/posts-preview/posts-preview.component';
import { PostFullComponent } from './posts/post-full/post-full.component';

const appRoutes: Routes = [
  {
    path: 'main',
    component: MainPageComponent,
    data: { title: 'Главная'},
  },
  {
    path: 'dialogs',
    component: DialogsListComponent,
    data: { title: 'Диалоги'},
    canActivate: [AuthGuardService],
    canActivateChild: [AuthGuardService],
    children: [
      {
        path: ':id',
        component: DialogComponent,
      }
    ]
  },
  {
    path: 'posts/:page',
    component: PostsPreviewComponent,
    data: { title: 'Публикации'}
  },
  {
    path: 'post/:id',
    component: PostFullComponent
  },
  {
    path: 'posts',
    redirectTo: '/posts/1'
  },
  {
    path: 'profile',
    component: UserProfileComponent,
    data: { title: 'Мой профиль' },
    canActivate: [AuthGuardService],
  },
  {
    path: 'users',
    children: [
      {
        path: '',
        component: UsersListComponent
      },
      {
        path: ':id',
        component: UserPageComponent
      }
    ]
  },
  {
    path: 'confirm-email/:token',
    component: EmailConfirmationComponent,
    data: { title: 'Подтверждение Email' }
  },
  {
    path: 'confirm-reset/:token',
    component: PasswordResetConfirmationComponent,
    data: { title: 'Подтверждение сброса пароля' }
  },
  {
    path: '404',
    component: PageNotFoundComponent,
    data: { title: '404 - Страница не найдена' }
  },
  {
    path: '',
    redirectTo: '/main',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/404'
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: true } // <-- debugging purposes only
    )
  ],
  exports: [
    RouterModule
  ]
})
export class AppRouterModule {}
