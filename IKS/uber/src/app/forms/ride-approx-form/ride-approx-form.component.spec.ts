import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideApproxFormComponent } from './ride-approx-form.component';

describe('RideApproxFormComponent', () => {
  let component: RideApproxFormComponent;
  let fixture: ComponentFixture<RideApproxFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideApproxFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideApproxFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
