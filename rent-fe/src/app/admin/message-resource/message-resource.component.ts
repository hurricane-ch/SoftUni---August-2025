import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { MessageResourceListComponent } from './components/message-resource-list/message-resource-list.component';

@Component({
    selector: 'app-message-resource',
    templateUrl: './message-resource.component.html',
    styleUrls: ['./message-resource.component.scss'],
    imports: [MessageResourceListComponent, TranslatePipe]
})
export class MessageResourceComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('messageResource.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
