import { Component, inject, OnInit } from '@angular/core';
import { RentalItem } from 'src/app/shared/interfaces/rental-item';
import { RentalItemService } from './service/rental-item-service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-rental-items-list',
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './rental-items-list.component.html',
  styleUrl: './rental-items-list.component.scss'
})
export class RentalItemsListComponent implements OnInit {
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
