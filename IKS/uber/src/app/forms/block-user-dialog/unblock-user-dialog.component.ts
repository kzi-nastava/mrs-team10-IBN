import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-unblock-user-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule, CommonModule],
  template: `
    <h2 mat-dialog-title>Unblock {{ data.userType }}</h2>
    <mat-dialog-content>
      <p>
        Are you sure you want to unblock <strong>{{ data.name }} {{ data.lastname }}</strong
        >?
      </p>
      <div class="blocking-info" *ngIf="data.blockingReason">
        <p><strong>Previously blocked for:</strong></p>
        <p class="reason">{{ data.blockingReason }}</p>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">Cancel</button>
      <button mat-raised-button color="primary" (click)="onConfirm()">Unblock</button>
    </mat-dialog-actions>
  `,
  styles: [
    `
      mat-dialog-content {
        min-width: 400px;
        padding: 20px 24px;
      }

      .blocking-info {
        margin-top: 20px;
        padding: 15px;
        background-color: #f5f5f5;
        border-left: 4px solid #ff5252;
        border-radius: 4px;
      }

      .reason {
        font-style: italic;
        color: #666;
        margin-top: 8px;
        padding: 20px;
      }
    `,
  ],
})
export class UnblockUserDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<UnblockUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
  ) {}

  onCancel(): void {
    this.dialogRef.close(false);
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }
}
