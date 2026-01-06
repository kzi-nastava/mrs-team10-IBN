import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoritesPopupComponent } from './favorites-popup.component';

describe('FavoritesPopupComponentComponent', () => {
  let component: FavoritesPopupComponent;
  let fixture: ComponentFixture<FavoritesPopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoritesPopupComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(FavoritesPopupComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
