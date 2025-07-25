import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservationListService } from './service/reservation-service';
import { Reservation } from 'src/app/shared/interfaces/reservation-interface';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { CalendarComponent } from './calendar/calendar.component';
import { RentalItem } from 'src/app/shared/interfaces/rental-item';

@Component({
  selector: 'app-reservation-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    CalendarComponent
  ],
  templateUrl: './reservation-list.component.html',
  styleUrl: './reservation-list.component.scss'
})
export class ReservationListComponent {

  selectedYear: string = new Date().getFullYear().toString();
  groupedReservations: {
    rentalItemId: number;
    rentalItemName: string;
    rentalItemType: string;
    reservations: Reservation[];
  }[] = [];
  
  rentalItemTypeTranslations: { [key: string]: string } = {
    BUNGALOW: 'Бунгало',
    CARAVAN: 'Каравана',
    TENT: 'Място за палатка',
    SPOT: 'Място за каравана'
  };

  constructor(private reservationService: ReservationListService) {}

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations(): void {
    this.reservationService.findAll(this.selectedYear).subscribe(
      (items: RentalItem[] | null) => {
        if (items && Array.isArray(items)) {
          this.groupedReservations = items.map(item => ({
            rentalItemId: item.id,
            rentalItemName: item.name,
            rentalItemType: item.rentalItemType,
            reservations: item.reservations ?? []
          }));
        } else {
          this.groupedReservations = [];
          console.warn('Received null or invalid data for reservations');
        }
      },
      (error) => {
        console.error('Error fetching reservations:', error);
        this.groupedReservations = [];
      }
    );
  }  

  applyFilter(): void {
    this.loadReservations();
  }

  // Handle calendar selection (optional: extend this)
  onSelectedDatesChanged(event: { rentalItemId: number; selectedDates: Date[] }): void {
    console.log(`Selected dates for rental item ${event.rentalItemId}:`, event.selectedDates);
 }

 getReservationRanges(reservations: Reservation[]): { start: Date; end: Date }[] {
  return reservations.map(res => ({
    start: new Date(res.fromDate),
    end: new Date(res.toDate)
  }));
}
}
