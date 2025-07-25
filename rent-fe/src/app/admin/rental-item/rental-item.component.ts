import { Component, OnInit, inject } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { MatTableModule } from "@angular/material/table";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MatDialog } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatCardModule } from "@angular/material/card";
import { MatButtonModule } from "@angular/material/button";
import { MatDialogModule } from "@angular/material/dialog";
import { Router } from "@angular/router";
import { RentalItem } from "src/app/shared/interfaces/rental-item";
import { RentalItemService } from "./services/rental-item.service";
import { RentalItemDialogComponent } from "../rental-item-dialog/rental-item-dialog.component";
import { MatChipsModule } from "@angular/material/chips";
import { ConfirmationDialogComponent } from "../../shared/confirmation-dialog/confirmation-dialog.component";

@Component({
  selector: "app-rental-item",
  imports: [
    ReactiveFormsModule,
    MatTableModule,
    MatIconModule,
    MatCardModule,
    MatButtonModule,
    MatDialogModule,
    MatChipsModule
  ],
  templateUrl: "./rental-item.component.html",
  styleUrl: "./rental-item.component.scss",
})
export class RentalItemComponent implements OnInit {
  rentalItemsEnabled: RentalItem[] = [];
  rentalItemsDisabled: RentalItem[] = [];
  errorMessage!: string;

  rentalItemTypeTranslations: { [key: string]: string } = {
    BUNGALOW: 'Бунгало',
    CARAVAN: 'Каравана',
    TENT: 'Място за палатка',
    SPOT: 'Място за каравана'
  };

  private rentalItemService = inject(RentalItemService);
  private _snackBar = inject(MatSnackBar);
  private router = inject(Router);
  private dialog = inject(MatDialog);

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

  openAddDialog(): void {
    const dialogRef = this.dialog.open(RentalItemDialogComponent, {
      width: '400px',
      data: {}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadRentalItems();
      }
    });
  }

  openConfirmationDialog(header: string, body: string, action: () => void): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: { header, body }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        action();
      }
    });
  }

  editItem(item: RentalItem): void {
    this.router.navigate([`admin/rental-items/${item.id}`]);
  }

  deleteItem(id: number): void {
    const header = "Delete Item";
    const body = "Are you sure you want to delete this rental item?";

    this.openConfirmationDialog(header, body, () => {
      this.rentalItemService.delete(id).subscribe({
        next: () => {
          this._snackBar.open("Item deleted successfully", "", { duration: 2000 });
          this.loadRentalItems();
        },
        error: (err) => {
          this._snackBar.open(`Error deleting item: ${err.message}`, "Close");
        },
      });
    });
  }

  enableItem(id: number): void {
    const header = "Enable Item";
    const body = "Are you sure you want to enable this rental item?";

    this.openConfirmationDialog(header, body, () => {
      this.rentalItemService.enable(id).subscribe({
        next: () => {
          this._snackBar.open("Item enabled successfully", "", { duration: 2000 });
          this.loadRentalItems();
        },
        error: (err) => {
          this._snackBar.open(`Error enabling item: ${err.message}`, "Close");
        },
      });
    });
  }

  disableItem(id: number): void {
    const header = "Disable Item";
    const body = "Are you sure you want to disable this rental item?";

    this.openConfirmationDialog(header, body, () => {
      this.rentalItemService.disable(id).subscribe({
        next: () => {
          this._snackBar.open("Item disabled successfully", "", { duration: 2000 });
          this.loadRentalItems();
        },
        error: (err) => {
          this._snackBar.open(`Error disabling item: ${err.message}`, "Close");
        },
      });
    });
  }
}
