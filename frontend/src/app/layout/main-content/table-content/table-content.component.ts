import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DataService } from '../../../serviecs/data.service';

@Component({
  selector: 'app-table-content',
  imports: [CommonModule],
  templateUrl: './table-content.component.html',
  styleUrl: './table-content.component.css'
})
export class TableContentComponent implements OnInit {
  jsonData: any[] = [];
  keys: string[] = [];

  constructor(private dataService: DataService) {}

  ngOnInit(): void {
    this.dataService.jsonData$.subscribe(data => {
      this.jsonData = data;
      if (data.length > 0) {
        this.keys = Object.keys(data[0]);
      }
    });
  }
}
