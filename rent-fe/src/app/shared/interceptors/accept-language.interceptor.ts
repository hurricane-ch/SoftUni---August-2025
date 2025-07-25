import {
  HTTP_INTERCEPTORS,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from "@angular/common/http";
import { Injectable, Provider } from "@angular/core";
import { Observable } from "rxjs";

@Injectable()
export class AcceptLanguageHeaderInterceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    const lang = localStorage.getItem("lang") ?? "bg";
    const clonedRequest = req.clone({
      headers: req.headers.set("Accept-Language", lang),
    });
    return next.handle(clonedRequest);
  }
}

export const acceptLanguageHeaderInterceptorProvider: Provider = {
  provide: HTTP_INTERCEPTORS,
  useClass: AcceptLanguageHeaderInterceptor,
  multi: true,
};
