import { BreakpointObserver, Breakpoints } from "@angular/cdk/layout";
import { Component, ViewChild } from "@angular/core";
import { Router, RouterLinkActive, RouterLink, RouterOutlet } from "@angular/router";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";
import { TokenStorageService } from "src/app/services/token.service";
import { AuthenticationService } from "../../../services/auth.service";
import { MatSlideToggleChange, MatSlideToggle } from "@angular/material/slide-toggle";
import { MatSidenavContainer, MatSidenav, MatSidenavContent } from "@angular/material/sidenav";
import { MatIcon } from "@angular/material/icon";
import { NgIf, AsyncPipe } from "@angular/common";
import { MatDivider } from "@angular/material/divider";
import { MatToolbar } from "@angular/material/toolbar";
import { MatIconButton } from "@angular/material/button";
import { TranslatePipe } from "@ngx-translate/core";

@Component({
    selector: "app-nav",
    templateUrl: "./nav.component.html",
    styleUrls: ["./nav.component.scss"],
    imports: [
        MatSidenavContainer,
        MatSidenav,
        RouterLinkActive,
        RouterLink,
        MatIcon,
        NgIf,
        MatDivider,
        MatSlideToggle,
        MatSidenavContent,
        MatToolbar,
        MatIconButton,
        RouterOutlet,
        AsyncPipe,
        TranslatePipe,
    ],
})
export class NavComponent {
  @ViewChild(MatSidenavContainer) sidenavContainer: MatSidenavContainer;

  public userToken = this.tokenStorage.getUser();
  public user: { roles: string[]; directorateCode: string } = JSON.parse(
    localStorage.getItem("auth-user") || "{}"
  );
  public isHandset: boolean = false;

  isHandset$: Observable<boolean> = this.breakpointObserver
    .observe(Breakpoints.Handset)
    .pipe(map((result) => result.matches));

  constructor(
    private breakpointObserver: BreakpointObserver,
    private authenticationService: AuthenticationService,
    private router: Router,
    private tokenStorage: TokenStorageService,
  ) {
    if (localStorage.getItem("theme")) {
      document.body.classList.add(localStorage.getItem("theme") || "");
      this.checked = true;
    }
    this.isHandset = this.breakpointObserver.isMatched(Breakpoints.Handset);
  }

  checked: boolean = false;

  ngAfterViewInit() {
    window.dispatchEvent(new Event("resize"));
  }

  logout() {
    const id = this.userToken.userId;
    this.authenticationService.logoutUser(id).subscribe();
    this.router.navigate(["/"]);
    this.tokenStorage.signOut();
  }

  public get isAdmin() {
    return this.userToken?.roles?.includes("ROLE_ADMIN");
  }

  onToggle(event: MatSlideToggleChange) {
    let theme = "light";
    if (event.checked) {
      theme = "dark";
    }
  }
}
