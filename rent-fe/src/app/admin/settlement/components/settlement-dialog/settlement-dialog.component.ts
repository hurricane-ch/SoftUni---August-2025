import { Component, Inject, OnInit } from "@angular/core";
import { Validators, FormBuilder, ReactiveFormsModule } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle, MatDialogContent, MatDialogClose } from "@angular/material/dialog";
import { SettlementInterface } from "../../../../shared/interfaces/settlement-interface";
import { CdkScrollable } from "@angular/cdk/scrolling";
import { MatFormField, MatLabel, MatError } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { NgIf } from "@angular/common";
import { MatSlideToggle } from "@angular/material/slide-toggle";
import { MatButton } from "@angular/material/button";

@Component({
    selector: "app-settlement-dialog",
    templateUrl: "./settlement-dialog.component.html",
    styleUrls: ["./settlement-dialog.component.scss"],
    imports: [ReactiveFormsModule, MatDialogTitle, CdkScrollable, MatDialogContent, MatFormField, MatLabel, MatInput, NgIf, MatError, MatSlideToggle, MatButton, MatDialogClose]
})
export class SettlementDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    name: ["", Validators.required],
    code: ["", Validators.required],
    nameLat: ["", Validators.required],
    district: ["", Validators.required],
    municipality: ["", Validators.required],
    placeType: ["", Validators.required],
    tsb: ["", Validators.required],
    parentCode: ["", Validators.required],
    countryCode: ["", Validators.required],
    enabled: [true, Validators.required],
    subSettlements: [[]],
  });

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<SettlementInterface>,
    @Inject(MAT_DIALOG_DATA)
    public data: { isAdd: boolean; settlement: SettlementInterface }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (this.data.settlement) {
      this.dialogForm.patchValue({
        name: this.data.settlement.name,
        enabled: this.data.settlement.enabled,
        code: this.data.settlement.code,
        nameLat: this.data.settlement.nameLat,
        district: this.data.settlement.district,
        municipality: this.data.settlement.municipality,
        placeType: this.data.settlement.placeType,
        tsb: this.data.settlement.tsb,
        parentCode: this.data.settlement.parentCode,
        countryCode: this.data.settlement.countryCode,
        // subSettlements: this.data.settlement.subSettlements,
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
