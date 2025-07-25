import { Component, OnInit } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, ValidationErrors, ValidatorFn, Validators, ReactiveFormsModule } from '@angular/forms';
import { UserService } from 'src/app/services/user.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ApiResponse } from 'src/app/shared/interfaces/common.interface';
import { MatCard, MatCardContent, MatCardActions, MatCardFooter } from '@angular/material/card';
import { LogoComponent } from '../logo/logo.component';
import { NgIf } from '@angular/common';
import { MatFormField, MatSuffix, MatError } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatIconButton, MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
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
    selector: 'app-verify-password',
    templateUrl: './verify-password.component.html',
    styleUrls: ['./verify-password.component.scss'],
    imports: [ReactiveFormsModule, MatCard, LogoComponent, NgIf, MatCardContent, MatFormField, MatInput, MatIconButton, MatSuffix, MatIcon, MatError, MatCardActions, MatButton, MatCardFooter, RouterLink, TranslatePipe]
})
export class VerifyPasswordComponent implements OnInit {
  hide = true;
  hideMatchingPassword = true;
  submitted = false;
  message?: string;
  public lang: string;

  resetPasswordForm = this.fb.group(
    {
      password: [null, [Validators.required, Validators.minLength(6)]],
      matchingPassword: [null, Validators.required],
      token: [''],
    },
    {
      validators: [matchPassword('password', 'matchingPassword')],
    }
  );

  constructor(
    private fb: UntypedFormBuilder,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    localStorage.setItem(
      'lang',
      this.route.snapshot.queryParams['lang'] || 'bg'
    );
    this.resetPasswordForm
      .get('token')
      ?.setValue(this.route.snapshot.queryParams['token']);
  }

  ngOnInit(): void {}

  onSubmit() {
    if (this.resetPasswordForm.invalid) {
      return;
    }

    this.userService.confirmPassword(this.resetPasswordForm.value).subscribe({
      next: (success: ApiResponse) => {
        this.submitted = true;
        this.message = success.message;
      },
      error: (e) => {
        console.error(e);
      },
    });
  }
}
