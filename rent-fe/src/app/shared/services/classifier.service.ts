import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { map, Observable } from "rxjs";
import { environment } from "src/environments/environment";
import { Classifier } from "../interfaces/classifier-interface";
import { ClassifierApi } from "../interfaces/classifier-api-interface";
import { Classifiers } from "../enums/classifiers";

@Injectable({
  providedIn: "root",
})
export class ClassifierService {
  constructor(private http: HttpClient) {}

  getClassifiers(): Observable<Classifier[]> {
    return this.http.get<Classifier[]>(
      `${environment.apiUrl}/api/v0.0.1/classifiers`
    );
  }

  getSubClassifiers(code: Classifiers): Observable<Classifier[]> {
    return this.http.get<Classifier[]>(
      `${environment.apiUrl}/api/v0.0.1/classifiers/${code}/sub-classifiers`
    );
  }

  getClassifier(code: string): Observable<Classifier> {
    return this.http.get<Classifier>(
      `${environment.apiUrl}/api/v0.0.1/classifiers/${code}`
    );
  }

  getClassifierGrouped(code: string, group: string): Observable<Classifier> {
    return this.http.get<Classifier>(
      `${environment.apiUrl}/api/v0.0.1/classifiers/${code}/sub-classifiers?grouped=${group}`
    );
  }

  updateClassifier(code: string, payload: Classifier): Observable<Classifier> {
    return this.http.put<Classifier>(
      `${environment.apiUrl}/api/v0.0.1/classifiers/${code}`,
      payload
    );
  }

  addClassifier(payload: Classifier): Observable<Classifier> {
    return this.http.post<Classifier>(
      `${environment.apiUrl}/api/v0.0.1/classifiers/create-next`,
      payload
    );
  }

  addSubClassifier(code: string, payload: Classifier): Observable<Classifier> {
    return this.http.post<Classifier>(
      `${environment.apiUrl}/api/v0.0.1/classifiers/${code}/create-next`,
      payload
    );
  }

  getEducationQualificationLevels(): Observable<{
    secondary: Classifier[];
    higher: Classifier[];
    professional: Classifier[];
  }> {
    return this.getClassifier(Classifiers.EDUCATION_QUALIFICATION_LEVEL).pipe(
      map((res) => res.subClassifiers),
      map((res) => {
        return res.reduce(
          ({ secondary, higher, professional }, classifier) => {
            const groupedCategories = classifier.grouped.split(",");

            if (groupedCategories.includes("SECONDARY")) {
              return {
                secondary: [...secondary, classifier],
                higher,
                professional,
              };
            } else if (groupedCategories.includes("HIGHER")) {
              return {
                secondary,
                higher: [...higher, classifier],
                professional,
              };
            } else if (
              groupedCategories.includes("PROFESSIONAL_QUALIFICATION")
            ) {
              return {
                secondary,
                higher,
                professional: [...professional, classifier],
              };
            }
            return { secondary, higher, professional };
          },
          { secondary: [], higher: [], professional: [] } as {
            secondary: Classifier[];
            higher: Classifier[];
            professional: Classifier[];
          }
        );
      })
    );
  }
}
