import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SimpleMessageDialogComponent } from './simple-message-dialog.component';

describe('SimpleMessageDialogComponent', () => {
  let component: SimpleMessageDialogComponent;
  let fixture: ComponentFixture<SimpleMessageDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SimpleMessageDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SimpleMessageDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
