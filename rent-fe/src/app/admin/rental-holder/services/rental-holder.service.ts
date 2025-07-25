import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {environment} from "src/environments/environment";
import {RentalHolder} from "../../../shared/interfaces/rental-holder";
import {TokenStorageService} from "../../../services/token.service";
import {ApiResponse} from "../../../shared/interfaces/common.interface";

@Injectable({
  providedIn: "root",
})
export class RentalHolderService {
  token = this.tokenService.getToken();

  constructor(private http: HttpClient,
  private tokenService: TokenStorageService) {}

  findAll() {
    const headers = new HttpHeaders({
      "Accept-Language": localStorage.getItem("lang") || "bg",
    });
    const requestOptions = { headers };

    return this.http
      .get<RentalHolder[]>(
        `${environment.apiUrl}/api/v0.0.1/rental-holders`,
        requestOptions
      );
  }

  findById(id: number): Observable<RentalHolder> {
    return this.http.get<RentalHolder>(
      `${environment.apiUrl}/api/v0.0.1/rental-holders/${id}`
    );
  }

  update(id: number, body: RentalHolder): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
        `${environment.apiUrl}/api/v0.0.1/rental-holders/${id}`,
        body
    );
  }

  create(body: RentalHolder): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(
      `${environment.apiUrl}/api/v0.0.1/rental-holders`,
      body
    );
  }
}
