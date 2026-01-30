import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ReviewDisplay } from '../../service/ride-history.service';

@Component({
  selector: 'app-reviews',
  imports: [],
  templateUrl: './reviews.component.html',
  styleUrl: './reviews.component.css',
})
export class ReviewsComponent {
  @Inject(MAT_DIALOG_DATA) public data: {reviews: ReviewDisplay[]} = {reviews:[]}
}
