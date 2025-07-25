import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { SettlementInterface } from "../../../../shared/interfaces/settlement-interface";
import { SettlementService } from "../../services/settlement.service";
import { SettlementDialogComponent } from "../settlement-dialog/settlement-dialog.component";
import { FormControl, ReactiveFormsModule } from "@angular/forms";
import { Observable } from "rxjs";
import { MatFormField, MatLabel, MatPrefix } from "@angular/material/form-field";
import { MatIcon } from "@angular/material/icon";
import { MatInput } from "@angular/material/input";
import { MatButton } from "@angular/material/button";
import { NgIf, NgFor } from "@angular/common";
import { MatCard, MatCardContent, MatCardActions } from "@angular/material/card";
import { RouterLink } from "@angular/router";
import { MatDivider } from "@angular/material/divider";

@Component({
    selector: "app-settlement-list",
    templateUrl: "./settlement-list.component.html",
    styleUrls: ["./settlement-list.component.scss"],
    imports: [MatFormField, MatLabel, MatIcon, MatPrefix, MatInput, ReactiveFormsModule, MatButton, NgIf, MatCard, MatCardContent, NgFor, RouterLink, MatDivider, MatCardActions]
})
export class SettlementListComponent implements OnInit {
  settlementsQuery: SettlementInterface[];
  settlements: SettlementInterface[];
  settlementControl: FormControl = new FormControl("");
  filteredOptions: Observable<string[]>;

  constructor(
    private settlementService: SettlementService,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  searchSettlements(searchVal: string) {
    this.settlementService
      .getSettlementsQuery(searchVal)
      .subscribe((res) => (this.settlementsQuery = res));
  }

  initialize(): void {
    this.settlementService
      .getParentSettlements()
      .subscribe((settlements: SettlementInterface[]) => {
        this.settlements = settlements;
      });
  }

  addSettlement() {
    const dialogRef = this.dialog.open(SettlementDialogComponent, {
      width: "400px",
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((settlement: SettlementInterface) => {
      if (settlement) {
        console.log(settlement);
        this.settlementService.addSettlement(settlement).subscribe({
          next: (res) => {
            //this.initialize();
            this._snackBar.open("Settlement added successfully", "", {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while adding the settlement: ${err.message}`,
              "Close"
            );
          },
        });
      }
    });
  }
}
