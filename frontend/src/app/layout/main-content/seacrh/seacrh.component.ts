import { Component, EventEmitter, Output } from '@angular/core';
import * as XLSX from 'xlsx';
import { DataService } from '../../../serviecs/data.service';

@Component({
  selector: 'app-seacrh',
  imports: [],
  templateUrl: './seacrh.component.html',
  styleUrl: './seacrh.component.css'
})
export class SeacrhComponent {

  constructor(private dataService: DataService) {}

  handleFileInput(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();

      reader.onload = (e: any) => {
        const data = new Uint8Array(e.target.result);
        const workbook = XLSX.read(data, { type: 'array' });
        const sheetName = workbook.SheetNames[0];
        const sheet = workbook.Sheets[sheetName];
        const jsonData = XLSX.utils.sheet_to_json(sheet);

        this.dataService.setJsonData(jsonData); // 📤 Truyền dữ liệu sang service
      };

      reader.readAsArrayBuffer(file);
    }
  }

}
