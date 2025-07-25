import { Component } from '@angular/core';
import {NavbarComponent} from "../../components/navbar/navbar.component";

@Component({
  selector: 'app-home',
  templateUrl: './landing.component.html',
  imports: [
    NavbarComponent,
  ],
  styleUrl: './landing.component.scss'
})
export class LandingComponent {

}
