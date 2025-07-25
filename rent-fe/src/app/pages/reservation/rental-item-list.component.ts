import { Component } from '@angular/core';
import {NavbarComponent} from "../../components/navbar/navbar.component";
import { RentalItemsListComponent } from "./rental-items-list/rental-items-list.component";

@Component({
  selector: 'app-rental-item-list',
  imports: [
    NavbarComponent,
    RentalItemsListComponent
],
  templateUrl: './rental-item-list.component.html',
  styleUrl: './rental-item-list.component.scss'
})
export class RentalItemListComponent {

}
