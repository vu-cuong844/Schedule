import { Component } from '@angular/core';
import { HeaderContentComponent } from './header-content/header-content.component';
import { TableContentComponent } from './table-content/table-content.component';
import { SeacrhComponent } from './seacrh/seacrh.component';
import { ScheduleComponent } from './schedule/schedule.component';

@Component({
  selector: 'app-main-content',
  standalone: true,
  imports: [HeaderContentComponent, TableContentComponent, SeacrhComponent, ScheduleComponent ],
  templateUrl: './main-content.component.html',
  styleUrl: './main-content.component.css'
})
export class MainContentComponent {

}
