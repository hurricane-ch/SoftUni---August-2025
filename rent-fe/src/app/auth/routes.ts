import {Routes} from '@angular/router';
import {AuthComponent} from "./auth.component";
import {LoginComponent} from "./components/login/login.component";
import {RegisterComponent} from "./components/register/register.component";
import {VerifyRegistrationComponent} from "./components/verify-registration/verify-registration.component";
import {ForgottenPasswordComponent} from "./components/forgotten-password/forgotten-password.component";
import {VerifyPasswordComponent} from "./components/verify-password/verify-password.component";

// export const routes: Routes = [
//     {
//         path: '',
//         loadComponent: () => import('./auth.component').then(m => m.AuthComponent)
//     }
// ];

export const routes: Routes = [
    {
        path: '',
        component: AuthComponent,
        children: [
            // {
            //   path: 'entranceType',
            //   component: EntranceTypeComponent,
            // },
            {
                path: 'login',
                component: LoginComponent,
            },
            // {
            //   path: 'signin',
            //   component: SigninComponent,
            // },
            { path: 'register', component: RegisterComponent },
            { path: 'user-registration', component: VerifyRegistrationComponent },
            { path: 'forgotten-password', component: ForgottenPasswordComponent },
            { path: 'forgot-password', component: VerifyPasswordComponent },
            { path: '**', redirectTo: '/auth/login', pathMatch: 'full' },
        ],
    },
];