import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { LanguageApiInterface } from "../../../shared/interfaces/language-api-interface";
import { LanguageInterface } from "../../../shared/interfaces/language-interface";

@Injectable({
  providedIn: "root",
})
export class LanguageService {
  constructor(private http: HttpClient) {}

  getLanguages(): Observable<LanguageInterface[]> {
    const headers = new HttpHeaders({
      "Accept-Language": localStorage.getItem("lang") || "bg",
    });
    const requestOptions = { headers };
    return this.http
      .get<LanguageApiInterface>(
        `${environment.apiUrl}/api/v0.0.1/auth/langs`,
        requestOptions
      )
      .pipe(map((data) => data.results));
  }

  getLanguage(id: string): Observable<LanguageInterface> {
    return this.http.get<LanguageInterface>(
      `${environment.apiUrl}/api/v0.0.1/langs/${id}`
    );
  }

  updateLanguage(id: string, payload: {}): Observable<LanguageInterface> {
    return this.http.put<LanguageInterface>(
      `${environment.apiUrl}/api/v0.0.1/langs/${id}`,
      payload
    );
  }

  addLanguage(payload: {}): Observable<LanguageInterface> {
    return this.http.post<LanguageInterface>(
      `${environment.apiUrl}/api/v0.0.1/langs`,
      payload
    );
  }
}
