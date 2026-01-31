import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { ReviewDisplay } from '../../service/ride-history.service';
import { MatIconModule } from "@angular/material/icon";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reviews',
  imports: [MatDialogModule, MatIconModule, CommonModule],
  templateUrl: './reviews.component.html',
  styleUrl: './reviews.component.css',
})
export class ReviewsComponent {
  public data = inject<{reviews: ReviewDisplay[]}>(MAT_DIALOG_DATA);
}
