import { Component } from '@angular/core';
import {NavbarComponent} from "../../components/navbar/navbar.component";
import { FooterComponent } from "src/app/components/footer/footer.component";

@Component({
  selector: 'app-gallery',
  imports: [
    NavbarComponent,
    FooterComponent
],
  templateUrl: './gallery.component.html',
  styleUrl: './gallery.component.scss'
})
export class GalleryComponent {

  galleryImages: string[] = [
  'image1-min.jpg',
  'image2-min.jpg',
  'image3-min.jpg',
  'image4-min.jpg',
  'image5-min.jpg',
];
}
