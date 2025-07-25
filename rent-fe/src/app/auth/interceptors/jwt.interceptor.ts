import {
  HTTP_INTERCEPTORS,
  HttpEvent,
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpErrorResponse,
} from "@angular/common/http";
import { Injectable } from "@angular/core";
import { NEVER, Observable, ReplaySubject, throwError } from "rxjs";
import { catchError, filter, first, switchMap } from "rxjs/operators";
import { AuthenticationService } from "../../services/auth.service";
import { TokenStorageService } from "../../services/token.service";
import { Router } from "@angular/router";
import { environment } from "src/environments/environment";

@Injectable({ providedIn: "root" })
export class AuthJwtInterceptor implements HttpInterceptor {
  private readonly refreshedAccessTokenSubject: ReplaySubject<string | null> =
    new ReplaySubject<string | null>(1);
  private isRefreshing = false;

  private readonly AUTH_API_BASE_PATH = "api/v0.0.1/auth/";
  private readonly TOKEN_HEADER_KEY = "Authorization";

  constructor(
    private readonly tokenService: TokenStorageService,
    private readonly authService: AuthenticationService,
    private readonly router: Router
  ) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<Object>> {
    const bypassContactUrl = `${environment.apiUrl}/` + this.AUTH_API_BASE_PATH + "/contacts";
    const bypassRentalHolderUrl = `${environment.apiUrl}/` + this.AUTH_API_BASE_PATH + "/rental-holders/1";
    if (req.url === bypassContactUrl || req.url === bypassRentalHolderUrl) {
      return next.handle(req);
    }
    const isRelativeUrl = req.url.startsWith("./") || req.url.startsWith("/");
    if (req.url.includes(this.AUTH_API_BASE_PATH) || isRelativeUrl) {
      return next.handle(req);
    }
    const token = this.tokenService.getToken();
    const refreshToken = this.tokenService.getRefreshToken();
    if (token) {
      const decodedToken = this.decodeJwt(token);
      if (decodedToken.exp * 1000 < Date.now()) {
        if (refreshToken) {
          return this.refreshToken(req, next, refreshToken);
        }
        return this.navigateToLogin();
      }
      return this.getReqHandler(req, next, token);
    } else if (refreshToken) {
      return this.refreshToken(req, next, refreshToken);
    }
    return this.navigateToLogin();
  }

  private refreshTokenOrNavigateToLogin(
    request: HttpRequest<any>,
    next: HttpHandler
  ) {
    const refreshToken = this.tokenService.getRefreshToken();
    if (refreshToken) {
      return this.refreshToken(request, next, refreshToken);
    }
    return this.navigateToLogin();
  }

  private refreshToken(
    request: HttpRequest<any>,
    next: HttpHandler,
    refreshToken: string
  ) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshedAccessTokenSubject.next(null);
      return this.authService.refreshToken(refreshToken).pipe(
        catchError((error) => {
          if (error.status == "400") {
            this.tokenService.signOut();
            this.navigateToLogin();
          }
          return throwError(() => error);
        }),
        switchMap((token) => {
          this.tokenService.saveToken(token.accessToken);
          this.isRefreshing = false;
          this.refreshedAccessTokenSubject.next(token.accessToken);
          return this.getReqHandler(request, next, token.accessToken);
        })
      );
    } else {
      return this.refreshedAccessTokenSubject.pipe(
        filter((token) => token != null),
        first(),
        switchMap((token) => {
          return this.getReqHandler(request, next, token);
        })
      );
    }
  }

  private getReqHandler(
    request: HttpRequest<any>,
    next: HttpHandler,
    token: string
  ): Observable<HttpEvent<any>> {
    const req = request.clone({
      headers: request.headers.set(this.TOKEN_HEADER_KEY, "Bearer " + token),
    });
    return next.handle(req).pipe(
      catchError((error) => {
        if (
          error instanceof HttpErrorResponse &&
          (error.status === 401 || error.status === 403)
        ) {
          return this.refreshTokenOrNavigateToLogin(req, next);
        }
        return throwError(() => error);
      })
    );
  }

  private navigateToLogin() {
    this.router
      .navigate(["/auth/entranceType"])
      .then(() => console.log("Redirected to login page"));
    return NEVER;
  }

  private decodeJwt(token: string): any {
    try {
      const payload = token.split(".")[1];
      const decodedPayload = atob(
        payload.replace(/-/g, "+").replace(/_/g, "/")
      );
      return JSON.parse(decodedPayload);
    } catch (e) {
      return null;
    }
  }
}

export const authJwtInterceptorProvider = {
  provide: HTTP_INTERCEPTORS,
  useClass: AuthJwtInterceptor,
  multi: true,
};
