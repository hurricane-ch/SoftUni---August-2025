import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { ActivatedRoute } from '@angular/router';
import { ContactUsService } from './services/contact-us-service';
import { RentalHolder } from '../../shared/interfaces/rental-holder';
import {NavbarComponent} from "../../components/navbar/navbar.component";
import { FooterComponent } from "src/app/components/footer/footer.component";

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatListModule, NavbarComponent, FooterComponent],
  templateUrl: './contact-us.component.html',
  styleUrl: './contact-us.component.scss',
})
export class ContactUsComponent implements OnInit {
  rentalHolder?: RentalHolder;
  errorMessage: string = '';

  constructor(
    private contactService: ContactUsService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.fetchRentalHolder();
  }

  fetchRentalHolder(): void {
    const id = Number(this.route.snapshot.paramMap.get('id')) || 1;
    this.contactService.findById(id).subscribe({
      next: (data) => {
        this.rentalHolder = data;
      },
      error: (error) => {
        this.errorMessage = 'Error fetching rental holder details!';
        console.error(error);
      },
    });
  }
}
