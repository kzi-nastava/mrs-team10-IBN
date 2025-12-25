import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComplaintDialogComponent } from './complaint-dialog.component';

describe('ComplaintDialogComponent', () => {
  let component: ComplaintDialogComponent;
  let fixture: ComponentFixture<ComplaintDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComplaintDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComplaintDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
