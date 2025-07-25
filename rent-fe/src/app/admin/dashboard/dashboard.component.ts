import { Component, OnInit } from "@angular/core";
import { Title } from "@angular/platform-browser";
import { TranslateService } from "@ngx-translate/core";
import { BuildInfoInterface } from "../../shared/interfaces/build-info-interface";
import { DashboardService } from "./services/dashboard.service";
import build from "../../../build";
import { Router, RouterLink } from "@angular/router";
import { MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatCard, MatCardContent } from "@angular/material/card";
import { NgIf, DatePipe } from "@angular/common";

@Component({
    selector: "app-dashboard",
    templateUrl: "./dashboard.component.html",
    styleUrls: ["./dashboard.component.scss"],
    imports: [RouterLink, MatIconButton, MatIcon, MatCard, NgIf, MatCardContent, DatePipe]
})
export class DashboardComponent implements OnInit {
  backendBuildInfo: BuildInfoInterface;
  frontendBuildInfo: typeof build;
  errorBuildInfo: string = "";

  constructor(
    private dashboardService: DashboardService,
    private title: Title,
    private translate: TranslateService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.frontendBuildInfo = build;
    this.translate.get("dashboard.title").subscribe((title) => {
      this.title.setTitle(title);
    });
    this.dashboardService.getBuildInfo().subscribe({
      next: (buildInfo: BuildInfoInterface) => {
        this.backendBuildInfo = buildInfo;
      },
      error: (err) => {
        this.errorBuildInfo = err;
      },
    });
  }

  redirectToPhysicalPersonDashboard() {
    this.router.navigate(["app/physical-person"]);
  }
}
