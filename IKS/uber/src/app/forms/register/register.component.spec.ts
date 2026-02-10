import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpResponse } from '@angular/common/http';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let router: Router;
  let mockCdr: jasmine.SpyObj<ChangeDetectorRef>;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['register']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, ReactiveFormsModule, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate');
    spyOn(component['cdr'], 'detectChanges');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Form Validation', () => {
    it('should initialize with an invalid form', () => {
      expect(component.registerForm.invalid).toBeTruthy();
    });

    it('should require email field', () => {
      const email = component.registerForm.get('email');
      expect(email?.hasError('required')).toBeTruthy();
    });

    it('should validate email format', () => {
      const email = component.registerForm.get('email');
      email?.setValue('invalidemail');
      expect(email?.hasError('email')).toBeTruthy();
      
      email?.setValue('valid@email.com');
      expect(email?.hasError('email')).toBeFalsy();
    });

    it('should require password with minimum 6 characters', () => {
      const password = component.registerForm.get('password');
      
      password?.setValue('12345');
      expect(password?.hasError('minlength')).toBeTruthy();
      
      password?.setValue('123456');
      expect(password?.hasError('minlength')).toBeFalsy();
    });

    it('should require all mandatory fields', () => {
      expect(component.registerForm.get('email')?.hasError('required')).toBeTruthy();
      expect(component.registerForm.get('password')?.hasError('required')).toBeTruthy();
      expect(component.registerForm.get('confirmPassword')?.hasError('required')).toBeTruthy();
      expect(component.registerForm.get('name')?.hasError('required')).toBeTruthy();
      expect(component.registerForm.get('lastName')?.hasError('required')).toBeTruthy();
      expect(component.registerForm.get('address')?.hasError('required')).toBeTruthy();
      expect(component.registerForm.get('phone')?.hasError('required')).toBeTruthy();
    });

    it('should be valid when all required fields are filled correctly', () => {
      component.registerForm.patchValue({
        email: 'test@example.com',
        password: 'password123',
        confirmPassword: 'password123',
        name: 'John',
        lastName: 'Doe',
        address: '123 Main St',
        phone: '1234567890'
      });
      
      expect(component.registerForm.valid).toBeTruthy();
    });
  });

  describe('Error Detection', () => {
    it('should set error message for missing email', () => {
      component.registerForm.patchValue({ email: '' });
      component.detectErrors();
      expect(component.errormsg).toBe('Email is required!');
    });

    it('should set error message for invalid email', () => {
      component.registerForm.patchValue({ email: 'invalidemail' });
      component.detectErrors();
      expect(component.errormsg).toBe('Please enter a valid email address!');
    });

    it('should set error message for missing password', () => {
      component.registerForm.patchValue({
        email: 'test@example.com',
        password: ''
      });
      component.detectErrors();
      expect(component.errormsg).toBe('Password is required!');
    });

    it('should set error message for missing confirmPassword', () => {
      component.registerForm.patchValue({
        email: 'test@example.com',
        password: 'password123',
        confirmPassword: ''
      });
      component.detectErrors();
      expect(component.errormsg).toBe('Please confirm your password!');
    });

    it('should set error message when passwords do not match', () => {
      component.registerForm.patchValue({
        email: 'test@example.com',
        password: 'password123',
        confirmPassword: 'differentpassword'
      });
      component.detectErrors();
      expect(component.errormsg).toBe('Passwords do not match!');
    });

    it('should set error message for missing name', () => {
      component.registerForm.patchValue({
        email: 'test@example.com',
        password: 'password123',
        confirmPassword: 'password123',
        name: ''
      });
      component.detectErrors();
      expect(component.errormsg).toBe('Name is required!');
    });
  });

  describe('Registration Submission', () => {
    beforeEach(() => {
      component.registerForm.patchValue({
        email: 'test@example.com',
        password: 'password123',
        confirmPassword: 'password123',
        name: 'John',
        lastName: 'Doe',
        address: '123 Main St',
        phone: '1234567890'
      });
    });

    it('should not call authService.register if form is invalid', () => {
      component.registerForm.patchValue({ email: '' });
      component.register();
      expect(mockAuthService.register).not.toHaveBeenCalled();
    });

    it('should call authService.register with correct data when form is valid', () => {
      mockAuthService.register.and.returnValue(of(new HttpResponse({ status: 200, body: {} })));
      
      component.register();
      
      expect(mockAuthService.register).toHaveBeenCalledWith({
        email: 'test@example.com',
        password: 'password123',
        type: 'PASSENGER',
        name: 'John',
        lastName: 'Doe',
        homeAddress: '123 Main St',
        phone: '1234567890',
        image: 'accountpic.png'
      });
    });

    it('should navigate to login page on successful registration', () => {
      mockAuthService.register.and.returnValue(of(new HttpResponse({ status: 200, body: {} })));
      
      component.register();
      
      expect(router.navigate).toHaveBeenCalledWith(['/login']);
    });

    it('should set error message when registration fails', () => {
      mockAuthService.register.and.returnValue(
        throwError(() => new Error('Registration failed'))
      );
      
      component.register();
      
      expect(component.errormsg).toBe('Account with this email address already exists!');
      expect(component['cdr'].detectChanges).toHaveBeenCalled();
    });

    it('should include custom profile image if uploaded', () => {
      mockAuthService.register.and.returnValue(of(new HttpResponse({ status: 200, body: {} })));
      component.userProfileImage = 'data:image/png;base64,customimage';
      
      component.register();
      
      const calledData = mockAuthService.register.calls.mostRecent().args[0];
      expect(calledData.image).toBe('data:image/png;base64,customimage');
    });
  });

  describe('File Upload', () => {
    it('should update filename when file is selected', () => {
      const mockFile = new File([''], 'test-image.png', { type: 'image/png' });
      const mockEvent = {
        target: {
          files: [mockFile]
        }
      };
      
      component.onFileSelected(mockEvent);
      
      expect(component.filename).toBe('test-image.png');
    });

    it('should convert uploaded file to base64 and update userProfileImage', (done) => {
      const mockFile = new File(['fake-content'], 'test-image.png', { type: 'image/png' });
      const mockEvent = {
        target: {
          files: [mockFile]
        }
      };
      
      const mockBase64 = 'data:image/png;base64,fakebase64data';
      spyOn(window as any, 'FileReader').and.returnValue({
        readAsDataURL: function(file: File) {
          this.onload({ target: { result: mockBase64 } });
        }
      });
      
      component.onFileSelected(mockEvent);
      
      setTimeout(() => {
        expect(component.userProfileImage).toBe(mockBase64);
        expect(component['cdr'].detectChanges).toHaveBeenCalled();
        done();
      }, 100);
    });

    it('should not update anything if no file is selected', () => {
      const initialFilename = component.filename;
      const mockEvent = {
        target: {
          files: []
        }
      };
      
      component.onFileSelected(mockEvent);
      
      expect(component.filename).toBe(initialFilename);
    });
  });
});