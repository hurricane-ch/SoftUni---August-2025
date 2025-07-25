import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";

@Component({
  selector: "app-navbar",
  imports: [CommonModule, RouterLink],
  templateUrl: "./navbar.component.html",
})
export class NavbarComponent {
  showMenu = false;
  toggleNavbar() {
    this.showMenu = !this.showMenu;
  }
}
