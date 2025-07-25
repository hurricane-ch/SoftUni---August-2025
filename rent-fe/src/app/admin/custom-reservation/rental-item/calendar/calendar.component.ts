import { CommonModule } from "@angular/common";
import { ChangeDetectionStrategy, Component, EventEmitter, HostListener, OnInit, Output, ViewChild } from "@angular/core";
import { MatCardModule } from "@angular/material/card";
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatCalendar } from "@angular/material/datepicker";
import { RentalItemService } from "../service/rental-item-service";
import { ActivatedRoute } from "@angular/router";
import { DEFAULT_DATE_FORMAT, DEFAULT_LOCALE } from "../../../../shared/utils/constants";
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter } from "@angular/material-moment-adapter";
import 'moment/locale/bg';


@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrl: './calendar.component.scss',
  imports: [
    MatCardModule,
    MatDatepickerModule,
    CommonModule
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: DEFAULT_LOCALE },
    { provide: MAT_DATE_FORMATS, useValue: DEFAULT_DATE_FORMAT },
    { provide: DateAdapter, useClass: MomentDateAdapter },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { useUtc: true } },
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalendarComponent implements OnInit {
  @ViewChild("juneCalendar") juneCalendar!: MatCalendar<Date>;
  @ViewChild("julyCalendar") julyCalendar!: MatCalendar<Date>;
  @ViewChild("augustCalendar") augustCalendar!: MatCalendar<Date>;
  @ViewChild("septemberCalendar") septemberCalendar!: MatCalendar<Date>;
  @Output() selectedDatesChange = new EventEmitter<Date[]>();

  selectedDates: Date[] = [];
  errorMessage: string = '';
  errorX: number = 0;
  errorY: number = 0;
  rentalItemId: String | null;

  currentYear: number = new Date().getFullYear();

  constructor(private rentalItemService: RentalItemService,
       private route: ActivatedRoute,
       private dateAdapter: DateAdapter<any>
  ) {}

  // Set min and max dates (June - September)
  // minDate: Date = new Date(this.currentYear, 6, 1);
  // maxDate: Date = new Date(this.currentYear, 9, 30);

  // Define each month start date
  juneStart = new Date(this.currentYear, 6, 1);
  julyStart = new Date(this.currentYear, 7, 1);
  augustStart = new Date(this.currentYear, 8, 1);
  septemberStart = new Date(this.currentYear, 9, 1);

  restrictedRanges: { start: Date; end: Date }[] = [];

  ngOnInit(): void {
    this.dateAdapter.setLocale(DEFAULT_LOCALE);

    const rentalItemId = this.route.snapshot.paramMap.get('id');
    const year = this.currentYear.toString();
  
    if(rentalItemId != null) {
    this.rentalItemService.findAllReservationsByRentalItemIdAndYear(rentalItemId, year)
      .subscribe({
        next: (reservations: any[]) => {
          this.restrictedRanges = reservations.map(res => ({
            start: new Date(res.fromDate),
            end: new Date(new Date(res.toDate).setDate(new Date(res.toDate).getDate() - 1))
          }));
          this.refreshCalendars();
          console.log(this.restrictedRanges)
        },
        error: (err) => {
          console.error('Failed to load reservations:', err);
        }
      });
    }
  }

  // Capture mouse position when clicking on the calendar
  @HostListener('document:click', ['$event'])
  onMouseClick(event: MouseEvent) {
    this.errorX = event.clientX;
    this.errorY = event.clientY;
  }

  // Handle date selection (only consecutive dates allowed)
  onDateChange(date: Date | null): void {
    if (!date || this.isRestrictedDate(date)) return;
  
    const dateString = date.toString();
    const index = this.selectedDates.findIndex(d => d.toString() === dateString);
  
    if (index !== -1) {
      // Attempting to unselect a selected date
      if (this.selectedDates.length <= 2) {
        // Allow unselecting any if 2 or fewer dates selected
        this.selectedDates.splice(index, 1);
        this.errorMessage = '';
      } else {
        // Prevent unselecting middle dates
        const sortedDates = [...this.selectedDates].sort((a, b) => a.valueOf() - b.valueOf());
        const isFirstOrLast = dateString === sortedDates[0].toString() || dateString === sortedDates[sortedDates.length - 1].toString();
  
        if (isFirstOrLast) {
          this.selectedDates.splice(index, 1);
          this.errorMessage = '';
        } else {
          this.errorMessage = 'Не можете да премахнете тази дата!';
          this.showErrorMessage();
        }
      }
    } else {
      // Selecting a new date
      if (this.selectedDates.length === 0) {
        this.selectedDates.push(date);
        this.errorMessage = '';
      } else {
        if (this.isConsecutive(date)) {
          this.selectedDates.push(date);
          this.errorMessage = '';
        } else {
          this.errorMessage = 'Моля, изберете поредна дата!';
          this.showErrorMessage();
        }
      }
    }
  
    this.selectedDatesChange.emit(this.selectedDates);
    this.refreshCalendars();
  
    console.log("Selected Dates:", this.selectedDates);
  }
  

  // Show the error message near the mouse cursor for 2 seconds
  showErrorMessage(): void {
    setTimeout(() => {
      this.errorMessage = '';
    }, 2000);
  }

  // Check if the selected date is consecutive
  isConsecutive(date: Date): boolean {
    const sortedDates = [...this.selectedDates].sort((a, b) => a.valueOf() - b.valueOf());
    const firstDate = sortedDates[0];
    const lastDate = sortedDates[sortedDates.length - 1];
  
    if (!firstDate || !lastDate) return true;
  
    const oneDay = 1000 * 60 * 60 * 24;
    const isBefore = Math.abs(date.valueOf() - firstDate.valueOf()) === oneDay;
    const isAfter = Math.abs(date.valueOf() - lastDate.valueOf()) === oneDay;
  
    return isBefore || isAfter;
  }

  // Refresh all calendars to apply dateClass changes
  refreshCalendars(): void {
    if (this.juneCalendar) this.juneCalendar.updateTodaysDate();
    if (this.julyCalendar) this.julyCalendar.updateTodaysDate();
    if (this.augustCalendar) this.augustCalendar.updateTodaysDate();
    if (this.septemberCalendar) this.septemberCalendar.updateTodaysDate();
  }

  // Check if the date falls within a restricted range
  isRestrictedDate(date: Date): boolean {
    return this.restrictedRanges.some(range =>
      date >= range.start && date <= range.end
    );
  }

  // Add CSS class for restricted (red) and selected (green) dates
  dateClass = (date: Date) => {
    if (this.isRestrictedDate(date)) {
      return 'red-date';
    }
    if (this.isSelectedDate(date)) {
      return 'green-date';
    }
    return '';
  };

  // Check if a date is selected
  isSelectedDate(date: Date): boolean {
    return this.selectedDates.some(d => d.toString() === date.toString());
  }

  // Prevent selection of restricted dates
  dateFilter = (date: Date | null): boolean => {
    return date ? !this.isRestrictedDate(date) : false;
  };
}