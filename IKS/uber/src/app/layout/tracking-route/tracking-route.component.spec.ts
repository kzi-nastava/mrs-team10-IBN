import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrackingRouteComponent } from './tracking-route.component';

describe('TrackingRouteComponent', () => {
  let component: TrackingRouteComponent;
  let fixture: ComponentFixture<TrackingRouteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrackingRouteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TrackingRouteComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
