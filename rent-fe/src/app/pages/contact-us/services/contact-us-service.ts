import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {environment} from "src/environments/environment";
import {RentalHolder} from "../../../shared/interfaces/rental-holder";

@Injectable({
  providedIn: "root",
})
export class ContactUsService {
  constructor(private http: HttpClient) {}

  findById(id: number): Observable<RentalHolder> {
    return this.http.get<RentalHolder>(
      `${environment.apiUrl}/api/v0.0.1/auth/rental-holder/${id}`
    );
  }
}
