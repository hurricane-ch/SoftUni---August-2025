import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { RouterOutlet } from '@angular/router';

@Component({
    selector: 'app-settlement',
    templateUrl: './settlement.component.html',
    styleUrls: ['./settlement.component.scss'],
    imports: [RouterOutlet, TranslatePipe]
})
export class SettlementComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('settlement.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
