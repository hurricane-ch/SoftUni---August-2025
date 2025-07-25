import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Params, RouterLink } from "@angular/router";
import { SettlementInterface } from "../../../../shared/interfaces/settlement-interface";
import { SettlementService } from "../../services/settlement.service";
import { Location, NgIf, NgFor } from "@angular/common";
import { SettlementDialogComponent } from "../settlement-dialog/settlement-dialog.component";
import { MatDialog } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MatIconButton, MatButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatCard, MatCardContent, MatCardActions } from "@angular/material/card";
import { MatDivider } from "@angular/material/divider";

@Component({
    selector: "app-settlement-item",
    templateUrl: "./settlement-item.component.html",
    styleUrls: ["./settlement-item.component.scss"],
    imports: [NgIf, MatIconButton, MatIcon, MatCard, MatCardContent, MatDivider, MatCardActions, MatButton, NgFor, RouterLink]
})
export class SettlementItemComponent implements OnInit {
  settlement: SettlementInterface;

  constructor(
    private settlementService: SettlementService,
    private activatedRoute: ActivatedRoute,
    private location: Location,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize() {
    this.activatedRoute.params.subscribe((params: Params) => {
      const code = params["code"];
      this.settlementService
        .getSettlementByCode(code)
        .subscribe((settlement: SettlementInterface) => {
          console.log(settlement);
          this.settlement = settlement;
        });
    });
  }

  back(): void {
    this.location.back();
  }

  editSettlement(settlement: SettlementInterface) {
    const dialogRef = this.dialog.open(SettlementDialogComponent, {
      width: "400px",
      data: { isAdd: false, settlement },
    });
    dialogRef.afterClosed().subscribe((settlement: SettlementInterface) => {
      if (settlement) {
        console.log(settlement);
        this.settlementService
          .updateSettlement(settlement.code, settlement)
          .subscribe({
            next: (res) => {
              this.initialize();
              this._snackBar.open("Settlement updated successfully", "", {
                duration: 1000,
              });
            },
            error: (err) => {
              this._snackBar.open(
                `Error while updating the settlement: ${err.message}`,
                "Close"
              );
            },
          });
      }
    });
  }

  addSubSettlement() {
    const dialogRef = this.dialog.open(SettlementDialogComponent, {
      width: "400px",
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((settlement: SettlementInterface) => {
      if (settlement) {
        console.log(settlement);
        this.settlementService.addSettlement(settlement).subscribe({
          next: (res) => {
            this.initialize();
            this._snackBar.open("Sub Settlement added successfully", "", {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while adding the Sub Settlement: ${err.message}`,
              "Close"
            );
          },
        });
      }
    });
  }
}
