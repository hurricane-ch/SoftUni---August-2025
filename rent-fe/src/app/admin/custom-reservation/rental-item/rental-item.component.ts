import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { RentalItem } from "src/app/shared/interfaces/rental-item";
import { RentalItemService } from "./service/rental-item-service";
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { MatBadgeModule } from "@angular/material/badge";
import { CommonModule } from "@angular/common";
import { CalendarComponent } from "./calendar/calendar.component";
import { MatDialog } from "@angular/material/dialog";
import { ReservationDialogComponent } from "./reservation-dialog/reservation-dialog.component";
import { MatSnackBar } from "@angular/material/snack-bar";


@Component({
  selector: "app-rental-item",
  imports: [
    ReactiveFormsModule,
    MatBadgeModule,
    CommonModule,
    CalendarComponent,
    RouterModule,
  ],
  templateUrl: "./rental-item.component.html",
  styleUrl: "./rental-item.component.scss",
})
export class RentalItemComponent implements OnInit {
  @ViewChild(CalendarComponent) calendarComponent!: CalendarComponent;

  rentalItem: RentalItem | undefined;
  rentalItemForm: FormGroup;

  originalMainPhoto: string | null = null;
  currentPhoto: string | null = null;


  selectedDates: Date[] = [];

  onSelectedDatesChanged(dates: Date[]) {
    this.selectedDates = dates;
  }

  showTooltip = false;
  tooltipTop = 0;
  tooltipLeft = 0;

  @ViewChild("reserveButton") reserveButton!: ElementRef<HTMLButtonElement>;

  handleReservationClick(): void {
    if (this.selectedDates.length === 0) {
      const rect = this.reserveButton.nativeElement.getBoundingClientRect();

      // Position tooltip centered above the button
      this.tooltipTop = rect.top + window.scrollY - 40;
      this.tooltipLeft = rect.left + window.scrollX + rect.width / 2 - 70;

      this.showTooltip = true;

      setTimeout(() => {
        this.showTooltip = false;
      }, 2000);
    } else {
      this.openAddDialog();
    }
  }

  constructor(
    private route: ActivatedRoute,
    private rentalItemService: RentalItemService,
    private fb: FormBuilder,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router,
  ) {
    this.rentalItemForm = this.fb.group({
      name: ["", Validators.required],
      rentalItemType: ["", Validators.required],
      room: ["", Validators.required],
      price: ["", [Validators.required, Validators.min(0)]],
      recommendedVisitors: ["", [Validators.required, Validators.min(1)]],
      mainFile: [null, Validators.required],
      files: [[], Validators.required],
    });
  }

 ngOnInit(): void {
  const rentalItemId = this.route.snapshot.paramMap.get("id");

  if (rentalItemId) {
    this.rentalItemService.findByIdWithReservations(Number(rentalItemId)).subscribe(
      (data) => {
        this.rentalItem = data;
        if (this.rentalItem) {
          this.rentalItem.files = this.rentalItem.files || [];
          this.originalMainPhoto = this.rentalItem.mainFile || null;
          this.currentPhoto = this.originalMainPhoto;  // Set initial display photo

          this.rentalItemForm.patchValue({
            name: this.rentalItem.name,
            rentalItemType: this.rentalItem.rentalItemType,
            room: this.rentalItem.room,
            price: this.rentalItem.price,
            recommendedVisitors: this.rentalItem.recommendedVisitors,
            mainFile: this.originalMainPhoto,
            files: this.rentalItem.files,
          });
        }
      },
      (error) => console.error(error),
    );
  }
}

  openAddDialog(): void {
    const selectedDates = this.calendarComponent.selectedDates;
    const pricePerDay = this.rentalItem?.price || 0;
    const rentalItemId = this.rentalItem?.id;

    const dialogRef = this.dialog.open(ReservationDialogComponent, {
      width: "500px",
      data: {
        pricePerDay,
        selectedDates,
        rentalItemId,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.snackBar.open(
          "🎆 Направихте успешна заявка за резервация! 🎆",
          "",
          {
            duration: 10000,
            panelClass: ["absolute-center-snackbar"],
          },
        );

        setTimeout(() => {
          this.router.navigate(["/admin/custom-reservation"]);
        }, 11000);
      }
    });
  }

setPhoto(attachment: string | null) {
  this.currentPhoto = attachment;
}

}
