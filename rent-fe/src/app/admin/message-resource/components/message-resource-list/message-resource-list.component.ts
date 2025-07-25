import { Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatSnackBar } from "@angular/material/snack-bar";
import { MatTableDataSource, MatTable, MatColumnDef, MatHeaderCellDef, MatHeaderCell, MatCellDef, MatCell, MatHeaderRowDef, MatHeaderRow, MatRowDef, MatRow, MatNoDataRow } from "@angular/material/table";
import { MessageResourceInterface } from "../../../../shared/interfaces/message-resource-interface";
import { MessageResourceService } from "../../services/message-resource.service";
import { MessageResourceDialogComponent } from "../message-resource-dialog/message-resource-dialog.component";
import { NgIf } from "@angular/common";
import { MatCard, MatCardContent } from "@angular/material/card";
import { MatFormField, MatLabel, MatPrefix } from "@angular/material/form-field";
import { MatIcon } from "@angular/material/icon";
import { MatInput } from "@angular/material/input";
import { MatButton } from "@angular/material/button";

@Component({
    selector: "app-message-resource-list",
    templateUrl: "./message-resource-list.component.html",
    styleUrls: ["./message-resource-list.component.scss"],
    imports: [NgIf, MatCard, MatCardContent, MatFormField, MatLabel, MatIcon, MatPrefix, MatInput, MatTable, MatColumnDef, MatHeaderCellDef, MatHeaderCell, MatCellDef, MatCell, MatButton, MatHeaderRowDef, MatHeaderRow, MatRowDef, MatRow, MatNoDataRow, MatPaginator]
})
export class MessageResourceListComponent implements OnInit {
  messages: MessageResourceInterface[];
  @ViewChild(MatPaginator) paginator: MatPaginator;
  pageSize: number = 10;

  displayedColumns: string[] = ["code", "languageId", "message", "*"];
  dataSource = new MatTableDataSource();

  constructor(
    private messageResourceService: MessageResourceService,
    public dialog: MatDialog,
    private _snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.messageResourceService
      .getMessages()
      .subscribe((messages: MessageResourceInterface[]) => {
        this.dataSource.data = messages;
        this.messages = messages;
      });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  editMessage(message: MessageResourceInterface) {
    this.messageResourceService
      .getMessage(message.code)
      .subscribe((messages: MessageResourceInterface[]) => {
        const dialogRef = this.dialog.open(MessageResourceDialogComponent, {
          width: "400px",
          data: { messages },
        });

        dialogRef
          .afterClosed()
          .subscribe((messages: MessageResourceInterface[]) => {
            if (messages) {
              this.messageResourceService.updateMessages(messages).subscribe({
                next: () => {
                  this.initialize();
                  this._snackBar.open("Message updated successfully", "", {
                    duration: 1000,
                  });
                },
                error: (err) => {
                  this._snackBar.open(
                    `Error while updating the message: ${err.message}`,
                    "Close"
                  );
                },
              });
            }
          });
      });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }
}
