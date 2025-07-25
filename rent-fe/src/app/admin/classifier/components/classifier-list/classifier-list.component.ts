import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Classifier } from "../../../../shared/interfaces/classifier-interface";
import { ClassifierService } from "../../../../shared/services/classifier.service";
import { ClassifierDialogComponent } from "../classifier-dialog/classifier-dialog.component";
import { NgIf, NgFor } from "@angular/common";
import { MatCard, MatCardContent, MatCardActions } from "@angular/material/card";
import { RouterLink } from "@angular/router";
import { MatDivider } from "@angular/material/divider";
import { MatButton } from "@angular/material/button";

@Component({
    selector: "app-classifier-list",
    templateUrl: "./classifier-list.component.html",
    styleUrls: ["./classifier-list.component.scss"],
    imports: [NgIf, MatCard, MatCardContent, NgFor, RouterLink, MatDivider, MatCardActions, MatButton]
})
export class ClassifierListComponent implements OnInit {
  classifiers: Classifier[];

  constructor(
    public dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private classifiersService: ClassifierService
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.classifiersService
      .getClassifiers()
      .subscribe((classifiers: Classifier[]) => {
        this.classifiers = classifiers;
      });
  }

  addClassifier() {
    const dialogRef = this.dialog.open(ClassifierDialogComponent, {
      width: "400px",
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((classifier: Classifier) => {
      if (classifier) {
        console.log(classifier);
        this.classifiersService.addClassifier(classifier).subscribe({
          next: () => {
            this.initialize();
            this._snackBar.open("Classifier added successfully", "", {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while adding the classifier: ${err.message}`,
              "Close"
            );
          },
        });
      }
    });
  }
}
