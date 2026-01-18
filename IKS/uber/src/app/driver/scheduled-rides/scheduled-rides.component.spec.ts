import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ScheduledRidesComponent } from './scheduled-rides.component';

describe('ScheduledRidesComponent', () => {
  let component: ScheduledRidesComponent;
  let fixture: ComponentFixture<ScheduledRidesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ScheduledRidesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ScheduledRidesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
