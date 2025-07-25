export interface SettlementInterface {
  id?: string;
  code: string;
  name: string;
  nameLat: string;
  district: string;
  municipality: string;
  placeType: string;
  tsb: string;
  parentCode: string;
  settlementType: string;
  countryCode: string;
  enabled: boolean;
  subSettlements: SettlementInterface[];
  municipalityName: string;
  fullName: string;
  regionName: string;
}
