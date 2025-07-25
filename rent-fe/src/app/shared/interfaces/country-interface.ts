import { Classifier } from "./classifier-interface";

export interface Country extends Classifier {
  code: string;
  isoAlpha3: string;
  continent: string;
  currencyCode: number;
  name: string;
  capital: string;
  continentName: string;
  description: string;
  enabled: boolean;
  europeanUnionMember: boolean;
}
