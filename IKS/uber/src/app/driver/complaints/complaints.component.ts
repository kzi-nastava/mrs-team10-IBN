import { Component, Inject } from '@angular/core';
import { ComplaintDisplay } from '../../service/ride-history.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-complaints',
  imports: [],
  templateUrl: './complaints.component.html',
  styleUrl: './complaints.component.css',
})
export class ComplaintsComponent {
  @Inject(MAT_DIALOG_DATA) public data: {complaints: ComplaintDisplay[]} = {complaints:[]}
}
