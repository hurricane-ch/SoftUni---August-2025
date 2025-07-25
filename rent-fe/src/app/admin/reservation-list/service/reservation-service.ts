import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {environment} from "src/environments/environment";
import {RentalItem} from "../../../shared/interfaces/rental-item";

@Injectable({
  providedIn: "root",
})
export class ReservationListService {
  constructor(
    private http: HttpClient
  ) {}

  findAll(year: string): Observable<RentalItem[]> {
    const headers = new HttpHeaders({
      "Accept-Language": localStorage.getItem("lang") || "bg",
    });
  
    return this.http.get<RentalItem[]>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/reserved/${year}`,
      { headers }
    );
  }
}  