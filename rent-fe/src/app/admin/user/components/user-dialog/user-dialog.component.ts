import { Component, Inject, OnInit } from "@angular/core";
import { UntypedFormBuilder, Validators, ReactiveFormsModule } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle, MatDialogContent, MatDialogClose } from "@angular/material/dialog";
import { UserInterface } from "src/app/shared/interfaces/user-interface";
import { MatFormField, MatLabel, MatError, MatSuffix } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { NgIf, NgFor } from "@angular/common";
import { MatIconButton, MatButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatSelect } from "@angular/material/select";
import { MatOption } from "@angular/material/core";
import { MatSlideToggle } from "@angular/material/slide-toggle";

@Component({
    selector: "app-user-dialog",
    templateUrl: "./user-dialog.component.html",
    styleUrls: ["./user-dialog.component.scss"],
    imports: [
      ReactiveFormsModule,
      MatDialogTitle,
      MatDialogContent,
      MatFormField,
      MatLabel,
      MatInput,
      NgIf,
      MatError,
      MatIconButton,
      MatSuffix,
      MatIcon,
      MatSelect,
      NgFor,
      MatOption,
      MatSlideToggle,
      MatButton,
      MatDialogClose
    ]
})
export class UserDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    id: [""],
    email: ["", [Validators.required, Validators.email]],
    fullName: ["", Validators.required],
    username: ["", Validators.required],
    enabled: true,
    password: ["", Validators.required],
    //matchingPassword: ['', Validators.required],
    identifier: [""],
    roles: ["", Validators.required],
    // branchId: ["", Validators.required],
    // directorateCode: [""],
  });

  hide = true;

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<UserDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      isAdd: boolean;
      user: UserInterface;
      roles: string[];
    }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (!this.data.isAdd) {
      this.dialogForm.patchValue({
        id: this.data.user?.id,
        email: this.data.user?.email,
        fullName: this.data.user?.fullName,
        password: this.data.user?.username,
        username: this.data.user?.username,
        enabled: this.data.user?.enabled,
        identifier: this.data.user?.identifier,
        roles: this.data.user?.roles,
        branchId: this.data.user?.branchId,
        directorateCode: this.data.user?.directorateCode,
      });
    }
    if (this.data.isAdd) {
      this.dialogForm.removeControl("id");
    }
  }

  onSubmit() {
    console.log(this.dialogForm.value);
    this.dialogRef.close(this.dialogForm.value);
  }
}
