import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuardService } from './services/auth-guard.service';

import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { UserProfileComponent } from './users/user-profile/user-profile.component';
import { EmailConfirmationComponent } from './confirmation/email-confirmation/email-confirmation.component';
import { PasswordResetConfirmationComponent } from './confirmation/password-reset-confirmation/password-reset-confirmation.component';
import { UsersHomeComponent } from './users/users-home/users-home.component';
import { UserPageComponent } from './users/user-page/user-page.component';
import { UsersListComponent } from './users/users-list/users-list.component';

const appRoutes: Routes = [
  {
    path: 'profile',
    component: UserProfileComponent,
    data: { title: 'Мой профиль' },
    canActivate: [AuthGuardService],
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
    path: 'users',
    component: UsersHomeComponent,
    children: [
      {
        path: '',
        component: UsersListComponent,
      },
      {
        path: ':id',
        component: UserPageComponent,
      }
    ]
  },
  {
    path: '404',
    component: PageNotFoundComponent,
    data: { title: '404 - Страница не найдена' }
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
