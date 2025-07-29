import { Component } from "@angular/core";
import { NavbarComponent } from "../../components/navbar/navbar.component";
import { FooterComponent } from "src/app/components/footer/footer.component";

@Component({
  selector: "app-home",
  templateUrl: "./landing.component.html",
  imports: [NavbarComponent, FooterComponent],
  styleUrl: "./landing.component.scss",
})
export class LandingComponent {}
