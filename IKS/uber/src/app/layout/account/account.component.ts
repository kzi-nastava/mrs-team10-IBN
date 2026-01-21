import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { UserFormComponent, UserFormData } from '../../forms/user-form/user-form.component';
import {
  VehicleFormComponent,
  VehicleFormData,
} from '../../forms/vehicle-form/vehicle-form.component';
import { VehiclePriceComponent } from '../../forms/vehicle-price/vehicle-price.component';
import { VehicleTypeService } from '../../service/vehicle-type.service';
import { User } from '../../model/user.model';
import { DriverDetails } from '../../model/driver.model';
import { environment } from '../../../environments/environment';
import { AuthService } from '../../service/auth.service';
import { ChangePasswordComponent } from '../../forms/change-password/change-password.component';
import { FavoritesPopupComponent } from '../favorites-popup/favorites-popup.component';

@Component({
  selector: 'app-account',
  imports: [
    RouterLink,
    CommonModule,
    NavBarComponent,
    RouterOutlet,
    VehicleFormComponent,
    UserFormComponent,
    VehiclePriceComponent,
    ChangePasswordComponent,
    FavoritesPopupComponent,
  ],
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css'],
  standalone: true,
})
export class AccountComponent implements OnInit {
  private authService = inject(AuthService);

  user: User | null = null;
  driverDetails: DriverDetails | null = null;
  userFormData!: UserFormData;
  vehicleData!: VehicleFormData;
  originalUserFormData!: UserFormData;
  originalVehicleData!: VehicleFormData;

  hoursWorkedToday = 0;
  maxHoursPerDay = 8;
  isDriverActive = false;
  showVehicleModal = false;
  showVehiclePriceModal = false;
  showChangePassword = false;
  showFavorites = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  userProfileImage: string = 'accountpic.png';
  isLoading = false;

  constructor(
    private http: HttpClient,
    private cd: ChangeDetectorRef,
    private vehicleTypeService: VehicleTypeService,
  ) {}

  ngOnInit() {
    this.initParticles();
    this.loadUserData();
  }

