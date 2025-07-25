import { Component, Inject, OnInit } from "@angular/core";
import { Validators, FormBuilder, UntypedFormBuilder, ReactiveFormsModule } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle, MatDialogContent, MatDialogClose } from "@angular/material/dialog";
import { LanguageInterface } from "../../../../shared/interfaces/language-interface";
import { CdkScrollable } from "@angular/cdk/scrolling";
import { MatFormField, MatLabel, MatError } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { NgIf } from "@angular/common";
import { MatSlideToggle } from "@angular/material/slide-toggle";
import { MatButton } from "@angular/material/button";

@Component({
    selector: "app-language-dialog",
    templateUrl: "./language-dialog.component.html",
    styleUrls: ["./language-dialog.component.scss"],
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
        MatSlideToggle,
        MatButton,
        MatDialogClose,
    ],
})
export class LanguageDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    languageId: ["", [Validators.required]],
    name: ["", [Validators.required]],
    locale: ["", [Validators.required]],
    description: ["", [Validators.required]],
    main: [null],
    enabled: [true],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<LanguageDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      isAdd: boolean;
      language: LanguageInterface;
      partialProperties?: boolean;
    }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (this.data.language) {
      this.dialogForm.patchValue({
        languageId: this.data.language.languageId,
        name: this.data.language.name,
        locale: this.data.language.locale,
        description: this.data.language.description,
        main: this.data.language.main,
        enabled: this.data.language.enabled,
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
