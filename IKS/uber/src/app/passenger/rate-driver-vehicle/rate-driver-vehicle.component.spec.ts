import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RateDriverVehicleComponent } from './rate-driver-vehicle.component';
import { ReviewService } from '../../service/review.service';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { RouterTestingModule } from '@angular/router/testing';
import { SimpleMessageDialogComponent } from '../../layout/simple-message-dialog/simple-message-dialog.component';

describe('RateDriverVehicleComponent', () => {
  let component: RateDriverVehicleComponent;
  let fixture: ComponentFixture<RateDriverVehicleComponent>;
  let mockReviewService: jasmine.SpyObj<ReviewService>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<RateDriverVehicleComponent>>;
  let mockDialog: jasmine.SpyObj<MatDialog>;
  let mockActivatedRoute: any;

  beforeEach(async () => {
    mockReviewService = jasmine.createSpyObj('ReviewService', ['postReview']);
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);

    mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: jasmine.createSpy('get').and.returnValue('123'),
        },
      },
    };

    await TestBed.configureTestingModule({
      imports: [
        RateDriverVehicleComponent,
        CommonModule,
        FormsModule,
        MatIconModule,
        RouterTestingModule,
      ],
      providers: [
        { provide: ReviewService, useValue: mockReviewService },
        { provide: MatDialog, useValue: mockDialog },
        { provide: MAT_DIALOG_DATA, useValue: null },
        { provide: MatDialogRef, useValue: mockDialogRef },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RateDriverVehicleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Component Initialization', () => {
    it('should initialize with empty review object', () => {
      expect(component.review.id).toBe(0);
    });

    it('should initialize hover states to 0', () => {
      expect(component.driverHover).toBe(0);
      expect(component.vehicleHover).toBe(0);
    });

    it('should initialize stars array with values 1-5', () => {
      expect(component.stars).toEqual([1, 2, 3, 4, 5]);
    });

    it('should set rideId from dialog data if provided', async () => {
      const dialogData = { rideId: 999 };

      await TestBed.resetTestingModule();
      TestBed.configureTestingModule({
        imports: [
          RateDriverVehicleComponent,
          CommonModule,
          FormsModule,
          MatIconModule,
          RouterTestingModule,
        ],
        providers: [
          { provide: ReviewService, useValue: mockReviewService },
          { provide: MatDialog, useValue: mockDialog },
          { provide: MAT_DIALOG_DATA, useValue: dialogData },
          { provide: MatDialogRef, useValue: mockDialogRef },
          { provide: ActivatedRoute, useValue: mockActivatedRoute },
        ],
      });

      const dialogFixture = TestBed.createComponent(RateDriverVehicleComponent);
      const dialogComponent = dialogFixture.componentInstance;

      expect(dialogComponent.review.rideId).toBe(999);
    });

    it('should set rideId from route params if dialog data is null', () => {
      expect(component.review.rideId).toBe(123);
    });
  });

  describe('Driver Rating Functionality', () => {
    it('should set driver rating when rateDriver is called', () => {
      component.rateDriver(4);
      expect(component.review.driverRating).toBe(4);
    });

    it('should set driver rating to 1 star', () => {
      component.rateDriver(1);
      expect(component.review.driverRating).toBe(1);
    });

    it('should update driver hover state on each hover', () => {
      component.hoverDriver(2);
      expect(component.driverHover).toBe(2);

      component.hoverDriver(4);
      expect(component.driverHover).toBe(4);
    });

    it('should allow rating changes after initial rating', () => {
      component.rateDriver(3);
      expect(component.review.driverRating).toBe(3);

      component.rateDriver(5);
      expect(component.review.driverRating).toBe(5);
    });
  });

  describe('Vehicle Rating Functionality', () => {
    it('should set vehicle rating when rateVehicle is called', () => {
      component.rateVehicle(4);
      expect(component.review.vehicleRating).toBe(4);
    });

    it('should set vehicle rating to 5 stars', () => {
      component.rateVehicle(5);
      expect(component.review.vehicleRating).toBe(5);
    });

    it('should update vehicle hover state on each hover', () => {
      component.hoverVehicle(2);
      expect(component.vehicleHover).toBe(2);

      component.hoverVehicle(4);
      expect(component.vehicleHover).toBe(4);
    });

    it('should allow rating changes after initial rating', () => {
      component.rateVehicle(2);
      expect(component.review.vehicleRating).toBe(2);

      component.rateVehicle(4);
      expect(component.review.vehicleRating).toBe(4);
    });
  });

  describe('Independent Rating Functionality', () => {
    it('should allow independent driver and vehicle ratings', () => {
      component.rateDriver(5);
      component.rateVehicle(3);

      expect(component.review.driverRating).toBe(5);
      expect(component.review.vehicleRating).toBe(3);
    });

    it('should allow independent hover states', () => {
      component.hoverDriver(5);
      component.hoverVehicle(2);

      expect(component.driverHover).toBe(5);
      expect(component.vehicleHover).toBe(2);
    });

    it('should update driver rating without affecting vehicle rating', () => {
      component.rateVehicle(4);
      component.rateDriver(2);

      expect(component.review.driverRating).toBe(2);
      expect(component.review.vehicleRating).toBe(4);
    });
  });

  describe('Review Submission - Success', () => {
    beforeEach(() => {
      component.review.driverRating = 5;
      component.review.vehicleRating = 4;
      component.review.rideId = 123;
    });

    it('should call reviewService.postReview with review data', () => {
      mockReviewService.postReview.and.returnValue(of({}));

      component.postReview();

      expect(mockReviewService.postReview).toHaveBeenCalledWith({
        id: 0,
        rideId: 123,
        driverRating: 5,
        vehicleRating: 4,
      });
    });

    it('should open success dialog on successful review submission', () => {
      mockReviewService.postReview.and.returnValue(of({}));

      component.postReview();

      expect(mockDialog.open).toHaveBeenCalledWith(SimpleMessageDialogComponent, {
        width: '300px',
        data: { message: 'Your review is submitted.' },
      });
    });

    it('should submit review with only driver rating', () => {
      mockReviewService.postReview.and.returnValue(of({}));
      component.review.vehicleRating = undefined;

      component.postReview();

      expect(mockReviewService.postReview).toHaveBeenCalledWith({
        id: 0,
        rideId: 123,
        driverRating: 5,
        vehicleRating: undefined,
      });
    });

    it('should submit review with only vehicle rating', () => {
      mockReviewService.postReview.and.returnValue(of({}));
      component.review.driverRating = undefined;

      component.postReview();

      expect(mockReviewService.postReview).toHaveBeenCalledWith({
        id: 0,
        rideId: 123,
        driverRating: undefined,
        vehicleRating: 4,
      });
    });
  });

  describe('Review Submission - Error Handling', () => {
    beforeEach(() => {
      component.review.driverRating = 3;
      component.review.vehicleRating = 3;
      component.review.rideId = 123;
    });

    it('should handle review submission error', () => {
      mockReviewService.postReview.and.returnValue(
        throwError(() => new Error('Submission failed')),
      );

      component.postReview();

      expect(mockDialog.open).toHaveBeenCalledWith(SimpleMessageDialogComponent, {
        width: '300px',
        data: { message: 'You cannot place review for this ride!' },
      });
    });

    it('should not prevent subsequent submission attempts after error', () => {
      mockReviewService.postReview.and.returnValue(throwError(() => new Error('Error')));

      component.postReview();

      mockReviewService.postReview.and.returnValue(of({}));

      component.postReview();

      expect(mockReviewService.postReview).toHaveBeenCalledTimes(2);
    });
  });

  describe('Review Data Validation', () => {
    it('should construct complete review object for submission', () => {
      component.review.rideId = 456;
      component.rateDriver(4);
      component.rateVehicle(5);

      mockReviewService.postReview.and.returnValue(of({}));

      component.postReview();

      const submittedData = mockReviewService.postReview.calls.mostRecent().args[0];
      expect(submittedData).toEqual({
        id: 0,
        rideId: 456,
        driverRating: 4,
        vehicleRating: 5,
      });
    });

    it('should allow submission with partial ratings', () => {
      component.rateDriver(5);

      mockReviewService.postReview.and.returnValue(of({}));

      component.postReview();

      expect(mockReviewService.postReview).toHaveBeenCalled();
    });
  });

  describe('Star Rating UI Interaction', () => {
    it('should have all 5 stars available for rating', () => {
      expect(component.stars.length).toBe(5);
      expect(component.stars[0]).toBe(1);
      expect(component.stars[4]).toBe(5);
    });

    it('should cycle through all star ratings for driver', () => {
      component.stars.forEach((starValue) => {
        component.rateDriver(starValue);
        expect(component.review.driverRating).toBe(starValue);
      });
    });

    it('should cycle through all star ratings for vehicle', () => {
      component.stars.forEach((starValue) => {
        component.rateVehicle(starValue);
        expect(component.review.vehicleRating).toBe(starValue);
      });
    });

    it('should cycle through all hover states for driver', () => {
      component.stars.forEach((starValue) => {
        component.hoverDriver(starValue);
        expect(component.driverHover).toBe(starValue);
      });
    });

    it('should cycle through all hover states for vehicle', () => {
      component.stars.forEach((starValue) => {
        component.hoverVehicle(starValue);
        expect(component.vehicleHover).toBe(starValue);
      });
    });
  });

  describe('Complete Review Workflow', () => {
    it('should complete full workflow: rate driver and vehicle, then submit', () => {
      mockReviewService.postReview.and.returnValue(of({}));

      component.hoverDriver(4);
      expect(component.driverHover).toBe(4);

      component.rateDriver(4);
      expect(component.review.driverRating).toBe(4);

      component.hoverVehicle(5);
      expect(component.vehicleHover).toBe(5);

      component.rateVehicle(5);
      expect(component.review.vehicleRating).toBe(5);

      component.postReview();

      expect(mockReviewService.postReview).toHaveBeenCalledWith({
        id: 0,
        rideId: 123,
        driverRating: 4,
        vehicleRating: 5,
      });

      expect(mockDialog.open).toHaveBeenCalledWith(SimpleMessageDialogComponent, {
        width: '300px',
        data: { message: 'Your review is submitted.' },
      });
    });

    it('should handle workflow with changed mind on rating', () => {
      mockReviewService.postReview.and.returnValue(of({}));

      component.rateDriver(3);
      expect(component.review.driverRating).toBe(3);

      component.rateDriver(5);
      expect(component.review.driverRating).toBe(5);

      component.rateVehicle(2);
      component.rateVehicle(4);
      expect(component.review.vehicleRating).toBe(4);

      component.postReview();

      const submittedData = mockReviewService.postReview.calls.mostRecent().args[0];
      expect(submittedData.driverRating).toBe(5);
      expect(submittedData.vehicleRating).toBe(4);
    });
  });

  describe('Edge Cases', () => {
    it('should handle zero rating submission', () => {
      mockReviewService.postReview.and.returnValue(of({}));

      component.postReview();

      expect(mockReviewService.postReview).toHaveBeenCalled();
    });
  });
});
