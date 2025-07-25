import {
  afterNextRender,
  AfterViewInit,
  Component,
  ContentChildren,
  Injector,
  Input,
  QueryList,
  ViewChild,
} from "@angular/core";
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import {
  MatColumnDef,
  MatTable,
  MatTableDataSource,
} from "@angular/material/table";

@Component({
  selector: "app-selection-table",
  templateUrl: "./selection-table.component.html",
  styleUrls: ["./selection-table.component.scss"],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: SelectionTableComponent,
      multi: true,
    },
  ],
})
export class SelectionTableComponent<T>
  implements AfterViewInit, ControlValueAccessor
{
  @Input() dataSource: MatTableDataSource<T>;
  @Input() dialogComponent: any;

  @ViewChild(MatTable) table: MatTable<T>;
  @ContentChildren(MatColumnDef) columnDefs: QueryList<MatColumnDef>;

  displayedColumns: (keyof T | "actions")[] = ["actions"];

  onChange = (_value: any) => {};
  onTouched = () => {};

  constructor(
    private readonly dialog: MatDialog,
    private readonly injector: Injector
  ) {}

  ngAfterViewInit(): void {
    this.columnDefs.forEach((columnDef) => {
      this.table.addColumnDef(columnDef);
      afterNextRender(
        () => {
          this.displayedColumns.unshift(columnDef.name as keyof T);
        },
        { injector: this.injector }
      );
    });
  }

  removeTableElement(elementIdx: number): void {
    this.dataSource.data.splice(elementIdx, 1);
    this.dataSource._updateChangeSubscription();
  }

  writeValue(obj: any): void {
    throw new Error("Method not implemented.");
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }
}
