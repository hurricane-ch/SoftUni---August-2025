import { Address } from "./address-interface";

export interface Addresses {
  permanentAddress: Address;
  currentAddress: Address;
  registrationAddressType: string;
  id: number;
  addressType: string;
  settlement: Settlement;
}

export interface Settlement {
  code: string;
  name: string;
  regionCode: string;
  regionName: string;
  municipalityCode: string;
  municipalityName: string;
}
