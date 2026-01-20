import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Complaint } from '../model/complaint.model';
@Injectable({
  providedIn: 'root'
})
export class ComplaintService {
  constructor(private http: HttpClient){

  }

  postComplaint(complaint: Complaint){
    console.log(complaint)
    return this.http.post(`${environment.apiHost}/reports`, complaint)
  }
}
