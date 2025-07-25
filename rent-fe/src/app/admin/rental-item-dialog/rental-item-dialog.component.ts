import { Component, Inject, OnInit } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA } from "@angular/material/dialog";
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatSelectModule } from "@angular/material/select";
import { MatDialogModule } from "@angular/material/dialog"; 
import { CommonModule } from "@angular/common";
import { RentalItemType } from "src/app/shared/interfaces/rental-item";
import { RentalItemService } from "../rental-item/services/rental-item.service";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";

@Component({
  selector: "app-rental-item-dialog",
  templateUrl: "./rental-item-dialog.component.html",
  styleUrl: "./rental-item-dialog.component.scss",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatSnackBarModule
  ],
})
export class RentalItemDialogComponent implements OnInit {
  form: FormGroup;
  rentalItemTypes: string[] = [];

  rentalItemTypeTranslations: { [key: string]: string } = {
    BUNGALOW: 'Бунгало',
    CARAVAN: 'Каравана',
    TENT: 'Място за палатка',
    SPOT: 'Място за каравана'
  };
  
  constructor(
    private fb: FormBuilder,
    private rentalItemService: RentalItemService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<RentalItemDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any 
  ) {}
  

  ngOnInit(): void {
    this.rentalItemTypes = Object.values(RentalItemType); 
    this.form = this.fb.group({
      name: [this.data?.name || "", Validators.required],
      rentalItemType: [this.data?.rentalItemType || "", Validators.required],
      room: [this.data?.room || null, Validators.required],
      price: [this.data?.price || null, [Validators.required, Validators.pattern("^[0-9]*$")]],
      recommendedVisitors: [this.data?.recommendedVisitors || null, Validators.required],
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.rentalItemService.create(this.form.value).subscribe({
        next: (response) => {
          this.snackBar.open("Rental item added successfully", "", { duration: 2000 });
          this.dialogRef.close(true);
        },
        error: (err) => {
          this.snackBar.open(`Error: ${err.message}`, "Close");
        },
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
