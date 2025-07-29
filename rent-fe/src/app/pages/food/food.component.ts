import { Component } from '@angular/core';
import {NavbarComponent} from "../../components/navbar/navbar.component";
import { FooterComponent } from "src/app/components/footer/footer.component";

@Component({
  selector: 'app-food',
  imports: [
    NavbarComponent,
    FooterComponent
],
  templateUrl: './food.component.html',
  styleUrl: './food.component.scss'
})
export class FoodComponent {

}
