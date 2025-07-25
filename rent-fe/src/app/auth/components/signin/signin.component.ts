import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder, Validators, ReactiveFormsModule } from "@angular/forms";
import {
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
  MatSnackBar,
} from "@angular/material/snack-bar";
import { TranslateService, TranslatePipe } from "@ngx-translate/core";
import { take } from "rxjs";
import { AuthenticationService } from "src/app/services/auth.service";
import { MatCard, MatCardContent, MatCardActions, MatCardFooter } from "@angular/material/card";
import { LogoComponent } from "../logo/logo.component";
import { MatFormField, MatError, MatSuffix, MatHint } from "@angular/material/form-field";
import { MatInput } from "@angular/material/input";
import { NgIf } from "@angular/common";
import { MatIconButton, MatButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { RouterLink } from "@angular/router";

@Component({
    selector: "app-signin",
    templateUrl: "./signin.component.html",
    styleUrls: ["./signin.component.scss"],
    imports: [ReactiveFormsModule, MatCard, LogoComponent, MatCardContent, MatFormField, MatInput, NgIf, MatError, MatIconButton, MatSuffix, MatIcon, MatCardActions, MatButton, MatCardFooter, MatHint, RouterLink, TranslatePipe]
})
export class SigninComponent implements OnInit {
  hide = true;
  submitted = false;
  horizontalPosition: MatSnackBarHorizontalPosition = "center";
  verticalPosition: MatSnackBarVerticalPosition = "top";

  loginForm = this.fb.group({
    username: ["admin", [Validators.required]],
    password: ["admin", [Validators.required, Validators.minLength(5)]],
    language: null,
    remember: null,
  });

  constructor(
    public translate: TranslateService,
    private fb: UntypedFormBuilder,
    private authenticationService: AuthenticationService,
    private _snackBar: MatSnackBar
  ) {
    this.loginForm.get("language")?.valueChanges.subscribe((lang) => {
      localStorage.setItem("lang", lang);
      this.translate.use(lang);
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    console.log(this.loginForm.value);

    this.authenticationService
      .login(
        this.loginForm.get("username")!.value,
        this.loginForm.get("password")!.value,
        "signin"
      )
      .pipe(take(1))
      .subscribe({
        next: (v) => console.log(v),
        error: (err) => {
          console.error(err);
          this._snackBar.open(
            err.error.error ? err.error.error : err.error,
            "Close",
            {
              duration: 3000,
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
            }
          );
        },
      });
  }
}
