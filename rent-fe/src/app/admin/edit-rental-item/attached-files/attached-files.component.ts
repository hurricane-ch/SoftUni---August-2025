import { Component, ElementRef, OnInit, ViewChild } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { ActivatedRoute } from "@angular/router";
import { CommonModule } from "@angular/common";
import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatTableModule } from "@angular/material/table";
import { MatSelectModule } from "@angular/material/select";
import { MatCardModule } from "@angular/material/card";
import { ConfirmationDialogComponent } from "src/app/shared/confirmation-dialog/confirmation-dialog.component";
import { IBreadCrumbItems } from "src/app/shared/interfaces/breadcrumbs-interface";
import { RentalItemEditService } from "../services/edit-rental-item.service";
import { ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from "@angular/material/checkbox";

@Component({
    selector: "app-attached-files",
    templateUrl: "./attached-files.component.html",
    styleUrls: ["./attached-files.component.scss"],
    standalone: true,
    imports: [
        CommonModule,
        MatButtonModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatTableModule,
        MatSelectModule,
        MatCardModule,
        ReactiveFormsModule,
        MatCheckboxModule
    ]
})
export class AttachedFilesComponent implements OnInit {
  @ViewChild("fileInput") fileInput!: ElementRef;
  person = sessionStorage.getItem('person');
  constructor(
    private fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly editRentalItemService: RentalItemEditService,
    public dialog: MatDialog
  ) {}

  breadcrumbItems: IBreadCrumbItems[] = [];
  attachForm: FormGroup;
  errorMessage!: string;
  groupEventsListSource = new MatTableDataSource<any>();
  personId!: string;
  reantalItemId!: string;
  displayedColumns: string[] = ["id", "description", "fileName", "setMain", "download", "action"];

  ngOnInit(): void {
    const urlSegments = this.route.snapshot.url;
    const lastSegment = urlSegments[urlSegments.length - 1].path;
  
    this.reantalItemId = lastSegment ? String(Number(lastSegment)) : "";
  
    this.getAttachedFiles();
  
    this.attachForm = this.fb.group({
      id: [""],
      document: ["", Validators.required],
      description: ["", Validators.required],
      fileName: ["", Validators.required],
      main: [false]
    });
  }

  setMain(fileId: number) {
    const rentalItemIdNum = Number(this.reantalItemId);
    if (!rentalItemIdNum) return;
  
    this.editRentalItemService.setMain(rentalItemIdNum, fileId, true).subscribe(() => {
      this.getAttachedFiles();
    });
  }
  
  removeMain(fileId: number) {
    const rentalItemIdNum = Number(this.reantalItemId);
    if (!rentalItemIdNum) return;
  
    this.editRentalItemService.setMain(rentalItemIdNum, fileId, false).subscribe(() => {
      this.getAttachedFiles();
    });
  }

  clearSearchFields() {
    this.attachForm.patchValue({
      id: "",
      document: "",
      description: "",
      fileName: "",
      main: null
    });

    if (this.fileInput && this.fileInput.nativeElement) {
      this.fileInput.nativeElement.value = "";
    }
  }

  onSubmit() {
    if (this.attachForm.valid) {
      const formData = new FormData();

      const documentFile = this.attachForm.get("document")?.value;
      if (documentFile) {
        formData.append("file", documentFile);
      }

      const description = this.attachForm.get("description")?.value;
      if (description) {
        formData.append("description", description);
      }

      const main = this.attachForm.get("main")?.value;
      formData.append("main", main ? "true" : "false");

      this.editRentalItemService
        .uploadFile(this.reantalItemId, formData)
        .subscribe((res) => {
          if (res) {
            this.getAttachedFiles();
            this.attachForm.reset();
          }
        });
    }
  }

  getAttachedFiles() {
    this.editRentalItemService.getFiles(this.reantalItemId).subscribe((res) => {
      this.groupEventsListSource.data = res;
    });
  }

  onSelectFile(event: Event) {
    const files = (event.target as HTMLInputElement).files;

    if (files && files.length) {
      this.attachForm.get("document")?.patchValue(files[0]);
      this.attachForm.get("fileName")?.patchValue(files ? files[0].name : "");
    }
  }

  delete(id: string): void {
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      data: {
        header: "Изтриване на прикачен файл",
        body: "Сигурни ли сте че искате да извършите действието?",
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.editRentalItemService
          .deleteFile(this.reantalItemId, id)
          .subscribe((res) => {
            if (res) {
              this.getAttachedFiles();
            }
          });
      }
    });
  }

  downloadFile(file: any) {
    this.editRentalItemService.downloadFile(file.id).subscribe((res) => {
        const binaryData = atob(res.resource);
        const arrayBuffer = new Uint8Array(binaryData.length);
        for (let i = 0; i < binaryData.length; i++) {
          arrayBuffer[i] = binaryData.charCodeAt(i);
        }
        const blob = new Blob([arrayBuffer], { type: res.contentType });
        const url = window.URL.createObjectURL(blob);
        const anchor = document.createElement('a');
        anchor.href = url;
        anchor.download = res.fileName;
        document.body.appendChild(anchor);
        anchor.click();

        document.body.removeChild(anchor);
        window.URL.revokeObjectURL(url);
    });
  }
}