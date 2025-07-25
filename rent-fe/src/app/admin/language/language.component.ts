import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { LanguageListComponent } from './components/language-list/language-list.component';

@Component({
    selector: 'app-language',
    templateUrl: './language.component.html',
    styleUrls: ['./language.component.scss'],
    imports: [LanguageListComponent, TranslatePipe]
})
export class LanguageComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('language.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
