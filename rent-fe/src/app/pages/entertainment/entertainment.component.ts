import { Component } from '@angular/core';
import {NavbarComponent} from "../../components/navbar/navbar.component";
import { FooterComponent } from "src/app/components/footer/footer.component";

@Component({
  selector: 'app-about-us',
  imports: [
    NavbarComponent,
    FooterComponent
],
  templateUrl: './entertainment.component.html',
  styleUrl: './entertainment.component.scss'
})
export class EntertainmentComponent {

}
