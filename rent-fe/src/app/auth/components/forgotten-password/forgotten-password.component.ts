import { Component, OnInit } from '@angular/core';
import { Validators, UntypedFormBuilder, ReactiveFormsModule } from '@angular/forms';
import {
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
  MatSnackBar,
} from '@angular/material/snack-bar';
import { Title, Meta } from '@angular/platform-browser';
import { UserService } from 'src/app/services/user.service';
import { ApiResponse } from 'src/app/shared/interfaces/common.interface';
import { MatCard, MatCardContent, MatCardActions, MatCardFooter } from '@angular/material/card';
import { LogoComponent } from '../logo/logo.component';
import { NgIf } from '@angular/common';
import { MatFormField, MatError } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

@Component({
    selector: 'app-forgotten-password',
    templateUrl: './forgotten-password.component.html',
    styleUrls: ['./forgotten-password.component.scss'],
    imports: [ReactiveFormsModule, MatCard, LogoComponent, NgIf, MatCardContent, MatFormField, MatInput, MatError, MatCardActions, MatButton, MatCardFooter, RouterLink, TranslatePipe]
})
export class ForgottenPasswordComponent implements OnInit {
  submitted = false;
  successMsg?: string;

  resetPasswordForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';

  constructor(
    private fb: UntypedFormBuilder,
    private userService: UserService,
    private title: Title,
    private meta: Meta,
    private _snackBar: MatSnackBar
  ) {
    title.setTitle('Restore your password');
  }

  ngOnInit(): void {}

  onSubmit() {
    if (this.resetPasswordForm.invalid) {
      return;
    }

    this.userService.resetPassword(this.resetPasswordForm.value).subscribe({
      next: (success: ApiResponse) => {
        this.submitted = true;
        console.log(success.message);
        this.successMsg = success.message;
      },
      error: (e) => {
        console.error(e);
        this._snackBar.open(e.error.error, 'Close', {
          duration: 3000,
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
        });
      },
    });
  }
}
