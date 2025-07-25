import {
  HttpBackend,
  HttpClient,
  HttpHeaders,
  HttpParams,
} from "@angular/common/http";
import { Injectable, signal } from "@angular/core";
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  Observable,
  of,
  switchMap,
} from "rxjs";
import { TokenStorageService } from "src/app/services/token.service";
import { environment } from "src/environments/environment";
import { SettlementApiInterface } from "../../../shared/interfaces/settlement-api-interface";
import { SettlementInterface } from "../../../shared/interfaces/settlement-interface";
import { toObservable, toSignal } from "@angular/core/rxjs-interop";
import { Settlement } from "src/app/shared/interfaces/settlement";

@Injectable({
  providedIn: "root",
})
export class SettlementService {
  constructor(
    private http: HttpClient,
    private httpBackend: HttpBackend,
    private tokenService: TokenStorageService
  ) {}

  getSettlements(): Observable<SettlementInterface[]> {
    return this.http
      .get<SettlementApiInterface>(
        `${environment.apiUrl}/api/v0.0.1/settlements`
      )
      .pipe(map((data) => data.results));
  }

  getSettlementsQuery(queryString: string): Observable<SettlementInterface[]> {
    return this.http.get<SettlementInterface[]>(
      `${environment.apiUrl}/api/v0.0.1/settlements/search?q=${queryString}`
    );
  }

  getParentSettlements(): Observable<SettlementInterface[]> {
    return this.http
      .get<SettlementApiInterface>(
        `${environment.apiUrl}/api/v0.0.1/settlements/parents`
      )
      .pipe(map((data) => data.results));
  }

  getSettlementByCode(code: string): Observable<SettlementInterface> {
    return this.http.get<SettlementInterface>(
      `${environment.apiUrl}/api/v0.0.1/settlements/${code}`
    );
  }

  getSettlementInfo(code: string): Observable<string> {
    return this.http.get(`${environment.apiUrl}/api/v0.0.1/`, {
      responseType: "text",
    });
  }

  getChildrenSettlements(
    parentCode: string
  ): Observable<SettlementInterface[]> {
    return this.http
      .get<SettlementApiInterface>(
        `${environment.apiUrl}/api/v0.0.1/settlements/${parentCode}/sub-settlements`
      )
      .pipe(map((data) => data.results));
  }

  updateSettlement(
    code: string,
    payload: SettlementInterface
  ): Observable<SettlementInterface> {
    return this.http.put<SettlementInterface>(
      `${environment.apiUrl}/api/v0.0.1/settlements/${code}`,
      payload
    );
  }

  addSettlement(payload: SettlementInterface): Observable<SettlementInterface> {
    return this.http.post<SettlementInterface>(
      `${environment.apiUrl}/api/v0.0.1/settlements`,
      payload
    );
  }

  getMunicipalitiesByCode(code: string): Observable<SettlementInterface[]> {
    return this.http
      .get<SettlementApiInterface>(
        `${environment.apiUrl}/api/v0.0.1/settlements/${code}/region-settlements`
      )
      .pipe(map((data) => data.results));
  }

  getRegionsByCode(code: string): Observable<SettlementInterface[]> {
    return this.http
      .get<SettlementApiInterface>(
        `${environment.apiUrl}/api/v0.0.1/settlements/${code}/municipality-settlements`
      )
      .pipe(map((data) => data.results));
  }

  searchSettlements(q: string): Observable<SettlementInterface[]> {
    const newHttpClient = new HttpClient(this.httpBackend);
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
      Authorization: "Bearer " + token,
      // 'Accept-Language': localStorage.getItem('lang') || 'bg',
    });
    const params = new HttpParams({ fromObject: { q } });
    const requestOptions = { headers, params };
    return newHttpClient.get<SettlementInterface[]>(
      `${environment.apiUrl}/api/v0.0.1/settlements`,
      requestOptions
    );
  }

  getInfo(code: string): Observable<string> {
    const newHttpClient = new HttpClient(this.httpBackend);
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
      Authorization: "Bearer " + token,
      "Accept-Language": localStorage.getItem("lang") || "bg",
    });
    const requestOptions: Object = { headers, responseType: "text" };
    return newHttpClient.get<string>(
      `${environment.apiUrl}/api/v0.0.1/settlements/${code}/info`,
      requestOptions
    );
  }

  private searchQuery = signal("");

  private settlementList$ = toObservable(this.searchQuery).pipe(
    debounceTime(800),
    distinctUntilChanged(),
    filter((val) => val.length >= 2),
    switchMap((searchValue) => {
      console.log(searchValue);
      return this.http
        .get<Settlement[]>(
          `${environment.apiUrl}/api/v0.0.1/settlements/search?q=${searchValue}`
        )
        .pipe(catchError(() => of([])));
    })
  );

  settlementList = toSignal(this.settlementList$);

  setSearchQuery(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    console.log(value);
    this.searchQuery.set(value);
  }

  search$(query: string): Observable<Settlement[] | any> {
    return this.http
      .get<Settlement[]>(
        `${environment.apiUrl}/api/v0.0.1/settlements/search?q=${query}`
      )
      .pipe(catchError(() => of([] as Settlement[])));
  }

  getByCode$(code: string): Observable<Settlement> {
    return this.http
      .get<Settlement>(`${environment.apiUrl}/api/v0.0.1/settlements/${code}`)
      .pipe(catchError(() => of({} as Settlement)));
  }
}
