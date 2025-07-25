import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable, shareReplay, tap } from "rxjs";
import { environment } from "src/environments/environment";
import { ApiResponse } from "../shared/interfaces/common.interface";
import { UserApiInterface } from "../shared/interfaces/user-api-interface";
import { UserInterface } from "../shared/interfaces/user-interface";

const TOKEN_KEY = "auth-token";
const REFRESHTOKEN_KEY = "auth-refreshtoken";
const USER_KEY = "auth-user";
@Injectable({
  providedIn: "root",
})
export class UserService {
  constructor(private http: HttpClient) {}

  getUsers(
    page: string = "0",
    size: string = "10",
    sort: string = "asc"
  ): Observable<UserApiInterface> {
    const params = new HttpParams({ fromObject: { page, size, sort } });
    const requestOptions = { params };
    return this.http
      .get<UserApiInterface>(
        `${environment.apiUrl}/api/v0.0.1/users`,
        requestOptions
      )
      .pipe(
        // delay(2000),
        map((data) => {
          return data;
        }),
        tap({
          // next: (val) => console.log('getUsers: ', val),
          error: (err) => console.error("getUsers: ", err),
        }),
        shareReplay(1)
      );
  }

  getCurrentUser(): Observable<UserInterface> {
    return this.http
      .get<UserInterface>(`${environment.apiUrl}/api/v0.0.1/users/me`)
      .pipe(
        map((data) => {
          return data;
        }),
        tap({
          // next: (val) => console.log('getCurrentUser: ', val),
          error: (err) => console.error("getCurrentUserError: ", err),
        })
      );
  }

  getUserById(id: number): Observable<UserInterface> {
    return this.http.get<UserInterface>(
      `${environment.apiUrl}/api/v0.0.1/users/${id}`
    );
  }

  getUserByName(username: string = "admin"): Observable<UserInterface> {
    return this.http
      .get<UserInterface>(
        `${environment.apiUrl}/api/v0.0.1/users/username/${username}`
      )
      .pipe(
        map((data) => {
          return data;
        }),
        tap({
          // next: (val) => console.log('getUserByName', val),
          error: (err) => console.error("getUserByName: ", err),
        }),
        shareReplay(1)
      );
  }

  search(searchString: string): Observable<UserInterface[]> {
    return this.http
      .get<UserInterface[]>(
        `${environment.apiUrl}/api/v0.0.1/users/search?q=${searchString}`
      )
      .pipe(
        map((data) => {
          return data;
        }),
        tap({
          error: (err) => console.error("searchError: ", err),
        })
      );
  }

  getRoles(): Observable<[string]> {
    return this.http
      .get<[string]>(`${environment.apiUrl}/api/v0.0.1/users/roles`)
      .pipe(
        map((data) => {
          return data;
        }),
        tap({
          // next: (val) => console.log('getRoles', val),
          error: (err) => console.error("getRoles", err),
        }),
        shareReplay(1)
      );
  }

  updateUser(id: string, payload: {}): Observable<UserApiInterface> {
    return this.http
      .put<UserApiInterface>(
        `${environment.apiUrl}/api/v0.0.1/users/${id}`,
        payload
      )
      .pipe(
        map((data) => {
          return data;
        }),
        tap({
          // next: (val) => console.log('updateUser', val),
          error: (err) => console.error("updateUser", err),
        }),
        shareReplay(1)
      );
  }

  createUser(payload: UserApiInterface): Observable<UserApiInterface> {
    return this.http
      .post<UserApiInterface>(`${environment.apiUrl}/api/v0.0.1/users`, payload)
      .pipe(
        map((data) => {
          return data;
        }),
        tap({
          // next: (val) => console.log('updateUser', val),
          error: (err) => console.error("updateUser", err),
        }),
        shareReplay(1)
      );
  }

  register(userDTO: UserApiInterface): Observable<ApiResponse> {
    const headers = new HttpHeaders({
      "Accept-Language": localStorage.getItem("lang") || "bg",
    });
    const requestOptions = { headers };
    return this.http.post<any>(
      `${environment.apiUrl}/api/v0.0.1/auth/signup`,
      userDTO,
      requestOptions
    );
  }

  verifyRegistration(token: string) {
    return this.http.put<any>(
      `${environment.apiUrl}/api/v0.0.1/auth/signup-confirm`,
      {
        token,
      }
    );
  }

  resetPassword(body: { email: string }) {
    const headers = new HttpHeaders({
      "Accept-Language": localStorage.getItem("lang") || "bg",
    });
    const requestOptions = { headers };
    return this.http.post<any>(
      `${environment.apiUrl}/api/v0.0.1/auth/forgot-password`,
      body,
      requestOptions
    );
  }

  confirmPassword(body: any) {
    return this.http.put<any>(
      `${environment.apiUrl}/api/v0.0.1/auth/forgot-password-confirm`,
      body
    );
  }

isCurrentUserAdmin(): Observable<boolean> {
  return this.getCurrentUser().pipe(
    map((user) => {
      return user.roles?.includes("ROLE_ADMIN");
    }),
    tap({
      error: (err) => console.error("Failed to check admin role", err)
    })
  );
}
}
