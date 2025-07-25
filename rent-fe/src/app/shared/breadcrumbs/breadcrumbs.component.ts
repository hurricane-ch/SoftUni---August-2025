import { Component, Input } from "@angular/core";
import { IBreadCrumbItems } from "../interfaces/breadcrumbs-interface";
import {NgClass} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";

@Component({
    selector: "app-breadcrumbs",
    templateUrl: "./breadcrumbs.component.html",
    imports: [
        NgClass,
        MatIcon,
        RouterLink
    ],
    styleUrl: "./breadcrumbs.component.scss"
})
export class BreadcrumbsComponent {
  @Input() items: IBreadCrumbItems[] = [];
}
