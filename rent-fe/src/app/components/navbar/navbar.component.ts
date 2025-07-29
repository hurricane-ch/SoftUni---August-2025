import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink, RouterLinkActive } from "@angular/router";

@Component({
  selector: "app-navbar",
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: "./navbar.component.html",
})
export class NavbarComponent {
  showMenu = false;

  ngOnInit(): void {
    addEventListener("keyup", (event) => {
      if (event.key === "Escape") {
        this.closeMenu();
      }
    });
  }

  openMenu() {
    this.showMenu = true;
  }

  closeMenu() {
    this.showMenu = false;
  }
}
