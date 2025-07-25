import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { RentalItem } from "../../../../../shared/interfaces/rental-item";
import { ApiResponse } from "src/app/shared/interfaces/common.interface";

@Injectable({
  providedIn: "root",
})
export class RentalItemService {
  constructor(private http: HttpClient) {}

  findByIdWithReservations(id: number): Observable<RentalItem> {
    const currentYear = new Date().getFullYear();
    return this.http.get<RentalItem>(
      `${environment.apiUrl}/api/v0.0.1/auth/rental-items/${id}/reservations?year=${currentYear}`,
    );
  }

  sendVerificationCode(email: string, phone: string, fullName: string) {
    const payload = { email, phone, fullName };

    return this.http.post<ApiResponse>(
      `${environment.apiUrl}/api/v0.0.1/auth/create-email-verification-code`,
      payload,
    );
  }

  createReservation(body: any, id: Number): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${environment.apiUrl}/api/v0.0.1/auth/reservations/rental-items/${id}`,
      body,
    );
  }

    createAdminReservation(body: any, id: Number): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${environment.apiUrl}/api/v0.0.1/auth/reservations/ad/rental-items/${id}`,
      body,
    );
  }
}