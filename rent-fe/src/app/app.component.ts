import { Component } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { RouterOutlet } from "@angular/router";
import { LoaderComponent } from "./loader/loader.component";

@Component({
    selector: "app-root",
    templateUrl: "./app.component.html",
    styleUrls: ["./app.component.scss"],
    imports: [RouterOutlet, LoaderComponent]
})
export class AppComponent {
  constructor(public translate: TranslateService) {
    this.translate.setDefaultLang(localStorage.getItem("lang") || "bg");
  }
}
