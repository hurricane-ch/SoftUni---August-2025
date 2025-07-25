import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";

@Injectable({
  providedIn: "root",
})
export class CountryService {
  constructor(private http: HttpClient) {}

  getAllCountries(): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/api/v0.0.1/countries`);
  }

  getEuCountries(): Observable<any> {
    return this.http.get<any>(
      `${environment.apiUrl}/api/v0.0.1/countries/eu-members`
    );
  }
}
