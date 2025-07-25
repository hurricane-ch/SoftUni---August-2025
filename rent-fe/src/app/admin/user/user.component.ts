import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { UserListComponent } from './components/user-list/user-list.component';

@Component({
    selector: 'app-user',
    templateUrl: './user.component.html',
    styleUrls: ['./user.component.scss'],
    imports: [UserListComponent, TranslatePipe]
})
export class UserComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('user.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
