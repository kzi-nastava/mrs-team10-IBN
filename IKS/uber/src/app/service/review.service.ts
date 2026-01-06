import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Review } from '../model/review.model';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  constructor(private http: HttpClient){

  }

  postReview(review: Review){
    console.log(review)
    return this.http.post(`${environment.apiHost}/reviews`, review)
  }
}
