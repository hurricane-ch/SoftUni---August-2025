import { Component, Inject, OnInit } from "@angular/core";
import { FormBuilder, Validators, ReactiveFormsModule } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle, MatDialogContent, MatDialogClose } from "@angular/material/dialog";
import { Classifier } from "../../../../shared/interfaces/classifier-interface";
import { CdkScrollable } from "@angular/cdk/scrolling";
import { MatFormField, MatLabel, MatError } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { NgIf } from "@angular/common";
import { MatSlideToggle } from "@angular/material/slide-toggle";
import { MatButton } from "@angular/material/button";

@Component({
    selector: "app-classifier-dialog",
    templateUrl: "./classifier-dialog.component.html",
    styleUrls: ["./classifier-dialog.component.scss"],
    imports: [ReactiveFormsModule, MatDialogTitle, CdkScrollable, MatDialogContent, MatFormField, MatLabel, MatInput, NgIf, MatError, MatSlideToggle, MatButton, MatDialogClose]
})
export class ClassifierDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    code: "",
    name: ["", Validators.required],
    description: "",
    enabled: true,
  });

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<Classifier>,
    @Inject(MAT_DIALOG_DATA)
    public data: { isAdd: boolean; classifier: Classifier }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (this.data.classifier) {
      this.dialogForm.patchValue({
        code: this.data.classifier.code,
        name: this.data.classifier.name,
        description: this.data.classifier.description,
        enabled: this.data.classifier.enabled,
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
