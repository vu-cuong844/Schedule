import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private jsonDataSource = new BehaviorSubject<any[]>([]);
  jsonData$ = this.jsonDataSource.asObservable();
  
  constructor(private http: HttpClient) {}

  setJsonData(data: any[]) {
    this.jsonDataSource.next(data);
  }
  
  sendScheduleData(data: any[]): Observable<any> {
    const apiUrl = 'api/schedule';
    
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    
    return this.http.post(apiUrl, data, { headers });
  }
}