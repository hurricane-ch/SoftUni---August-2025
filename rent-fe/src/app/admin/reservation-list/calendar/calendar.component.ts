import { CommonModule } from "@angular/common";
import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  HostListener,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
  ViewChild,
} from "@angular/core";
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS, MomentDateAdapter } from "@angular/material-moment-adapter";
import { MatCardModule } from "@angular/material/card";
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from "@angular/material/core";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatCalendar } from "@angular/material/datepicker";
import { DEFAULT_DATE_FORMAT, DEFAULT_LOCALE } from "src/app/shared/utils/constants";

@Component({
  selector: "app-calendar",
  standalone: true,
  templateUrl: "./calendar.component.html",
  styleUrl: "./calendar.component.scss",
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
export class CalendarComponent implements OnChanges {
  @ViewChild("juneCalendar") juneCalendar!: MatCalendar<Date>;
   @ViewChild("julyCalendar") julyCalendar!: MatCalendar<Date>;
   @ViewChild("augustCalendar") augustCalendar!: MatCalendar<Date>;
   @ViewChild("septemberCalendar") septemberCalendar!: MatCalendar<Date>;
   @Output() selectedDatesChange = new EventEmitter<Date[]>();
   @Input() restrictedRanges: { start: Date; end: Date }[] = [];
 
   selectedDates: Date[] = [];
   // errorMessage: string = '';
   errorX: number = 0;
   errorY: number = 0;
   rentalItemId: String | null;
 
   currentYear: number = new Date().getFullYear();
 
   constructor(
   ) {}

   ngOnChanges(changes: SimpleChanges): void {
    if (changes['restrictedRanges']) {
      // Convert string dates to Date objects if needed
      this.restrictedRanges = this.restrictedRanges.map(range => ({
        start: new Date(range.start),
        end: new Date(range.end)
      }));
      this.refreshCalendars();
    }
  }
 
   // Set min and max dates (June - September)
   // minDate: Date = new Date(this.currentYear, 6, 1);
   // maxDate: Date = new Date(this.currentYear, 9, 30);
 
   // Define each month start date
   juneStart = new Date(this.currentYear, 6, 1);
   julyStart = new Date(this.currentYear, 7, 1);
   augustStart = new Date(this.currentYear, 8, 1);
   septemberStart = new Date(this.currentYear, 9, 1);
  
   // Capture mouse position when clicking on the calendar
   @HostListener('document:click', ['$event'])
   onMouseClick(event: MouseEvent) {
     this.errorX = event.clientX;
     this.errorY = event.clientY;
   }
 
   // Show the error message near the mouse cursor for 2 seconds
   // showErrorMessage(): void {
   //   setTimeout(() => {
   //     this.errorMessage = '';
   //   }, 2000);
   // }
 
   // Refresh all calendars to apply dateClass changes
   refreshCalendars(): void {
     if (this.juneCalendar) this.juneCalendar.updateTodaysDate();
     if (this.julyCalendar) this.julyCalendar.updateTodaysDate();
     if (this.augustCalendar) this.augustCalendar.updateTodaysDate();
     if (this.septemberCalendar) this.septemberCalendar.updateTodaysDate();
   }
 
   // Check if the date falls within a restricted range
   getRestrictedRangeCount(date: Date): number {
    return this.restrictedRanges.filter(range =>
      date >= range.start && date <= range.end
    ).length;
  }
  
  isRestrictedDate(date: Date): boolean {
    return this.getRestrictedRangeCount(date) > 0;
  }
  
   // Add CSS class for restricted (red) and selected (green) dates
   dateClass = (date: Date) => {
    const rangeCount = this.getRestrictedRangeCount(date);
    if (rangeCount >= 2) {
      return 'orange-date';
    }
    if (rangeCount === 1) {
      return 'red-date';
    }
    if (this.isSelectedDate(date)) {
      return 'green-date';
    }
    return '';
  };
  
 
   // Check if a date is selected
   isSelectedDate(date: Date): boolean {
     return this.selectedDates.some(d => d.toDateString() === date.toDateString());
   }
 
   // Prevent selection of restricted dates
   dateFilter = (date: Date | null): boolean => {
     return date ? !this.isRestrictedDate(date) : false;
   };
 }