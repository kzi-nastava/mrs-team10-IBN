import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiclePriceComponent } from './vehicle-price.component';

describe('VehiclePriceComponent', () => {
  let component: VehiclePriceComponent;
  let fixture: ComponentFixture<VehiclePriceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehiclePriceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehiclePriceComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
