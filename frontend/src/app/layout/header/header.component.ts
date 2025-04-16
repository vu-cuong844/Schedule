import { Component } from '@angular/core';
import { LogoComponent } from './logo/logo.component';
import { LoginButtonComponent } from './login-button/login-button.component';

@Component({
  selector: 'app-header',
  imports: [LogoComponent, LoginButtonComponent],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

}
