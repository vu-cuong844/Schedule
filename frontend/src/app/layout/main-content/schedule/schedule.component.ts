import { Component, OnInit } from '@angular/core';
import { DataService } from '../../../serviecs/data.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-schedule',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.css']
})
export class ScheduleComponent implements OnInit {
  jsonData: any[] = [];

  constructor(private dataService: DataService) {}

  ngOnInit() {
    this.dataService.jsonData$.subscribe(data => {
      this.jsonData = data;
      console.log('Schedule Dữ liệu nhận được từ DataService:', this.jsonData.length);
    });
  }

  sendDataToBackend() {
    this.dataService.sendScheduleData(this.jsonData).subscribe(
      (response) => {
        console.log('Gửi dữ liệu thành công:', response);
      },
      (error) => {
        console.error('Lỗi khi gửi dữ liệu:', error);
      }
    );
  }

}
