import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatCard, MatCardContent, MatCardActions } from '@angular/material/card';
import { LogoComponent } from '../logo/logo.component';
import { MatButton } from '@angular/material/button';

@Component({
    selector: 'app-entrance-type',
    templateUrl: './entrance-type.component.html',
    styleUrls: ['./entrance-type.component.scss'],
    imports: [MatCard, MatCardContent, LogoComponent, MatCardActions, MatButton]
})
export class EntranceTypeComponent implements OnInit {

  constructor(public router: Router) { }

  ngOnInit(): void {
  }

}
