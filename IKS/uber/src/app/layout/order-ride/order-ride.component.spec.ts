import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderRideComponent } from './order-ride.component';

describe('OrderRideComponent', () => {
  let component: OrderRideComponent;
  let fixture: ComponentFixture<OrderRideComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderRideComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderRideComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
