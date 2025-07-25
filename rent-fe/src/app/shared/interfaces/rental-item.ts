import { Reservation } from "./reservation-interface";

export enum RentalItemType {
  BUNGALOW = "BUNGALOW",
  CARAVAN = "CARAVAN",
  TENT = "TENT",
  SPOT = "SPOT"
}

export interface RentalItem {
  id: number;
  rentalItemType: RentalItemType;         
  name: string;
  room: number;                       
  price: number;                          
  enabled: boolean;
  recommendedVisitors: number;           
  mainFile: string;
  files: string[];
  reservations: Reservation[];        
}