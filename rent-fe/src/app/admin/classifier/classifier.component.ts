import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { RouterOutlet } from '@angular/router';

@Component({
    selector: 'app-classifier',
    templateUrl: './classifier.component.html',
    styleUrls: ['./classifier.component.scss'],
    imports: [RouterOutlet, TranslatePipe]
})
export class ClassifierComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('classifier.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
