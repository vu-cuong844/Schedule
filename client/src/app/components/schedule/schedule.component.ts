import { Component, OnInit } from '@angular/core';
import { DataService } from '../../services/data.service';
import * as XLSX from 'xlsx';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-schedule',
  imports: [],
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css'] // sửa lại 'styleUrls' thay vì 'styleUrl'
})
export class ScheduleComponent implements OnInit {  // implements OnInit để sử dụng lifecycle hook
  allFiles: File[] = [];
  classJson: any[] = [];
  roomJson: any[] = [];

  constructor(private dataService: DataService, private apiService: ApiService) { }

  ngOnInit(): void {
    this.loadFiles();  // Tự động tải file khi component được khởi tạo
  }

  // Tự động tải file từ dataService
  loadFiles(): void {
    this.dataService.files$.subscribe(files => {
      this.allFiles = files;
      console.log('Files loaded:', this.allFiles.length);
    });
  }

  async schedule(): Promise<void> {
    console.log("Start");
    await this.convertToJson();
    console.log("CLass1: ", this.classJson.length);
    console.log("Room1: ", this.roomJson.length);
    console.log("Start schedule...");

    const payload = {
      rooms: this.roomJson,
      classes: this.classJson
    };

    // console.log(this.classJson.at(0));
    // console.log(this.roomJson.at(4));

    this.apiService.sendData(payload).subscribe({
      next: (response) => console.log(response),
      error: (error) => console.error(error),
    });

  }

  // Chuyển file thành jsonData chỉ khi nhấn nút
  private async convertToJson(): Promise<void> {
    if (this.allFiles.length > 0) {
      this.classJson = await this.convertFileToJsonClass(this.allFiles[0]);
    }

    if (this.allFiles.length > 1) {
      this.roomJson = await this.convertFileToJsonRoom(this.allFiles[1]);
    }
  }

  // Chuyển file thành jsonData
  private convertFileToJsonClass(file: File): Promise<any[]> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        try {
          const binaryData = new Uint8Array(e.target.result);
          const workbook = XLSX.read(binaryData, { type: 'array' });
          const firstSheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[firstSheetName];
          const rawData = XLSX.utils.sheet_to_json(worksheet, { defval: '' });
  
          const mappedData = rawData.map((row: any) => ({
            id: row['Mã_lớp'] || '',
            maHP: row['Mã_HP'] || '',
            slMax: parseInt(row['SL_Max'], 10) || 0,
            thoiLuong: parseInt(row['Thời_lượng'], 10) || 0,
            type: row['Loại_lớp'] || ''
          }));
  
          resolve(mappedData);
        } catch (error) {
          reject(error);
        }
      };
      reader.onerror = (error) => reject(error);
      reader.readAsArrayBuffer(file);
    });
  }

  private convertFileToJsonRoom(file: File): Promise<any[]> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        try {
          const binaryData = new Uint8Array(e.target.result);
          const workbook = XLSX.read(binaryData, { type: 'array' });
          const firstSheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[firstSheetName];
          const rawData = XLSX.utils.sheet_to_json(worksheet, { defval: '' });
  
          const mappedData = rawData.map((row: any) => {
            const name = row['Phòng'] || '';
            const maHPsRaw = row['Mã_HP'] || '';
            const corrected = maHPsRaw.replace(/'/g, '"');
            let maHPs: string[] = [];
  
            try {
              maHPs = JSON.parse(corrected);
              if (!Array.isArray(maHPs)) {
                maHPs = [];
              }
            } catch {
              maHPs = [];
            }
  
            const slMax = parseInt(row['SL_Max'], 10) || 0;
            const type = row['Loại_lớp'] || '';
  
            return { name, maHPs, slMax, type };
          });
  
          resolve(mappedData);
        } catch (error) {
          reject(error);
        }
      };
      reader.onerror = (error) => reject(error);
      reader.readAsArrayBuffer(file);
    });
  }
  
  

}
