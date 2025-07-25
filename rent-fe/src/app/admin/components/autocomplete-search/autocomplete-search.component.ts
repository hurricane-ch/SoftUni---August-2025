import {
  AfterViewInit,
  booleanAttribute,
  ChangeDetectorRef,
  Component,
  DestroyRef,
  ElementRef,
  EventEmitter,
  Input,
  NgZone,
  OnDestroy,
  OnInit,
  Optional,
  Output,
  Self,
  ViewChild,
} from "@angular/core";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { ControlValueAccessor, NgControl, Validators } from "@angular/forms";
import {
  MatAutocomplete,
  MatAutocompleteTrigger,
  MatOption,
} from "@angular/material/autocomplete";
import {
  BehaviorSubject,
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  first,
  Observable,
  of,
  ReplaySubject,
  Subject,
  Subscription,
  switchMap,
} from "rxjs";
import { MatFormField, MatLabel, MatHint, MatSuffix } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { NgIf, AsyncPipe } from "@angular/common";
import { MatIconButton, MatButton } from "@angular/material/button";
import { MatTooltip } from "@angular/material/tooltip";
import { MatIcon } from "@angular/material/icon";
import { MatOption as MatOption_1 } from "@angular/material/core";

@Component({
    selector: "app-autocomplete-search",
    templateUrl: "./autocomplete-search.component.html",
    styleUrl: "./autocomplete-search.component.scss",
    imports: [
        MatFormField,
        MatLabel,
        MatInput,
        MatAutocompleteTrigger,
        NgIf,
        MatHint,
        MatIconButton,
        MatSuffix,
        MatTooltip,
        MatIcon,
        MatAutocomplete,
        MatOption_1,
        MatButton,
        AsyncPipe,
    ],
})
export class AutocompleteSearchComponent<T>
  implements OnInit, OnDestroy, AfterViewInit, ControlValueAccessor
{
  @Input({ required: true }) label: string;
  @Input() placeholder: string;
  @Input() fetchDataFn: (searchQuery: string) => Observable<T[]> | T[];
  @Input() disabled: boolean;
  @Input() displayFn: (element: T) => string = (element) => String(element);
  @Input() transformFn: (element: T) => any = (element) => element;
  @Input() minQueryLength = 3;
  @Input({ transform: booleanAttribute }) noHint: boolean;
  @Input() getOption: (id: any) => T | Observable<T>;
  @Input() refreshOptionsList: Observable<unknown> = new BehaviorSubject(
    null,
  ).asObservable();
  @Input() notFoundAction?: () => void;
  @Input() notFoundText: string;
  @Input() notFoundButtonText: string;
  @Output() optionSelected = new EventEmitter<any>();
  listOfOptions$: Observable<T[] | null>;
  required = false;

  onTouchedFn = () => {};
  onChange$ = new Subject<T | null>();

  writeValue$ = new ReplaySubject<T>();
  searchQuery$ = new BehaviorSubject<string>("");

  @ViewChild("searchQuery") input: ElementRef<HTMLInputElement>;
  @ViewChild("auto") matAutocompleteRef: MatAutocomplete;
  @ViewChild(MatAutocompleteTrigger)
  autocompleteTrigger: MatAutocompleteTrigger;

  private readonly subscriptions = new Subscription();

  constructor(
    @Self() @Optional() private readonly ngControl: NgControl,
    private readonly zone: NgZone,
    private readonly cd: ChangeDetectorRef,
    private readonly destroy: DestroyRef,
  ) {
    this.ngControl && (this.ngControl.valueAccessor = this);
  }

  ngOnInit(): void {
    this.placeholder = this.placeholder || this.label;
    this.noHint = this.noHint || this.minQueryLength === 0;
    const refresh$ = this.refreshOptionsList.pipe(
      takeUntilDestroyed(this.destroy),
    );
    const search$ = this.searchQuery$.pipe(
      debounceTime(500),
      distinctUntilChanged(),
    );
    this.listOfOptions$ = combineLatest([refresh$, search$]).pipe(
      switchMap(([, searchQuery]) => {
        if (searchQuery.trim().length >= this.minQueryLength) {
          // check if fetchDataFn returns an observable or a value
          const fetchDataFnResult = this.fetchDataFn(searchQuery);
          if (fetchDataFnResult instanceof Observable) {
            return fetchDataFnResult;
          }
          return of(fetchDataFnResult);
        } else {
          return of([]);
        }
      }),
    );
    this.configureNgControl();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  ngAfterViewInit(): void {
    this.subscriptions.add(
      this.writeValue$.subscribe((val) => {
        this.setInputValue(val);
      }),
    );
  }

  focusAutocomplete() {
    this.zone.runOutsideAngular(() => {
      setTimeout(() => {
        this.input.nativeElement.focus();
      });
    });
    this.autocompleteTrigger.openPanel();
  }

  onSearchUpdated(searchQuery: string) {
    this.searchQuery$.next(searchQuery);
  }

  reset() {
    this.matAutocompleteRef.options.forEach((item: MatOption) =>
      item.deselect(),
    );
    this.input.nativeElement.value = "";
    this.onChange$.next(null);
  }

  onSelected(value: T) {
    this.onChange$.next(value);
    this.optionSelected.emit(value);
  }

  onTouchedHandler() {
    this.onTouchedFn();
  }

  writeValue(transformed: any): void {
    if (this.input) {
      this.setInputValue(transformed);
    } else {
      this.writeValue$.next(transformed);
    }
  }

  registerOnChange(fn: any): void {
    this.subscriptions.add(
      this.onChange$.subscribe((value) =>
        value ? fn(this.transformFn(value)) : fn(null),
      ),
    );
  }

  registerOnTouched(fn: any): void {
    this.onTouchedFn = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  private setInputValue(transformed: any) {
    if (this.input) {
      if (!transformed) {
        this.input.nativeElement.value = "";
        this.searchQuery$.next("");
        this.reset();
      } else if (this.getOption) {
        const getOptionResult = this.getOption(transformed);
        if (getOptionResult instanceof Observable) {
          this.subscriptions.add(
            getOptionResult.pipe(first()).subscribe((option) => {
              this.input.nativeElement.value = this.displayFn(option);
            }),
          );
        } else {
          this.input.nativeElement.value = this.displayFn(getOptionResult);
        }
      } else {
        this.input.nativeElement.value = this.displayFn(transformed);
      }
      this.cd.detectChanges();
    }
  }

  private configureNgControl() {
    this.required = !!this.ngControl?.control?.hasValidator(
      Validators.required,
    );
  }
}
