import { Component, Inject, OnInit } from "@angular/core";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from "@angular/material/dialog";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MatSelectModule } from "@angular/material/select";
import { MatDialogModule } from "@angular/material/dialog";
import { CommonModule, formatDate } from "@angular/common";
import { MatSnackBar, MatSnackBarModule } from "@angular/material/snack-bar";
import { RentalItemService } from "../service/rental-item-service";
import { DEFAULT_DATE_FORMAT, DEFAULT_LOCALE } from "../../../../shared/utils/constants";
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from "@angular/material/core";
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter } from "@angular/material-moment-adapter";
import { Router } from "@angular/router";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { TermsDialogComponent } from "../terms-dialog/terms-dialog.component";

@Component({
  selector: "app-rental-item-dialog",
  templateUrl: "./reservation-dialog.component.html",
  styleUrl: "./reservation-dialog.component.scss",
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    ReactiveFormsModule,
    MatSnackBarModule,
    MatCheckboxModule
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: DEFAULT_LOCALE },
    { provide: MAT_DATE_FORMATS, useValue: DEFAULT_DATE_FORMAT },
    { provide: DateAdapter, useClass: MomentDateAdapter },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { useUtc: true } },
  ],
})
export class ReservationDialogComponent implements OnInit {
  form: FormGroup;
  // rentalItemTypes: string[] = [];
  // confirmationCodeEnabled = false;
  countdown: number = 0;
  sendCodeDisabled: boolean = false;
  countdownInterval: any;
  isConfirmed: Boolean = false;

  constructor(
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<ReservationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private rentalItemService: RentalItemService,
    private router: Router,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    const { pricePerDay, selectedDates, rentalItemId } = this.data;

    const totalDays = selectedDates?.length || 0;
    const totalPrice = pricePerDay * (totalDays-1);

    this.form = this.fb.group({
      price: [{ value: totalPrice, disabled: true }],
      fullName: [null, Validators.required],
      phone: [null, [Validators.required,  Validators.pattern(/^\+?\d[\d\s]*$/)]],
      email: [null,[Validators.required, Validators.pattern(/^[^@]+@[^@]+\.[^@]+$/),],],
      confirmCode: [{ value: null, disabled: true }, [Validators.required, Validators.pattern(/^\d{6}$/)]],
      termsAccepted: [false, Validators.requiredTrue],
      fromDate: [{ value: selectedDates?.[0] || null, disabled: true }],
      toDate: [
        {
          value: selectedDates?.[selectedDates.length - 1] || null,
          disabled: true,
        },
      ],
    });
  }

  onCancel(): void {
    this.dialogRef.close();
    this.router.navigate(['/']);
  }

  // TODO not needed. Refactor me. Use MomentDateAdapter
  getFormattedDate(date: string | Date): string {
    const locale = DEFAULT_LOCALE;
    const day = formatDate(date, "EEEE", locale);
    const capitalizedDay = day.charAt(0).toUpperCase() + day.slice(1);
    const formattedDate = formatDate(date, "dd.MM.yyyy г.", locale);
    return `${formattedDate} ( ${capitalizedDay} )`;
  }

  isVerificationReady(): boolean {
    if (
      this.form.get("fullName")?.valid &&
      this.form.get("phone")?.valid &&
      this.form.get("email")?.valid
    ) {
      return true;
    } else {
      return false;
    }
  }

  onSendCode(): void {
    const email = this.form.get("email")?.value;
    const phone = this.form.get("phone")?.value?.replace(/\s+/g, '');
    const fullName = this.form.get("fullName")?.value;
  
    if (!email || !phone || !fullName) {
      this.snackBar.open("Моля попълнете име, телефон и email.", "", {
        duration: 2500,
      });
      return;
    }
  
    this.rentalItemService.sendVerificationCode(email, phone, fullName).subscribe({
      next: (res) => {
        if (res.success) {
          this.snackBar.open("Кодът беше изпратен успешно!", "", {
            duration: 2500,
          });
  
          this.form.get("confirmCode")?.enable();
  
          // Start countdown
          this.countdown = 60;
          this.sendCodeDisabled = true;
  
          this.countdownInterval = setInterval(() => {
            this.countdown--;
            if (this.countdown <= 0) {
              clearInterval(this.countdownInterval);
              this.sendCodeDisabled = false;
            }
          }, 1000);
        } else {
          this.snackBar.open(
            res.message || "Грешка при изпращане на кода.",
            "",
            { duration: 2500 },
          );
        }
      },
      error: () => {
        this.snackBar.open("Грешка при изпращане на кода.", "", {
          duration: 2500,
        });
      },
    });
  }

  onSubmit(): void {
    if (this.form.valid && this.form.get('confirmCode')?.valid) {
      const formValues = this.form.getRawValue();
  
      const reservationDTO = {
        token: formValues.confirmCode,
        reservationNumber: "", 
        reservationDate: new Date().toISOString(),
        price: formValues.price,
        paid: 0, 
        status: "PENDING", 
        fromDate: new Date(formValues.fromDate).toISOString(),
        toDate: new Date(formValues.toDate).toISOString(),
        termsAccepted: formValues.termsAccepted,
        contractor: {
          fullName: formValues.fullName,
          entityType: "PHYSICAL",
          email: formValues.email,
          phone: formValues.phone.replace(/\s+/g, ''),
        },
      };
  
      const rentalItemId = this.data.rentalItemId;
  
      this.rentalItemService.createReservation(reservationDTO, rentalItemId).subscribe({
        next: () => {
          this.isConfirmed = true;
        },
        error: (err) => {
          this.snackBar.open(`Грешка: ${err.message}`, 'Затвори');
        },
      });
    }
  }

  openTerms(event: Event): void {
    event.preventDefault(); 
    this.dialog.open(TermsDialogComponent, {
      width: '700px',
      data: {} 
    });
  }
}