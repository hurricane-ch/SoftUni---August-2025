import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {environment} from "src/environments/environment";
import {RentalItem} from "../../../shared/interfaces/rental-item";
import {ApiResponse} from "../../../shared/interfaces/common.interface";

@Injectable({
  providedIn: "root",
})
export class RentalItemService {
  constructor(
    private http: HttpClient
  ) {}

  findAll(): Observable<RentalItem[]> {
    const headers = new HttpHeaders({
      "Accept-Language": localStorage.getItem("lang") || "bg",
    });
    return this.http.get<RentalItem[]>(
      `${environment.apiUrl}/api/v0.0.1/auth/rental-items`,
      { headers },
    );
  }

  findById(id: number): Observable<RentalItem> {
    return this.http.get<RentalItem>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}`,
    );
  }

  update(id: number, body: RentalItem): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}`,
      body,
    );
  }

  create(body: RentalItem): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${environment.apiUrl}/api/v0.0.1/rental-items`,
      body,
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}`,
    );
  }

  enable(id: number): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}/enable`,
      {}
    );
  }
  
  disable(id: number): Observable<ApiResponse> {
    return this.http.patch<ApiResponse>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}/disable`,
      {}
    );
  }  

    findAllReservationsByRentalItemIdAndYear(id: String, year: String): Observable<RentalItem[]> {
    const headers = new HttpHeaders({
      "Accept-Language": localStorage.getItem("lang") || "bg",
    });
    return this.http.get<RentalItem[]>(
      `${environment.apiUrl}/api/v0.0.1/auth/reservations/${id}/${year}`,
      { headers },
    );
  }
}
