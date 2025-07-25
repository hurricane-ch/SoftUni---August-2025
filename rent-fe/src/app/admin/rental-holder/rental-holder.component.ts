import {Component, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCard} from "@angular/material/card";
import {RentalHolder} from "../../shared/interfaces/rental-holder";
import {RentalHolderService} from "./services/rental-holder.service";
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatInputModule} from "@angular/material/input";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter} from "@angular/material-moment-adapter";
import {DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE} from "@angular/material/core";
import {DEFAULT_DATE_FORMAT, DEFAULT_LOCALE} from "../../shared/utils/constants";

@Component({
  selector: 'app-rental-holder',
  imports: [
    ReactiveFormsModule,
    MatButton,
    MatCard,
    MatDatepickerModule,
    MatInputModule
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: DEFAULT_LOCALE },
    { provide: MAT_DATE_FORMATS, useValue: DEFAULT_DATE_FORMAT },
    { provide: DateAdapter, useClass: MomentDateAdapter },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { useUtc: true } },
  ],
  templateUrl: './rental-holder.component.html',
  styleUrl: './rental-holder.component.scss'
})
export class RentalHolderComponent implements OnInit {
  form: FormGroup;
  errorMessage!: string;

  id!: number;

  rentalHolder: RentalHolder | undefined;

  constructor(
      private fb: FormBuilder,
      private rentalHolderService: RentalHolderService,
      private _snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      id: [null],
      name: ["", Validators.required],
      openDate: ["", Validators.required],
      closeDate: ["", Validators.required],
      address: ["", Validators.required],
      phone: ["", Validators.required],
      email: ["", Validators.required],
    });

    this.initialize();
  }

  initialize(): void {
    this.rentalHolderService.findById(1).subscribe({
      next: (rentalHolder: RentalHolder) => {
        this.rentalHolder = rentalHolder;
        this.form.patchValue(this.rentalHolder);
      },
      error: (err) => {
        this.errorMessage = err;
      },
    });
  }

  onSubmit() {
      this.update();
  }

  update() {
    this.rentalHolderService
        .update(
            this.form.value.id,
            this.form.value
        )
        .subscribe({
          next: () => {
            this._snackBar.open(
                `Записът е направен успешно`,
                "",
                {
                  duration: 2000,
                  verticalPosition: "top",
                }
            );
          },
          error: (err) => {
            this._snackBar.open(
                `Възникна грешка: ${err.message}`,
                "Затвори"
            );
          },
        });
  }
}
