import { Component, OnInit, AfterViewInit, ViewChild } from '@angular/core';
import { ReservationService } from '../../services/reservation.service';
import { Reservation, ReservationStatus } from 'src/app/shared/interfaces/reservation-interface';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ConfirmationDialogComponent } from '../../shared/confirmation-dialog/confirmation-dialog.component';
import { LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import localeBg from '@angular/common/locales/bg';

@Component({
  selector: 'app-reservation',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatCardModule,
    MatButtonModule,
    FormsModule,
    MatIconModule,
    MatDialogModule
  ],
  templateUrl: './reservation.component.html',
  styleUrls: ['./reservation.component.scss'],
  providers: [
    { provide: LOCALE_ID, useValue: 'bg' } // 👈 Apply Bulgarian locale just here
  ]
})
export class ReservationComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['reservationNumber', 'reservationDate', 'clientFullName', 'clientEmail','clientPhone', 'fromDate', 'toDate', 'price',
    'paid','status', 'actions'];
  reservations = new MatTableDataSource<Reservation>([]);
  statuses = Object.values(ReservationStatus);
  currentYear = new Date().getFullYear();
  selectedStatus: ReservationStatus = ReservationStatus.ALL;
  selectedYear: number = this.currentYear;
  editingId: number | null = null;
  tempPaidValue: number | null = null;
  totalUnpaidSum: number = 0;
  totalPaidSum: number = 0;

  reservationUnPaidDataSource = new MatTableDataSource<Reservation>();
  reservationPaidDataSource = new MatTableDataSource<Reservation>();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private reservationService: ReservationService,
    private dialog: MatDialog 
  ) {}

  statusTranslations: { [key: string]: string } = {
    ALL: 'Всички',
    PENDING: 'Изчакваща',
    CONFIRMED: 'Потвърдена',
    REJECTED: 'Отхвърлена'
  };   

  ngOnInit(): void {
    registerLocaleData(localeBg); // ✅ ensures the Bulgarian locale is available
    this.loadReservations();
  }

  ngAfterViewInit(): void {
    this.reservations.paginator = this.paginator;
    this.reservations.sort = this.sort;
  }
  
  loadReservations(): void {
    const statusToSend = this.selectedStatus === ReservationStatus.ALL ? null : this.selectedStatus;

    this.reservationService.findAllByStatusAndYear(statusToSend, this.selectedYear)
      .subscribe({
        next: (data) => {
          const safeData = data ?? []; 
          const unpaid = safeData.filter(r => (r.paid ?? 0) < (r.price ?? 0));
          const paid = safeData.filter(r => (r.paid ?? 0) >= (r.price ?? 0));
  
          this.reservationUnPaidDataSource.data = unpaid;
          this.reservationPaidDataSource.data = paid;
  
          this.totalUnpaidSum = unpaid.reduce((sum, r) => sum + (r.price ?? 0), 0);
          this.totalPaidSum = paid.reduce((sum, r) => sum + (r.price ?? 0), 0);
        },
        error: (err) => {
          console.error('Error fetching reservations:', err);
          this.reservationUnPaidDataSource.data = [];
          this.reservationPaidDataSource.data = [];
          this.totalUnpaidSum = 0;
          this.totalPaidSum = 0;
        }
      });
  }

  applyFilter(): void {
    this.loadReservations();
  }

  setConfirmed(element: Reservation): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: { 
        header: "Потвърждение на резервация", 
        body: `Искате ли да потвърдите резервация с №${element.reservationNumber} ?` 
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.reservationService.confirmReservation(element.id ?? 0).subscribe({
          next: () => {
            console.log(`Reservation #${element.reservationNumber} confirmed`);
            this.loadReservations(); 
          },
          error: (err) => console.error("Error confirming reservation", err)
        });
      }
    });
  }  
  
  setRejected(element: Reservation): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: { 
        header: "Отказване на резервация", 
        body: `Искате ли да откажете резервация с №${element.reservationNumber} ?` 
      }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.reservationService.rejectReservation(element.id ?? 0).subscribe({
          next: () => {
            console.log(`Reservation #${element.reservationNumber} rejected`);
            this.loadReservations(); 
          },
          error: (err) => console.error("Error rejecting reservation", err)
        });
      }
    });
  }
  
  editPaid(element: any) {
    this.editingId = element.id;
    this.tempPaidValue = element.paid;
  }
  
  confirmPaidUpdate(element: any) {
    if (this.tempPaidValue !== null) {
      this.reservationService.setPaid(element.id, this.tempPaidValue).subscribe({
        next: () => {
          this.loadReservations(); 
          this.editingId = null;
        },
        error: (err) => console.error("Error updating paid amount", err),
      });
    }
  }
  
  cancelPaidUpdate() {
    this.editingId = null;
  }
}
