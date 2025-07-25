import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { BehaviorSubject, Observable } from "rxjs";
import { map } from "rxjs/operators";
import { User } from "../models/user";
import { environment } from "src/environments/environment";
import { TokenStorageService } from "./token.service";
import { RefreshTokenResponse } from "src/app/shared/interfaces/auth-interface";

@Injectable({ providedIn: "root" })
export class AuthenticationService {
  private userSubject: BehaviorSubject<any>;
  public user: Observable<User>;

  constructor(
    private router: Router,
    private http: HttpClient,
    private tokenStorage: TokenStorageService
  ) {
    this.userSubject = new BehaviorSubject<any>(null);
    this.user = this.userSubject.asObservable();
  }

  public get userValue(): User {
    return this.userSubject.value;
  }

  login(
    username: string,
    password: string,
    urlPiece: string
  ): Observable<User> {
    let body = new URLSearchParams();
    body.set("username", username);
    body.set("password", password);

    let options = {
      headers: new HttpHeaders().set(
        "Content-Type",
        "application/x-www-form-urlencoded"
      ),
    };
    return this.http
      .post<any>(
        `${environment.apiUrl}/api/v0.0.1/auth/${urlPiece}`,
        body.toString(),
        options
      )
      .pipe(
        map((data) => {
          this.tokenStorage.saveToken(data.token);
          this.tokenStorage.saveRefreshToken(data.refreshToken);
          this.tokenStorage.saveUser(data);
          this.userSubject.next(data);
          this.router.navigate(["/admin"]);
          return data;
        })
      );
  }

  logoutUser(id: string) {
    return this.http.post(
      `${environment.apiUrl}/api/v0.0.1/users/signout`,
      { id },
      { responseType: "text" }
    );
  }

  logoutContractor(id: string) {
    return this.http.post(
      `${environment.apiUrl}/api/v0.0.1/contractors/signout`,
      { id },
      { responseType: "text" }
    );
  }

  refreshToken(token: string) {
    return this.http
      .post<RefreshTokenResponse>(
        `${environment.apiUrl}/api/v0.0.1/auth/token/refresh`,
        { token },
        { withCredentials: true }
      )
      .pipe(
        map((user) => {
          this.userSubject.next(user);
          return user;
        })
      );
  }
}
