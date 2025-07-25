import { Injectable, Provider } from "@angular/core";
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpContextToken,
  HTTP_INTERCEPTORS,
} from "@angular/common/http";
import { Observable, timer } from "rxjs";
import { LoaderService } from "../services/loader.service";
import { filter, finalize } from "rxjs/operators";

export const LoaderInterceptorHideLoaderContext = new HttpContextToken<boolean>(
  () => false
);

@Injectable()
export class LoaderInterceptor implements HttpInterceptor {
  constructor(private readonly loaderService: LoaderService) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    // Allow to bypass loader interceptor for some requests
    if (request.context.get(LoaderInterceptorHideLoaderContext)) {
      return next.handle(request);
    }

    this.loaderService.show();

    let reqDone = false;
    const start = performance.now();
    // if request takes less than 200ms, delay hiding the loader
    timer(200)
      .pipe(filter(() => reqDone))
      .subscribe(() => this.loaderService.hide());
    return next.handle(request).pipe(
      finalize(() => {
        reqDone = true;
        if (performance.now() - start >= 200) {
          this.loaderService.hide();
        }
      })
    );
  }
}

export const loaderInterceptorProvider: Provider = {
  provide: HTTP_INTERCEPTORS,
  useClass: LoaderInterceptor,
  multi: true,
};
