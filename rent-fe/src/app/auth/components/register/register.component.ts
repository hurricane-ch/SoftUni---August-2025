import { Component, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, ValidationErrors, ValidatorFn, Validators, ReactiveFormsModule } from '@angular/forms';
import { Meta, Title } from '@angular/platform-browser';
import { UserService } from 'src/app/services/user.service';
import { ApiResponse } from '../../../shared/interfaces/common.interface';
import { MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition, MatSnackBar } from '@angular/material/snack-bar';
import { NgIf } from '@angular/common';
import { MatCard, MatCardContent, MatCardActions, MatCardFooter } from '@angular/material/card';
import { LogoComponent } from '../logo/logo.component';
import { MatFormField, MatError, MatSuffix, MatHint } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIconButton, MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';

const matchPassword = (
  firstControl: string,
  secondControl: string
): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    const password = control.get(firstControl)?.value;
    const confirm = control.get(secondControl)?.value;

    if (password !== confirm) {
      control.get(secondControl)?.setErrors({ noMatch: true });
      return { noMatch: true };
    }

    return null;
  };
};

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.scss'],
    imports: [NgIf, ReactiveFormsModule, MatCard, LogoComponent, MatCardContent, MatFormField, MatInput, MatError, MatIconButton, MatSuffix, MatIcon, MatCardActions, MatButton, MatCardFooter, MatHint, RouterLink, TranslatePipe]
})
export class RegisterComponent implements OnInit {
  hide = true;
  hideMatchingPassword = true;
  submitted = false;
  successMsg?: string;

  registerForm = this.fb.group(
    {
      email: [null, [Validators.required, Validators.email]],
      username: [null, [Validators.required]],
      fullName: [null, [Validators.required]],
      identifier: [null, Validators.required],
      password: [null, [Validators.required, Validators.minLength(6)]],
      matchingPassword: [null, Validators.required],
    },
    {
      validators: [matchPassword('password', 'matchingPassword')],
    }
  );

  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';

  constructor(
    private fb: UntypedFormBuilder,
    private userService: UserService,
    private title: Title,
    private meta: Meta,
    private _snackBar: MatSnackBar
  ) {
    title.setTitle('Register');
  }

  ngOnInit(): void {}

  onSubmit() {
    if (this.registerForm.invalid) {
      return;
    }

    this.userService.register(this.registerForm.value).subscribe({
      next: (success: ApiResponse) => {
        this.submitted = true;
        this.successMsg = success.message;
      },
      error: (e) => {
        console.error(e);
        this._snackBar.open(e.error.message, 'Close', {
          duration: 3000,
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
        });
      },
    });
  }
}
