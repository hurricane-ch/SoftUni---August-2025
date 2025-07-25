import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MatTableDataSource, MatTable, MatColumnDef, MatHeaderCellDef, MatHeaderCell, MatCellDef, MatCell, MatHeaderRowDef, MatHeaderRow, MatRowDef, MatRow, MatNoDataRow } from "@angular/material/table";
import { LanguageInterface } from "../../../../shared/interfaces/language-interface";
import { LanguageService } from "../../services/language.service";
import { LanguageDialogComponent } from "../language-dialog/language-dialog.component";
import { NgIf } from "@angular/common";
import { MatCard, MatCardContent } from "@angular/material/card";
import { MatFormField, MatLabel, MatPrefix } from "@angular/material/form-field";
import { MatIcon } from "@angular/material/icon";
import { MatInput } from "@angular/material/input";
import { MatButton } from "@angular/material/button";

@Component({
    selector: "app-language-list",
    templateUrl: "./language-list.component.html",
    styleUrls: ["./language-list.component.scss"],
    imports: [
        NgIf,
        MatCard,
        MatCardContent,
        MatFormField,
        MatLabel,
        MatIcon,
        MatPrefix,
        MatInput,
        MatButton,
        MatTable,
        MatColumnDef,
        MatHeaderCellDef,
        MatHeaderCell,
        MatCellDef,
        MatCell,
        MatHeaderRowDef,
        MatHeaderRow,
        MatRowDef,
        MatRow,
        MatNoDataRow]
})
export class LanguageListComponent implements OnInit {
  languages: LanguageInterface[];

  displayedColumns: string[] = [
    "enabled",
    "languageId",
    "name",
    "locale",
    "main",
    "description",
    "*",
  ];
  dataSource = new MatTableDataSource();

  constructor(
    public dialog: MatDialog,
    private languageService: LanguageService,
    private _snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.languageService.getLanguages().subscribe((languages) => {
      this.dataSource.data = languages;
      this.languages = languages;
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  editLanguage(language: LanguageInterface) {
    const dialogRef = this.dialog.open(LanguageDialogComponent, {
      width: "400px",
      data: { isAdd: false, language },
    });

    dialogRef.afterClosed().subscribe((language: LanguageInterface) => {
      if (language) {
        this.languageService
          .updateLanguage(language.languageId, language)
          .subscribe({
            next: () => {
              this.initialize();
              this._snackBar.open("Language updated successfully", "", {
                duration: 1000,
              });
            },
            error: (err) => {
              this._snackBar.open(
                `Error while updating the language: ${err.message}`,
                "Close"
              );
            },
          });
      }
    });
  }

  addLanguage() {
    const dialogRef = this.dialog.open(LanguageDialogComponent, {
      width: "400px",
      data: { isAdd: true },
    });

    dialogRef.afterClosed().subscribe((language: LanguageInterface) => {
      if (language) {
        this.languageService.addLanguage(language).subscribe({
          next: () => {
            this.initialize();
            this._snackBar.open("Language added successfully", "", {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while adding the language: ${err.message}`,
              "Close"
            );
          },
        });
      }
    });
  }
}
