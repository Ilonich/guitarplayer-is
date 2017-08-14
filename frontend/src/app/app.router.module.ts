import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuardService } from './services/auth-guard.service';

import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { UserProfileComponent } from './components/users/user-profile/user-profile.component';
import { EmailConfirmationComponent } from './components/confirmation/email-confirmation/email-confirmation.component';
import { PasswordResetConfirmationComponent } from './components/confirmation/password-reset-confirmation/password-reset-confirmation.component';
import { UserPageComponent } from './components/users/user-page/user-page.component';
import { UsersListComponent } from './components/users/users-list/users-list.component';
import { MainPageComponent } from './components/main-page/main-page.component';
import { DialogsListComponent } from './components/dialogs/dialogs-list/dialogs-list.component';
import { DialogComponent } from './components/dialogs/dialog/dialog.component';
import { PostsPreviewComponent } from './components/posts/posts-preview/posts-preview.component';
import { PostFullComponent } from './components/posts/post-full/post-full.component';

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
