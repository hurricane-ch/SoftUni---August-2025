import { Classifier } from "./classifier-interface";

export interface ClassifierApi {
  totalCount: number;
  results: Classifier[];
}
