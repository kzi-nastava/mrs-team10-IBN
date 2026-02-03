import { Component, Inject } from '@angular/core';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Complaint } from '../../model/complaint.model';
import { MatDialog } from '@angular/material/dialog';
import { SimpleMessageDialogComponent } from '../../layout/simple-message-dialog/simple-message-dialog.component';
import { ComplaintService } from '../../service/complaint.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-complaint-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, FormsModule],
  templateUrl: './complaint-dialog.component.html',
  styleUrls: ['./complaint-dialog.component.css'],
})
export class ComplaintDialogComponent {
  public complaint: Complaint = {
    id: 0,
    rideId: 0,
    content: '',
  };

  constructor(
    private dialogRef: MatDialogRef<ComplaintDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { rideId: number },
    private dialog: MatDialog,
    private complaintService: ComplaintService,
  ) {
    this.complaint.rideId = data.rideId;
  }

  close() {
    this.dialogRef.close();
  }

  submit() {
    this.dialogRef.close(true);
  }

  postComplaint() {
    this.complaintService.postComplaint(this.complaint).subscribe({
      next: () => {
        this.dialog.open(SimpleMessageDialogComponent, {
          width: '300px',
          data: { message: 'Your review is submitted.' },
        });
      },
      error: (err) => {
        console.error('Error posting review', err);
      },
    });
  }
}
