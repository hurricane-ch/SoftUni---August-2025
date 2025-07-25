import {ApplicationConfig, importProvidersFrom} from '@angular/core';
import {BrowserModule} from "@angular/platform-browser";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatTableModule} from "@angular/material/table";
import {ReactiveFormsModule} from "@angular/forms";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatDialogModule} from "@angular/material/dialog";
import {MatInputModule} from "@angular/material/input";
import {MatAutocompleteModule} from "@angular/material/autocomplete";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import {TranslateLoader, TranslateModule} from "@ngx-translate/core";
import {httpTranslateLoader, OtherOptions} from "./app.module";
import {HttpClient, provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";
import {authJwtInterceptorProvider} from "./auth/interceptors/jwt.interceptor";
import {loaderInterceptorProvider} from "./loader/loader.interceptor";
import {acceptLanguageHeaderInterceptorProvider} from "./shared/interceptors/accept-language.interceptor";
import {MAT_TOOLTIP_DEFAULT_OPTIONS} from "@angular/material/tooltip";
import {MAT_DATE_LOCALE} from "@angular/material/core";
import {provideAnimations} from "@angular/platform-browser/animations";
import {routes} from './app.routes';
import {
    provideRouter,
    withEnabledBlockingInitialNavigation,
    withInMemoryScrolling,
    withRouterConfig,
    withViewTransitions
} from "@angular/router";
import {DEFAULT_LOCALE} from "./shared/utils/constants";

export const appConfig: ApplicationConfig = {
    providers: [
        provideRouter(routes,
            withRouterConfig({
                onSameUrlNavigation: 'reload'
            }),
            withInMemoryScrolling({
                scrollPositionRestoration: 'top',
                anchorScrolling: 'enabled'
            }),
            withEnabledBlockingInitialNavigation(),
            withViewTransitions()
        ),
        importProvidersFrom(BrowserModule, MatProgressSpinnerModule, MatTableModule, ReactiveFormsModule, MatButtonModule, MatIconModule, MatFormFieldModule, MatDialogModule, MatInputModule, MatIconModule, MatInputModule, MatButtonModule, MatAutocompleteModule, MatSnackBarModule, TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: httpTranslateLoader,
                deps: [HttpClient],
            },
        })),
        authJwtInterceptorProvider,
        loaderInterceptorProvider,
        acceptLanguageHeaderInterceptorProvider,
        {provide: MAT_TOOLTIP_DEFAULT_OPTIONS, useValue: OtherOptions},
        provideHttpClient(withInterceptorsFromDi()),
        {provide: MAT_DATE_LOCALE, useValue: DEFAULT_LOCALE},
        provideAnimations(),
    ]
};