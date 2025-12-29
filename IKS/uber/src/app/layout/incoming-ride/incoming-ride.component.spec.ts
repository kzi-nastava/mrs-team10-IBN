import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IncomingRideComponent } from './incoming-ride.component';

describe('IncomingRideComponent', () => {
  let component: IncomingRideComponent;
  let fixture: ComponentFixture<IncomingRideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IncomingRideComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IncomingRideComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
