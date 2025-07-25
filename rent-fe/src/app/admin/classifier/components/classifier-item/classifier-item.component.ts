import { Component, OnInit } from "@angular/core";
import { Location, NgIf, NgFor } from "@angular/common";
import { Classifier } from "../../../../shared/interfaces/classifier-interface";
import { ActivatedRoute, Params, RouterLink } from "@angular/router";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MatDialog } from "@angular/material/dialog";
import { ClassifierService } from "../../../../shared/services/classifier.service";
import { ClassifierDialogComponent } from "../classifier-dialog/classifier-dialog.component";
import { MatIconButton, MatButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatCard, MatCardContent, MatCardActions } from "@angular/material/card";
import { MatDivider } from "@angular/material/divider";

@Component({
    selector: "app-classifier-item",
    templateUrl: "./classifier-item.component.html",
    styleUrls: ["./classifier-item.component.scss"],
    imports: [NgIf, MatIconButton, MatIcon, MatCard, MatCardContent, MatDivider, MatCardActions, MatButton, NgFor, RouterLink]
})
export class ClassifierItemComponent implements OnInit {
  classifier: Classifier;

  constructor(
    private classifierService: ClassifierService,
    private activatedRoute: ActivatedRoute,
    private location: Location,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize() {
    this.activatedRoute.params.subscribe((params: Params) => {
      const id = params["id"];
      this.classifierService
        .getClassifier(id)
        .subscribe((classifier: Classifier) => {
          this.classifier = classifier;
        });
    });
  }

  editClassifier(classifier: Classifier) {
    const dialogRef = this.dialog.open(ClassifierDialogComponent, {
      width: "400px",
      data: { isAdd: false, classifier },
    });
    dialogRef.afterClosed().subscribe((classifier: Classifier) => {
      if (classifier) {
        console.log(classifier);
        this.classifierService
          .updateClassifier(classifier.code, classifier)
          .subscribe({
            next: () => {
              this.initialize();
              this._snackBar.open("Classifier updated successfully", "", {
                duration: 1000,
              });
            },
            error: (err) => {
              this._snackBar.open(
                `Error while updating the classifier: ${err.message}`,
                "Close"
              );
            },
          });
      }
    });
  }

  addSubClassifier() {
    const dialogRef = this.dialog.open(ClassifierDialogComponent, {
      width: "400px",
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((classifier: Classifier) => {
      if (classifier) {
        console.log(classifier);
        this.classifierService
          .addSubClassifier(this.classifier.code, classifier)
          .subscribe({
            next: () => {
              this._snackBar.open("Subclassifier added successfully", "", {
                duration: 1000,
              });
              this.initialize();
            },
            error: (err) => {
              this._snackBar.open(
                `Error while adding the subclassifier type: ${err.message}`,
                "Close"
              );
            },
          });
      }
    });
  }

  back(): void {
    this.location.back();
  }
}
