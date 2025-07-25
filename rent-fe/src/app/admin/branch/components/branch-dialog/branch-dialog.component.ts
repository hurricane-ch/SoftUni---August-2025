import { Component, Inject, OnInit } from "@angular/core";
import { UntypedFormBuilder, Validators, ReactiveFormsModule } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle, MatDialogContent, MatDialogClose } from "@angular/material/dialog";
import { fromEvent } from "rxjs";
import { BranchInterface } from "../../../../shared/interfaces/branch-interface";
import { SettlementService } from "src/app/admin/settlement/services/settlement.service";
import { Settlement } from "../../../../shared/interfaces/settlement";
import { CdkScrollable } from "@angular/cdk/scrolling";
import { MatFormField, MatLabel, MatError } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { NgIf } from "@angular/common";
import { AutocompleteSearchComponent } from "../../../components/autocomplete-search/autocomplete-search.component";
import { MatSlideToggle } from "@angular/material/slide-toggle";
import { MatButton } from "@angular/material/button";

@Component({
    selector: "app-branch-dialog",
    templateUrl: "./branch-dialog.component.html",
    styleUrls: ["./branch-dialog.component.scss"],
    imports: [
        ReactiveFormsModule,
        MatDialogTitle,
        CdkScrollable,
        MatDialogContent,
        MatFormField,
        MatLabel,
        MatInput,
        NgIf,
        MatError,
        AutocompleteSearchComponent,
        MatSlideToggle,
        MatButton,
        MatDialogClose,
    ],
})
export class BranchDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    id: [""],
    identifier: [""],
    address: [""],
    email: [""],
    phone1: [""],
    phone2: [""],
    phone3: [""],
    description: [""],
    name: [""],
    main: false,
    enabled: true,
    settlementCode: [null, [Validators.required]],
  });
  filteredSettlements: any;
  selectedSettlementCode: string | null = null;
  isLoading = false;
  minLengthTerm = 3;
  isReadOnly: boolean = false;

  constructor(
    private readonly fb: UntypedFormBuilder,
    private readonly settlementService: SettlementService,
    public dialogRef: MatDialogRef<BranchDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      isAdd: boolean;
      branch: BranchInterface;
    }
  ) {
    dialogRef.disableClose = true;
    fromEvent(document, "keyup").subscribe((event: any) => {
      if (event.key === "Escape") {
        this.dialogRef.close();
      }
    });
  }

  ngOnInit(): void {
    this.selectedSettlementCode = this.data.branch?.settlementCode ?? null;

    if (!this.data.isAdd) {
      this.settlementService
        .getInfo(this.selectedSettlementCode)
        .subscribe((settlementInfo: string) => {
          this.dialogForm.controls["settlementCode"].setValue(settlementInfo, {
            emitEvent: false,
          });
          this.isReadOnly = true;
        });

      this.dialogForm.patchValue({
        id: this.data.branch?.id,
        identifier: this.data.branch?.identifier,
        address: this.data.branch?.address,
        email: this.data.branch?.email,
        phone1: this.data.branch?.phone1,
        phone2: this.data.branch?.phone2,
        phone3: this.data.branch?.phone3,
        description: this.data.branch?.description,
        name: this.data.branch?.name,
        main: this.data.branch?.main,
        enabled: this.data.branch?.enabled,
      });
    }
    if (this.data.isAdd) {
      this.dialogForm.removeControl("id");
    }
  }

  getSettlements = (query: string) => {
    return this.settlementService.search$(query);
  };

  displaySettlement = (settlement: Settlement) =>
    settlement.fullName ?? settlement.name;

  onSubmit() {
    const data = {
      ...this.dialogForm.value,
      settlementCode: this.selectedSettlementCode,
    };
    console.log(data);
    this.dialogRef.close(data);
  }
}
