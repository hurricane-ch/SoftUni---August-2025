import { Component, inject, OnInit } from '@angular/core';
import { RentalItem } from 'src/app/shared/interfaces/rental-item';
import { RentalItemService } from './../rental-item/services/rental-item.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-custom-reservation',
 imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './custom-reservation.component.html',
  styleUrl: './custom-reservation.component.scss'
})
export class CustomReservationComponent implements OnInit {
  rentalItemsEnabled: RentalItem[] = [];
   rentalItemsDisabled: RentalItem[] = [];
   errorMessage!: string;
 
   private rentalItemService = inject(RentalItemService);
 
   ngOnInit(): void {
     this.loadRentalItems();
   }
 
   loadRentalItems(): void {
     this.rentalItemService.findAll().subscribe({
       next: (items) => {
         this.rentalItemsEnabled = items.filter(item => item.enabled);
         this.rentalItemsDisabled = items.filter(item => !item.enabled);
       },
       error: (err) => {
         this.errorMessage = err.message;
       },
     });
   }
 }
 