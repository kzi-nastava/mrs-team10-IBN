import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrackingMapComponent } from './tracking-map.component';

describe('TrackingMapComponent', () => {
  let component: TrackingMapComponent;
  let fixture: ComponentFixture<TrackingMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackingMapComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrackingMapComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
