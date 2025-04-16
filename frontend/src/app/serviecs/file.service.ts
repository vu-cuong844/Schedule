import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private excelDataSubject = new BehaviorSubject<any[]>([]);
  excelData$: Observable<any[]> = this.excelDataSubject.asObservable();

  constructor(private http: HttpClient) {}

  // Lưu dữ liệu từ file Excel
  setExcelData(data: any[]): void {
    this.excelDataSubject.next(data);
  }

  // Gửi dữ liệu đến Backend
  sendToBackend(data: any[]): Observable<any> {
    // Thêm headers nếu backend yêu cầu, ví dụ Content-Type
    const headers = { 'Content-Type': 'application/json' };

    // Đảm bảo rằng endpoint backend là chính xác
    return this.http.post("http://your-backend-api-url.com/endpoint", data, { headers });
  }
}
