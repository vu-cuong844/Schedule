import { Component } from '@angular/core';
import { DataService } from '../../services/data.service';

@Component({
  selector: 'app-menu',
  imports: [],
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent {

  constructor (private dataService: DataService) { }

  onSelectFile(index: number): void {
    this.dataService.setIndexFileRender(index);
  }
}
