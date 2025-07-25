import {HttpClient, HttpParams} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {environment} from "src/environments/environment";
import {RentalItem} from "../../../shared/interfaces/rental-item";
import {ApiResponse} from "../../../shared/interfaces/common.interface";

@Injectable({
  providedIn: "root",
})
export class RentalItemEditService {
  constructor(
    private http: HttpClient
  ) {}

  findById(id: number): Observable<RentalItem> {
    return this.http.get<RentalItem>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}`
    );
  }

  update(id: number, body: RentalItem): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}`,
      body,
    );
  }

   // --------------------------------------------------------------------------------------------

  setMain(id: number, fileId: number, main: boolean): Observable<ApiResponse> {
    const params = new HttpParams().set("main", main.toString());
    return this.http.patch<ApiResponse>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}/files/${fileId}`,
      null,
      { params }
    );
  }
  
  uploadFile(id: string, formData: FormData) {
    return this.http.post<any[]>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}/files`,
      formData,
    );
  }

  getFiles(id: string) {
    return this.http.get<any[]>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}/files`,
    );
  }

  deleteFile(id: string, fileId: string) {
    return this.http.delete<any[]>(
      `${environment.apiUrl}/api/v0.0.1/rental-items/${id}/files/${fileId}`,
    );
  }

  downloadFile(fileId: string) {
    return this.http.get<any>(
      `${environment.apiUrl}/api/v0.0.1/files/${fileId}`,
    );
  }
}
