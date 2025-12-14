import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideDialogComponent } from './ride-dialog.component';

describe('RideDialogComponent', () => {
  let component: RideDialogComponent;
  let fixture: ComponentFixture<RideDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
