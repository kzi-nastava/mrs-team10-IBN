import { Component, inject } from '@angular/core';
import { ComplaintDisplay } from '../../service/ride-history.service';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-complaints',
  imports: [MatDialogModule],
  templateUrl: './complaints.component.html',
  styleUrl: './complaints.component.css',
})
export class ComplaintsComponent {
  public data = inject<{complaints: ComplaintDisplay[]}>(MAT_DIALOG_DATA);
}
