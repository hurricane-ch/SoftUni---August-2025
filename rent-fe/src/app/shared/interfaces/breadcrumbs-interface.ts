import { Params } from "@angular/router";

export interface IBreadCrumbItems {
  label?: string;
  routerLink?: string;
  queryParams?: Params;
  onClick?: () => any;
}
