import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import 'zone.js';
import 'zone.js/testing';
import { RegisterDriverComponent } from './register-driver.component';
import { DriverService, DriverDTO } from '../../service/driver.service';
import { of, throwError } from 'rxjs';
import { CommonModule } from '@angular/common';
import { UserFormComponent } from '../../forms/user-form/user-form.component';
import { VehicleFormComponent } from '../../forms/vehicle-form/vehicle-form.component';
import { HttpErrorResponse } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { routes } from '../../app.routes';

describe('RegisterDriverComponent', () => {
  let component: RegisterDriverComponent;
  let fixture: ComponentFixture<RegisterDriverComponent>;
  let mockDriverService: jasmine.SpyObj<DriverService>;

  const mockDriverDTO: DriverDTO = {
    accountDTO: {
      email: 'test@example.com',
    },
    createUserDTO: {
      name: 'John',
      lastName: 'Doe',
      homeAddress: '123 Main St',
      phone: '1234567890',
      image: '',
    },
    vehicleDTO: {
      vehicleTypeDTO: { id: null, name: 'STANDARD', price: 0 },
      model: 'Toyota Camry',
      plate: 'ABC-123',
      seatNumber: 4,
      babySeat: false,
      petFriendly: false,
    },
    uptime: 0,
    blocked: false,
    reason: '',
  };

  beforeEach(async () => {
    mockDriverService = jasmine.createSpyObj('DriverService', ['registerDriver']);

    await TestBed.configureTestingModule({
      imports: [RegisterDriverComponent, CommonModule, UserFormComponent, VehicleFormComponent],
      providers: [{ provide: DriverService, useValue: mockDriverService }, provideRouter(routes)],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterDriverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Component Initialization', () => {
    it('should initialize with empty driver data', () => {
      expect(component.newDriverData).toEqual({
        firstName: '',
        lastName: '',
        address: '',
        phone: '',
        email: '',
        image: '',
      });
    });

    it('should initialize with no messages', () => {
      expect(component.successMessage).toBeNull();
      expect(component.errorMessage).toBeNull();
    });

    it('should initialize with isLoading set to false', () => {
      expect(component.isLoading).toBeFalsy();
    });
  });

  describe('Form Validation', () => {
    beforeEach(() => {
      const mockUserForm: any = {
        isFormValid: jasmine.createSpy('isFormValid'),
        formTouched: false,
        touchedFields: {
          firstName: false,
          lastName: false,
          address: false,
          phone: false,
          email: false,
        },
      };

      const mockVehicleForm: any = {
        isFormValid: jasmine.createSpy('isFormValid'),
        formTouched: false,
        touchedFields: {
          model: false,
          type: false,
          licensePlate: false,
          seatNumber: false,
        },
      };

      component.userForm = mockUserForm;
      component.vehicleForm = mockVehicleForm;
    });

    it('should not register driver if user form is invalid', () => {
      (component.userForm.isFormValid as jasmine.Spy).and.returnValue(false);
      (component.vehicleForm.isFormValid as jasmine.Spy).and.returnValue(true);

      component.registerDriver();

      expect(mockDriverService.registerDriver).not.toHaveBeenCalled();
      expect(component.errorMessage).toBe('Please fill in all required fields correctly.');
    });

    it('should mark user form fields as touched on submit', () => {
      (component.userForm.isFormValid as jasmine.Spy).and.returnValue(false);
      (component.vehicleForm.isFormValid as jasmine.Spy).and.returnValue(false);

      component.registerDriver();

      expect(component.userForm.formTouched).toBeTruthy();
      expect(component.userForm.touchedFields.firstName).toBeTruthy();
      expect(component.userForm.touchedFields.lastName).toBeTruthy();
      expect(component.userForm.touchedFields.address).toBeTruthy();
      expect(component.userForm.touchedFields.phone).toBeTruthy();
      expect(component.userForm.touchedFields.email).toBeTruthy();
    });

    it('should validate all required user fields', () => {
      component.newDriverData = {
        firstName: '',
        lastName: 'Doe',
        address: '123 Main St',
        phone: '1234567890',
        email: 'test@example.com',
        image: '',
      };

      (component.userForm.isFormValid as jasmine.Spy).and.returnValue(false);
      (component.vehicleForm.isFormValid as jasmine.Spy).and.returnValue(true);

      component.registerDriver();

      expect(component.errorMessage).toBe('Please fill in all required fields correctly.');
    });

    it('should validate email format', () => {
      component.newDriverData.email = 'invalid-email';

      (component.userForm.isFormValid as jasmine.Spy).and.returnValue(false);
      (component.vehicleForm.isFormValid as jasmine.Spy).and.returnValue(true);

      component.registerDriver();

      expect(component.errorMessage).toBe('Please fill in all required fields correctly.');
    });

    it('should validate phone number format', () => {
      component.newDriverData.phone = 'abc';

      (component.userForm.isFormValid as jasmine.Spy).and.returnValue(false);
      (component.vehicleForm.isFormValid as jasmine.Spy).and.returnValue(true);

      component.registerDriver();

      expect(component.errorMessage).toBe('Please fill in all required fields correctly.');
    });

    it('should validate required fields: firstName, lastName, address, phone, email', () => {
      const requiredFields = ['firstName', 'lastName', 'address', 'phone', 'email'];

      requiredFields.forEach((field) => {
        component.newDriverData = {
          firstName: 'John',
          lastName: 'Doe',
          address: '123 Main St',
          phone: '1234567890',
          email: 'test@example.com',
          image: '',
        };

        (component.newDriverData as any)[field] = '';
        (component.userForm.isFormValid as jasmine.Spy).and.returnValue(false);
        (component.vehicleForm.isFormValid as jasmine.Spy).and.returnValue(true);

        component.registerDriver();

        expect(component.errorMessage).toBe('Please fill in all required fields correctly.');
      });
    });
  });

  describe('Driver Registration Submission', () => {
    beforeEach(() => {
      component.newDriverData = {
        firstName: 'John',
        lastName: 'Doe',
        address: '123 Main St',
        phone: '1234567890',
        email: 'john.doe@example.com',
        image: '',
      };

      component.newVehicleData = {
        model: 'Toyota Camry',
        type: 'standard',
        plate: 'ABC-123',
        seatNumber: 4,
        babySeat: false,
        petFriendly: true,
      };

      const mockUserForm: any = {
        isFormValid: jasmine.createSpy('isFormValid').and.returnValue(true),
        formTouched: false,
        touchedFields: {
          firstName: false,
          lastName: false,
          address: false,
          phone: false,
          email: false,
        },
      };

      const mockVehicleForm: any = {
        isFormValid: jasmine.createSpy('isFormValid').and.returnValue(true),
        formTouched: false,
        touchedFields: {
          model: false,
          type: false,
          licensePlate: false,
          seatNumber: false,
        },
      };

      component.userForm = mockUserForm;
      component.vehicleForm = mockVehicleForm;
    });

    it('should call driverService.registerDriver with correct data when forms are valid', () => {
      mockDriverService.registerDriver.and.returnValue(of(mockDriverDTO));

      component.registerDriver();

      expect(mockDriverService.registerDriver).toHaveBeenCalledWith({
        accountDTO: {
          email: 'john.doe@example.com',
        },
        createUserDTO: {
          name: 'John',
          lastName: 'Doe',
          homeAddress: '123 Main St',
          phone: '1234567890',
          image: '',
        },
        vehicleDTO: {
          vehicleTypeDTO: { id: null, name: 'STANDARD', price: 0 },
          model: 'Toyota Camry',
          plate: 'ABC-123',
          seatNumber: 4,
          babySeat: false,
          petFriendly: true,
        },
      });
    });

    it('should show success message on successful registration', () => {
      mockDriverService.registerDriver.and.returnValue(of(mockDriverDTO));

      component.registerDriver();

      expect(component.successMessage).toBe('Driver registered successfully!');
      expect(component.errorMessage).toBeNull();
    });

    it('should reset forms after successful registration', fakeAsync(() => {
      mockDriverService.registerDriver.and.returnValue(of(mockDriverDTO));
      spyOn(component, 'resetForms');

      component.registerDriver();

      tick(2100);

      expect(component.resetForms).toHaveBeenCalled();
    }));

    it('should clear previous error messages on successful registration', () => {
      component.errorMessage = 'Previous error';
      mockDriverService.registerDriver.and.returnValue(of(mockDriverDTO));

      component.registerDriver();

      expect(component.errorMessage).toBeNull();
      expect(component.successMessage).toBe('Driver registered successfully!');
    });

    it('should send all required driver fields', () => {
      mockDriverService.registerDriver.and.returnValue(of(mockDriverDTO));

      component.registerDriver();

      const calledData = mockDriverService.registerDriver.calls.mostRecent().args[0];

      expect(calledData.accountDTO.email).toBe('john.doe@example.com');
      expect(calledData.createUserDTO.name).toBe('John');
      expect(calledData.createUserDTO.lastName).toBe('Doe');
      expect(calledData.createUserDTO.homeAddress).toBe('123 Main St');
      expect(calledData.createUserDTO.phone).toBe('1234567890');
    });
  });

  describe('Error Handling', () => {
    beforeEach(() => {
      component.newDriverData = {
        firstName: 'John',
        lastName: 'Doe',
        address: '123 Main St',
        phone: '1234567890',
        email: 'john.doe@example.com',
        image: '',
      };

      component.newVehicleData = {
        model: 'Toyota Camry',
        type: 'standard',
        plate: 'ABC-123',
        seatNumber: 4,
        babySeat: false,
        petFriendly: false,
      };

      const mockUserForm: any = {
        isFormValid: jasmine.createSpy('isFormValid').and.returnValue(true),
        formTouched: false,
        touchedFields: {},
      };
      const mockVehicleForm: any = {
        isFormValid: jasmine.createSpy('isFormValid').and.returnValue(true),
        formTouched: false,
        touchedFields: {},
      };

      component.userForm = mockUserForm;
      component.vehicleForm = mockVehicleForm;
    });

    it('should handle email already exists error (string error)', () => {
      const error = new HttpErrorResponse({
        error: 'Email already registered',
        status: 0,
        statusText: 'Unknown Error',
      });
      mockDriverService.registerDriver.and.returnValue(throwError(() => error));

      component.registerDriver();

      expect(component.errorMessage).toBe('Mail already exists.');
      expect(component.successMessage).toBeNull();
    });

    it('should handle bad request error (400)', () => {
      const error = new HttpErrorResponse({
        error: {},
        status: 400,
        statusText: 'Bad Request',
      });
      mockDriverService.registerDriver.and.returnValue(throwError(() => error));

      component.registerDriver();

      expect(component.errorMessage).toBe('Invalid data provided. Please check all fields.');
    });

    it('should handle server error (500)', () => {
      const error = new HttpErrorResponse({
        error: {},
        status: 500,
        statusText: 'Internal Server Error',
      });
      mockDriverService.registerDriver.and.returnValue(throwError(() => error));

      component.registerDriver();

      expect(component.errorMessage).toBe('Server error. Please try again later.');
    });

    it('should handle connection error (status 0 with empty object)', () => {
      const error = new HttpErrorResponse({
        error: {},
        status: 0,
        statusText: 'Unknown Error',
      });
      mockDriverService.registerDriver.and.returnValue(throwError(() => error));

      component.registerDriver();

      expect(component.errorMessage).toBe(
        'Cannot connect to server. Please check if backend is running.',
      );
    });

    it('should handle error with custom message', () => {
      const error = new HttpErrorResponse({
        error: { message: 'Custom error message' },
        status: 422,
        statusText: 'Unprocessable Entity',
      });
      mockDriverService.registerDriver.and.returnValue(throwError(() => error));

      component.registerDriver();

      expect(component.errorMessage).toBe('Custom error message');
    });
  });

  describe('Reset Forms', () => {
    beforeEach(() => {
      const mockUserForm: any = {
        isFormValid: jasmine.createSpy('isFormValid'),
        formTouched: true,
        touchedFields: {
          firstName: true,
          lastName: true,
          address: true,
          phone: true,
          email: true,
        },
      };

      const mockVehicleForm: any = {
        isFormValid: jasmine.createSpy('isFormValid'),
        formTouched: true,
        touchedFields: {
          model: true,
          type: true,
          licensePlate: true,
          seatNumber: true,
        },
      };

      component.userForm = mockUserForm;
      component.vehicleForm = mockVehicleForm;
    });

    it('should reset driver data to default values', () => {
      component.newDriverData = {
        firstName: 'John',
        lastName: 'Doe',
        address: '123 Main St',
        phone: '1234567890',
        email: 'john.doe@example.com',
        image: '',
      };

      component.resetForms();

      expect(component.newDriverData).toEqual({
        firstName: '',
        lastName: '',
        address: '',
        phone: '',
        email: '',
        image: '',
      });
    });

    it('should reset user form touched state', () => {
      component.resetForms();

      expect(component.userForm.formTouched).toBeFalsy();
      expect(component.userForm.touchedFields.firstName).toBeFalsy();
      expect(component.userForm.touchedFields.lastName).toBeFalsy();
      expect(component.userForm.touchedFields.address).toBeFalsy();
      expect(component.userForm.touchedFields.phone).toBeFalsy();
      expect(component.userForm.touchedFields.email).toBeFalsy();
    });
  });

  describe('Message Display', () => {
    it('should show messages and clear the opposite message', () => {
      component.errorMessage = 'Some error';
      component.showSuccess('Success!');
      expect(component.successMessage).toBe('Success!');
      expect(component.errorMessage).toBeNull();

      component.successMessage = 'Some success';
      component.showError('Error!');
      expect(component.errorMessage).toBe('Error!');
      expect(component.successMessage).toBeNull();
    });

    it('should auto-clear messages after 5 seconds', fakeAsync(() => {
      component.showSuccess('Success message');
      expect(component.successMessage).toBe('Success message');
      tick(5100);
      expect(component.successMessage).toBeNull();

      component.showError('Error message');
      expect(component.errorMessage).toBe('Error message');
      tick(5100);
      expect(component.errorMessage).toBeNull();
    }));
  });
});
