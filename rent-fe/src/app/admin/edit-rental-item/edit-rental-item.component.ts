import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar } from "@angular/material/snack-bar";
import { ActivatedRoute, Router } from '@angular/router';
import { MatButton } from "@angular/material/button";
import { MatCard } from "@angular/material/card";
import { MatInputModule } from "@angular/material/input";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter } from "@angular/material-moment-adapter";
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from "@angular/material/core";
import { DEFAULT_DATE_FORMAT, DEFAULT_LOCALE } from "../../shared/utils/constants";
import { RentalItem, RentalItemType } from 'src/app/shared/interfaces/rental-item';
import { RentalItemEditService } from './services/edit-rental-item.service';
import { MatSelectModule } from "@angular/material/select";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { CommonModule } from '@angular/common';
import { AttachedFilesComponent } from './attached-files/attached-files.component';

@Component({
  selector: 'app-edit-rental-item',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButton,
    MatCard,
    MatDatepickerModule,
    MatInputModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    AttachedFilesComponent
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: DEFAULT_LOCALE },
    { provide: MAT_DATE_FORMATS, useValue: DEFAULT_DATE_FORMAT },
    { provide: DateAdapter, useClass: MomentDateAdapter },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { useUtc: true } },
  ],
  templateUrl: './edit-rental-item.component.html',
  styleUrls: ['./edit-rental-item.component.scss']
})
export class EditRentalItemComponent implements OnInit {
  form: FormGroup;
  rentalItem!: RentalItem;
  errorMessage!: string;
  rentalItemTypes = Object.values(RentalItemType);

  constructor(
    private fb: FormBuilder,
    private rentalItemEditService: RentalItemEditService,
    private _snackBar: MatSnackBar,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      id: [null],
      name: ["", Validators.required],
      rentalItemType: [null , Validators.required],
      room: ["", Validators.required],
      price: ["", Validators.required],
      enabled: [null],
      recommendedVisitors: ["", Validators.required],
    });

    this.route.paramMap.subscribe(params => {
      const id = Number(params.get("id"));
      if (id) {
        this.loadRentalItem(id);
      }
    });
  }

  loadRentalItem(id: number): void {
    this.rentalItemEditService.findById(id).subscribe({
      next: (rentalItem: RentalItem) => {
        this.rentalItem = rentalItem;

        const rentalTypeEnumValue = rentalItem.rentalItemType as RentalItemType;

        this.form.patchValue({
          id: rentalItem.id,
          name: rentalItem.name,
          rentalItemType: this.rentalItemTypes.find(type => type === rentalTypeEnumValue),
          room: rentalItem.room,
          price: rentalItem.price,
          enabled: rentalItem.enabled,
          recommendedVisitors: rentalItem.recommendedVisitors,
        });
      },
      error: (err) => {
        this.errorMessage = err.message;
      },
    });
  }

  onSubmit() {
    this.update();
}

update() {
  this.rentalItemEditService
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
          this.router.navigate(['../admin/rental-items']);
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
