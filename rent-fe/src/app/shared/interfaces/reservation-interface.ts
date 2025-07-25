import { RentalItem } from "./rental-item";

export enum ReservationStatus {
    ALL = 'ALL',
    PENDING = 'PENDING',
    CONFIRMED = 'CONFIRMED',
    REJECTED = 'REJECTED'
}

export interface Reservation {
    id?: number; 
    reservationNumber: string;
    reservationDate?: string;
    price?: number;
    paid?: number;
    clientFullName?: string;
    clientPhone?: string;
    clientEmail?: string;
    status: ReservationStatus;
    fromDate: string;
    toDate: string;
    termsAccepted: boolean;
    contractor: {
        fullName: string;
        entityType: string;
        email: string;
        phone: string;
      };
  }
