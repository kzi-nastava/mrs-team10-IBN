import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-block-user-dialog',
  standalone: true,
  imports: [MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, FormsModule],
  template: `
    <h2 mat-dialog-title>Block {{ data.userType }}</h2>
    <mat-dialog-content>
      <p>Are you sure you want to block {{ data.name }} {{ data.lastname }}?</p>
      <mat-form-field appearance="outline" style="width: 100%;">
        <mat-label>Blocking Reason</mat-label>
        <textarea
          matInput
          [(ngModel)]="reason"
          placeholder="Enter reason for blocking..."
          rows="4"
          required
        >
        </textarea>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancel</button>
      <button mat-raised-button color="warn" [disabled]="!reason" (click)="onConfirm()">
        Block User
      </button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      mat-dialog-content {
        min-width: 400px;
        padding: 20px 24px;
      }
    `,
  ],
})
export class BlockUserDialogComponent {
  reason: string = '';

  constructor(
    public dialogRef: MatDialogRef<BlockUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {}

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    this.dialogRef.close(this.reason);
  }
}
