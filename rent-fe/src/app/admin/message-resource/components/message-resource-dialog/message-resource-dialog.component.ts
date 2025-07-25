import { Component, Inject, OnInit } from "@angular/core";
import { FormArray, UntypedFormBuilder, Validators, ReactiveFormsModule } from "@angular/forms";
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogTitle, MatDialogContent, MatDialogClose } from "@angular/material/dialog";
import { MessageResourceInterface } from "../../../../shared/interfaces/message-resource-interface";
import { CdkScrollable } from "@angular/cdk/scrolling";
import { MatFormField, MatLabel, MatError } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { NgFor, NgIf } from "@angular/common";
import { MatButton } from "@angular/material/button";

@Component({
    selector: "app-message-resource-dialog",
    templateUrl: "./message-resource-dialog.component.html",
    styleUrls: ["./message-resource-dialog.component.scss"],
    imports: [ReactiveFormsModule, MatDialogTitle, CdkScrollable, MatDialogContent, MatFormField, MatLabel, MatInput, NgFor, NgIf, MatError, MatButton, MatDialogClose]
})
export class MessageResourceDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    code: "",
    messages: this.fb.array([]),
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<MessageResourceDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      messages: MessageResourceInterface[];
    }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (this.data.messages) {
      console.log(this.data.messages);

      this.dialogForm.patchValue({
        code: this.data.messages[0].code,
      });
      this.data.messages.forEach((message) => {
        const control = this.fb.group({
          languageId: message.languageId,
          code: message.code,
          message: [message.message, [Validators.required]],
        });
        (this.dialogForm.controls["messages"] as FormArray).push(control);
      });
    }
  }

  onSubmit() {
    console.log(this.dialogForm.controls["messages"].value);

    const messages = this.dialogForm.controls["messages"].value;
    this.dialogRef.close(messages);
  }
}
