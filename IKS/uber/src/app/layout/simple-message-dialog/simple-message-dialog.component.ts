import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-simple-message-dialog',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './simple-message-dialog.component.html',
  styleUrls: ['./simple-message-dialog.component.css'],
})
export class SimpleMessageDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<SimpleMessageDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { message: string }
  ) {}

  close() {
    this.dialogRef.close();
  }
}
