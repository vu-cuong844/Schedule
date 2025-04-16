import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeacrhComponent } from './seacrh.component';

describe('SeacrhComponent', () => {
  let component: SeacrhComponent;
  let fixture: ComponentFixture<SeacrhComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SeacrhComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SeacrhComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