  private getAuthHeaders() {
    const token = localStorage.getItem('auth_token');
    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`,
      }),
    };
  }

  isDriver() {
    const role = this.authService.role();
    return role?.toUpperCase() === 'DRIVER';
  }

  isAdministrator() {
    const role = this.authService.role();
    return role?.toUpperCase() === 'ADMINISTRATOR';
  }

  isPassenger() {
    const role = this.authService.role();
    return role?.toUpperCase() === 'PASSENGER';
  }

  loadUserData() {
    this.isLoading = true;

    this.http.get<any>(`${environment.apiHost}/account/me`, this.getAuthHeaders()).subscribe({
      next: (response) => {
        const userData = response.createdUserDTO;
        const accountData = response.accountDTO;

        if (!userData || !accountData) {
          this.showError('User data is incomplete.');
          this.isLoading = false;
          return;
        }

        this.userFormData = {
          firstName: userData.name || '',
          lastName: userData.lastName || '',
          address: userData.homeAddress || '',
          phone: userData.phone || '',
          email: accountData.email || '',
          image: userData.image || '',
        };

        this.userProfileImage = userData.image || 'accountpic.png';
        this.originalUserFormData = { ...this.userFormData };

        const roleFromToken = this.authService.role();

        if (roleFromToken) {
          this.user = { role: roleFromToken.toLowerCase() } as User;
        } else {
          this.user = { role: 'passenger' } as User;
        }

        if (this.isDriver()) {
          this.loadDriverDetails();
        }

        this.cd.detectChanges();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading user data:', err);
        this.showError('Failed to load user data.');
        this.isLoading = false;
      },
    });
  }

  attemptLoadDriverDetails() {
    this.http
      .get<DriverDetails>(`${environment.apiHost}/drivers/me`, this.getAuthHeaders())
      .subscribe({
        next: (response) => {
          this.processDriverDetails(response);
        },
        error: () => {
          this.cd.detectChanges();
        },
      });
  }

  loadDriverDetails() {
    this.isLoading = true;

    this.http
      .get<DriverDetails>(`${environment.apiHost}/drivers/me`, this.getAuthHeaders())
      .subscribe({
        next: (response) => {
          this.processDriverDetails(response);
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error loading driver details:', err);
          this.showError('Failed to load driver details.');
          this.isLoading = false;
        },
      });
  }

  processDriverDetails(response: DriverDetails) {
    this.hoursWorkedToday = response.uptime || 0;
    this.isDriverActive = true;

    if (response.vehicleDTO) {
      this.vehicleData = {
        model: response.vehicleDTO.model || '',
        type:
          (response.vehicleDTO.vehicleTypeDTO?.name?.toLowerCase() as
            | 'standard'
            | 'luxury'
            | 'van') || 'standard',
        plate: response.vehicleDTO.plate || '',
        seatNumber: response.vehicleDTO.seatNumber || 4,
        babySeat: response.vehicleDTO.babySeat || false,
        petFriendly: response.vehicleDTO.petFriendly || false,
      };
      this.originalVehicleData = { ...this.vehicleData };
    }

    if (response.createUserDTO) {
      this.userFormData = {
        firstName: response.createUserDTO.name || '',
        lastName: response.createUserDTO.lastName || '',
        address: response.createUserDTO.homeAddress || '',
        phone: response.createUserDTO.phone || '',
        email: response.accountDTO?.email || '',
        image: response.createUserDTO.image || '',
      };
      this.userProfileImage = response.createUserDTO.image || 'accountpic.png';
      this.originalUserFormData = { ...this.userFormData };
    }

    this.cd.detectChanges();
  }

  getProgressPercentage(): number {
    return (this.hoursWorkedToday / this.maxHoursPerDay) * 100;
  }

  getStatusColor(): string {
    return this.isDriverActive ? '#10b981' : '#ef4444';
  }

  getStatusText(): string {
    return this.isDriverActive ? 'Active' : 'Inactive';
  }

  showSuccess(message: string) {
    this.successMessage = message;
    this.cd.detectChanges();
    setTimeout(() => {
      this.successMessage = null;
      this.cd.detectChanges();
    }, 3000);
  }

  showError(message: string) {
    this.errorMessage = message;
    this.cd.detectChanges();
    setTimeout(() => {
      this.errorMessage = null;
      this.cd.detectChanges();
    }, 3000);
  }

  onUserFormSubmit(data: UserFormData) {
    const url = this.isDriver()
      ? `${environment.apiHost}/drivers/me/change-request`
      : `${environment.apiHost}/account/me/profile`;

    const payload = this.isDriver()
      ? {
          password: null,
          createUserDTO: {
            name: data.firstName,
            lastName: data.lastName,
            homeAddress: data.address,
            phone: data.phone,
            image: data.image || null,
          },
          vehicleDTO: null,
        }
      : {
          name: data.firstName,
          lastName: data.lastName,
          homeAddress: data.address,
          phone: data.phone,
          image: data.image || null,
        };

    this.isLoading = true;
    const request = this.isDriver()
      ? this.http.post(url, payload, this.getAuthHeaders())
      : this.http.put(url, payload, this.getAuthHeaders());

    request.subscribe({
      next: (res: any) => {
        if (this.isDriver()) {
          this.userFormData = { ...this.originalUserFormData };
          this.showSuccess('Change request sent to admin successfully.');
        } else {
          this.userFormData = { ...data };
          this.originalUserFormData = { ...data };
          this.showSuccess('Profile updated successfully.');
        }

        this.cd.detectChanges();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error saving user data:', err);

        if (this.isDriver()) {
          this.showError('Failed to submit change request.');
        } else {
          this.showError('Failed to update profile.');
        }

        this.isLoading = false;
      },
    });
  }

  onVehicleFormSubmit(data: VehicleFormData) {
    if (!this.isDriver()) return;

    const vehicleTypeDTO = this.vehicleTypeService.mapTypeToDTO(data.type);
    const payload = {
      createUserDTO: null,
      vehicleDTO: {
        model: data.model,
        plate: data.plate,
        seatNumber: data.seatNumber,
        babySeat: data.babySeat,
        petFriendly: data.petFriendly,
        vehicleTypeDTO: vehicleTypeDTO,
      },
    };

    this.isLoading = true;
    this.http
      .post(`${environment.apiHost}/drivers/me/change-request`, payload, this.getAuthHeaders())
      .subscribe({
        next: () => {
          this.loadDriverDetails();
          this.showVehicleModal = false;
          this.showSuccess('Vehicle change request sent to admin successfully.');
          this.cd.detectChanges();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error saving vehicle data:', err);
          this.showError('Failed to submit vehicle change request.');
          this.isLoading = false;
        },
      });
  }

  onProfileImageChange(newImage: string) {
    this.isLoading = true;
    this.http
      .put(
        `${environment.apiHost}/account/me/profile-image`,
        { profileImage: newImage },
        this.getAuthHeaders(),
      )
      .subscribe({
        next: () => {
          this.userProfileImage = newImage;
          this.userFormData.image = newImage;
          this.showSuccess('Profile image updated successfully.');
          this.cd.detectChanges();
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error updating profile image:', err);
          this.showError('Failed to update profile image.');
          this.isLoading = false;
        },
      });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        const base64Image = e.target.result;

        this.userProfileImage = base64Image;

        this.userFormData = {
          ...this.userFormData,
          image: base64Image,
        };

        this.cd.detectChanges();

        this.onUserFormSubmit(this.userFormData);
      };
      reader.readAsDataURL(file);
    }
  }

  openVehicleModal() {
    this.showVehicleModal = true;
  }

  closeVehicleModal() {
    this.showVehicleModal = false;
  }

  openVehiclePriceModal() {
    this.showVehiclePriceModal = true;
  }

  closeVehiclePriceModal() {
    this.showVehiclePriceModal = false;
  }

  openChangePassword() {
    this.showChangePassword = true;
  }

  closeChangePassword() {
    this.showChangePassword = false;
  }

  openFavorites() {
    this.showFavorites = true;
  }

  closeFavorites() {
    this.showFavorites = false;
  }

  onPasswordChanged(message: string) {
    this.showSuccess(message);
    this.closeChangePassword();
  }

  get menuItems() {
    const commonItems = [
      {
        icon: '🔑',
        label: 'Change password',
        route: '/change-password',
      },
      {
        icon: '🗑️',
        label: 'Delete account',
        route: '/delete-account',
      },
    ];

    type RoleType = 'passenger' | 'driver' | 'administrator';

    const roleMenus: Record<RoleType, Array<{ icon: string; label: string; route: string }>> = {
      passenger: [
        { icon: '❤️', label: 'Favorites', route: '/favorites' },
        { icon: '📊', label: 'My statistics', route: '/statistics/user' },
      ],

      driver: [
        { icon: '🚗', label: 'My vehicle', route: '/my-vehicle' },
        { icon: '📊', label: 'My statistics', route: '/statistics/User' },
        { icon: '📅', label: 'Scheduled rides', route: '/scheduled-rides' },
      ],

      administrator: [
        { icon: '📊', label: 'Platform statistics', route: '/statistics/admin' },
        { icon: '📥', label: 'Requests', route: '/requests' },
        { icon: '👥', label: 'Manage users', route: '/manage-users' },
        { icon: '💵', label: 'Vehicle price', route: '/vehicle-price' },
      ],
    };

    const userRole = this.authService.role();

    if (!userRole) {
      return null;
    }

    const roleLowercase = userRole.toLowerCase() as RoleType;

    return [...(roleMenus[roleLowercase] || []), ...commonItems];
  }

  initParticles() {
    const canvas = document.getElementById('particles-canvas') as HTMLCanvasElement;
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;

    const particles: any[] = [];
    const particleCount = 50;
    const icons = ['🚕', '🚖'];

    class Particle {
      x: number;
      y: number;
      size: number;
      speedX: number;
      speedY: number;
      icon: string;
      opacity: number;

      constructor() {
        this.x = Math.random() * canvas.width;
        this.y = Math.random() * canvas.height;
        this.size = Math.random() * 40 + 30;
        this.speedX = Math.random() * 0.8 - 0.25;
        this.speedY = Math.random() * 0.8 - 0.25;
        this.icon = icons[Math.floor(Math.random() * icons.length)];
        this.opacity = Math.random() * 0.4 + 0.3;
      }

      update() {
        this.x += this.speedX;
        this.y += this.speedY;

        if (this.x > canvas.width) this.x = 0;
        if (this.x < 0) this.x = canvas.width;
        if (this.y > canvas.height) this.y = 0;
        if (this.y < 0) this.y = canvas.height;
      }

      draw() {
        if (!ctx) return;
        ctx.font = `${this.size}px Arial`;
        ctx.globalAlpha = this.opacity;
        ctx.fillText(this.icon, this.x, this.y);
      }
    }

    for (let i = 0; i < particleCount; i++) {
      particles.push(new Particle());
    }

    const animate = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      particles.forEach((particle) => {
        particle.update();
        particle.draw();
      });

      requestAnimationFrame(animate);
    };

    animate();

    window.addEventListener('resize', () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    });
  }
}
