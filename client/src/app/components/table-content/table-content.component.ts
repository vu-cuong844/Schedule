import { Component, OnInit } from '@angular/core';
import { DataService } from '../../services/data.service';
import * as XLSX from 'xlsx';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-table-content',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './table-content.component.html',
  styleUrls: ['./table-content.component.css'],
})
export class TableContentComponent implements OnInit {
  tableData: any[][] = [];
  indexFileRender: number = 0;
  files: File[] = [];

  constructor(private dataService: DataService) {}

  ngOnInit(): void {
    // Đăng ký theo dõi file
    this.dataService.files$.subscribe((files) => {
      this.files = files;
      this.updateTable();
    });

    // Đăng ký theo dõi chỉ số file được chọn
    this.dataService.indexFileRender$.subscribe((index) => {
      this.indexFileRender = index;
      this.updateTable();
    });
  }

  updateTable(): void {
    const file = this.files[this.indexFileRender];
    if (file) {
      this.convertFileToTable(file);
    }
  }

  convertFileToTable(file: File): void {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      const binaryData = new Uint8Array(e.target.result);
      const workbook = XLSX.read(binaryData, { type: 'array' });
      const firstSheetName = workbook.SheetNames[0];
      const worksheet = workbook.Sheets[firstSheetName];
      const data: any[][] = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
      this.tableData = data;
    };

    reader.readAsArrayBuffer(file);
  }
}
