import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = 'http://localhost:8080/api/schedule/schedule-room'; // Thay thế bằng URL thực tế

  constructor(private http: HttpClient) { }

  sendData(payload: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json',
      // 'Authorization': 'Bearer your-token', // Thay thế bằng token thực tế nếu cần
    });

    return this.http.post(this.apiUrl, payload, { headers });
  }
}
