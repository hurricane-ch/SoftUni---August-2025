import { HttpClient, HttpHeaders , HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { TokenStorageService } from "../services/token.service";
import { Reservation, ReservationStatus } from "../shared/interfaces/reservation-interface";

@Injectable({
  providedIn: "root",
})
export class ReservationService {
  constructor(
    private http: HttpClient,
    private tokenService: TokenStorageService
  ) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${this.tokenService.getToken()}`,
      "Content-Type": "application/json"
    });
  }

  findAllByStatusAndYear(status: ReservationStatus | null, year: number): Observable<Reservation[]> {
    let url = `${environment.apiUrl}/api/v0.0.1/reservations/search?year=${year}`;
    
    if (status) {
      url += `&status=${status}`;
    }
  
    return this.http.get<Reservation[]>(url, { headers: this.getHeaders() });
  }

  findById(id: number): Observable<Reservation> {
    const url = `${environment.apiUrl}/api/v0.0.1/reservations/${id}`;
    return this.http.get<Reservation>(url, { headers: this.getHeaders() });
  }

  setPaid(id: number, paid: number): Observable<Reservation> {
    const url = `${environment.apiUrl}/api/v0.0.1/reservations/${id}/set-paid`;
    const params = new HttpParams().set('paid', paid.toString());
    return this.http.patch<Reservation>(url, null, { params, headers: this.getHeaders() });
  }  

  confirmReservation(id: number): Observable<any> {
    const url = `${environment.apiUrl}/api/v0.0.1/reservations/${id}/confirm`;
    return this.http.patch(url, null, { headers: this.getHeaders() });
  }
  
  rejectReservation(id: number): Observable<any> {
    const url = `${environment.apiUrl}/api/v0.0.1/reservations/${id}/reject`;
    return this.http.patch(url, null, { headers: this.getHeaders() });
  }
  
}
