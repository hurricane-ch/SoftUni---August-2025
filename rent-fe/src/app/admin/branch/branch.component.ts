import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import { BranchListComponent } from './components/branch-list/branch-list.component';

@Component({
    selector: 'app-branch',
    templateUrl: './branch.component.html',
    styleUrls: ['./branch.component.scss'],
    imports: [BranchListComponent, TranslatePipe]
})
export class BranchComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('branch.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
