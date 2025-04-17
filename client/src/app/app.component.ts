import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { MenuComponent } from './components/menu/menu.component';
import { FooterComponent } from './components/footer/footer.component';
import { HeaderContentComponent } from './components/header-content/header-content.component';
import { ToolBarComponent } from './components/tool-bar/tool-bar.component';
import { TableContentComponent } from './components/table-content/table-content.component';
import { SearchComponent } from './components/search/search.component';
import { ScheduleComponent } from './components/schedule/schedule.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, MenuComponent, FooterComponent, HeaderContentComponent, ToolBarComponent, TableContentComponent, SearchComponent, ScheduleComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'client';
}
