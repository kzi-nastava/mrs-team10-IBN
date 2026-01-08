import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { UserFormComponent, UserFormData } from '../../forms/user-form/user-form.component';
import {
  VehicleFormComponent,
  VehicleFormData,
} from '../../forms/vehicle-form/vehicle-form.component';
import { User } from '../../model/user.model';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ChangeDetectorRef } from '@angular/core';

interface DriverDetails {
  id: number;
  userId: number;
  vehicleId: number;
  active: boolean;
  hoursWorkedToday: number;
  vehicle?: VehicleDetails;
}

interface VehicleDetails {
  id: number;
  model: string;
  type: string;
  licensePlate: string;
  seats: number;
  babyTransport: boolean;
  petTransport: boolean;
}

@Component({
  selector: 'app-account',
  imports: [
    RouterLink,
    CommonModule,
    NavBarComponent,
    RouterOutlet,
    VehicleFormComponent,
    UserFormComponent,
  ],
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css'],
  standalone: true,
})
export class AccountComponent implements OnInit {
  protected user: User | null;
  driverDetails: DriverDetails | null = null;

  userFormData: UserFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
  };

  originalUserData: UserFormData = {
    firstName: '',
    lastName: '',
    address: '',
    phone: '',
    email: '',
  };

  vehicleData: VehicleFormData = {
    model: '',
    type: 'standard',
    licensePlate: '',
    seats: 4,
    babyTransport: false,
    petTransport: false,
  };

  hoursWorkedToday = 0;
  maxHoursPerDay = 8;
  isDriverActive = false;
  showVehicleModal = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  userProfileImage: string = 'accountpic.png';
  isLoading = false;

  constructor(private http: HttpClient, private cd: ChangeDetectorRef) {
    let logged = sessionStorage.getItem('loggedUser');
    if (logged != null) {
      this.user = JSON.parse(logged) as User;
    } else {
      this.user = null;
    }
  }

  isDriver() {
    if (this.user != null) return this.user.role === 'driver';
    return false;
  }

  ngOnInit() {
    this.initParticles();
    this.loadUserData();

    if (this.isDriver()) {
      this.loadDriverDetails();
    }
  }

  loadUserData() {
    if (!this.user?.id) return;

    this.isLoading = true;
    this.http.get<any>(`${environment.apiHost}/account/${this.user.id}`).subscribe({
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
        };

        this.originalUserData = { ...this.userFormData };

        if (userData.image) {
          this.userProfileImage = userData.image;
        }
        this.cd.detectChanges();

        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading user data:', error);
        this.showError('Failed to load user data.');
        this.isLoading = false;
      },
    });
  }

  loadDriverDetails() {
    if (!this.user?.id) return;

    this.isLoading = true;
    this.http.get<DriverDetails>(`${environment.apiHost}/drivers/${this.user.id}`).subscribe({
      next: (driverData) => {
        this.driverDetails = driverData;
        this.hoursWorkedToday = driverData.hoursWorkedToday || 0;
        this.isDriverActive = driverData.active || false;

        if (driverData.vehicle) {
          this.vehicleData = {
            model: driverData.vehicle.model || '',
            type: 'standard',
            licensePlate: driverData.vehicle.licensePlate || '',
            seats: driverData.vehicle.seats || 4,
            babyTransport: driverData.vehicle.babyTransport || false,
            petTransport: driverData.vehicle.petTransport || false,
          };
        } else if (driverData.vehicleId) {
          this.loadVehicleData(driverData.vehicleId);
        }

        this.cd.detectChanges();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading driver details:', error);
        this.showError('Failed to load driver details.');
        this.isLoading = false;
      },
    });
  }

  loadVehicleData(vehicleId: number) {
    this.http.get<VehicleDetails>(`/api/vehicles/${vehicleId}`).subscribe({
      next: (vehicle) => {
        if (vehicle) {
          this.vehicleData = {
            model: vehicle.model || '',
            type: 'luxury',
            licensePlate: vehicle.licensePlate || '',
            seats: vehicle.seats || 4,
            babyTransport: vehicle.babyTransport || false,
            petTransport: vehicle.petTransport || false,
          };
        }
        this.cd.detectChanges();
      },
      error: (error) => {
        console.error('Error loading vehicle data:', error);
      },
    });
  }

  toggleDriverStatus() {
    if (!this.user?.id || !this.driverDetails) return;

    this.isLoading = true;
    const newStatus = !this.isDriverActive;

    this.http.put(`/api/drivers/${this.driverDetails.id}/status`, { active: newStatus }).subscribe({
      next: () => {
        this.isDriverActive = newStatus;
        this.showSuccess(`Driver status changed to ${newStatus ? 'active' : 'inactive'}.`);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error updating driver status:', error);
        this.showError('Failed to update driver status.');
        this.isLoading = false;
      },
    });
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
    setTimeout(() => {
      this.successMessage = null;
    }, 3000);
  }

  showError(message: string) {
    this.errorMessage = message;
    setTimeout(() => {
      this.errorMessage = null;
    }, 3000);
  }

  onUserFormSubmit(data: UserFormData) {
    if (!this.user?.id) return;

    this.isLoading = true;

    if (this.user?.role === 'driver') {
      this.http.post(`/api/drivers/${this.driverDetails?.id}/change-request`, data).subscribe({
        next: () => {
          console.log('Sent to admin:', data);
          this.showSuccess('Changes sent to admin successfully.');
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error sending change request:', error);
          this.showError('Failed to send change request.');
          this.isLoading = false;
        },
      });
    } else {
      this.http.put(`/api/account/${this.user.id}`, data).subscribe({
        next: (updatedUser) => {
          console.log('Saving:', data);
          this.originalUserData = { ...data };
          this.userFormData = { ...data };

          if (this.user) {
            this.user = { ...this.user, ...updatedUser };
          }

          this.showSuccess('Changes saved successfully.');
          this.cd.detectChanges();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error saving user data:', error);
          this.showError('Failed to save changes.');
          this.isLoading = false;
        },
      });
    }
  }

  onVehicleFormSubmit(data: VehicleFormData) {
    if (!this.user?.id || !this.driverDetails) return;

    this.isLoading = true;

    this.http.post(`/api/drivers/${this.driverDetails.id}/vehicle-change-request`, data).subscribe({
      next: () => {
        console.log('Vehicle changes sent to admin:', data);
        this.showSuccess('Vehicle changes sent to admin successfully.');
        this.closeVehicleModal();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error sending vehicle change request:', error);
        this.showError('Failed to send vehicle change request.');
        this.isLoading = false;
      },
    });
  }

  onProfileImageChange(newImage: string) {
    if (!this.user?.id) return;

    this.isLoading = true;

    this.http
      .put(`/api/users/${this.user.id}/profile-image`, { profileImage: newImage })
      .subscribe({
        next: () => {
          this.userProfileImage = newImage;

          if (this.user) {
            this.user.image = newImage;
          }
          this.cd.detectChanges();

          this.showSuccess('Profile image updated successfully.');
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error updating profile image:', error);
          this.showError('Failed to update profile image.');
          this.isLoading = false;
        },
      });
  }

  openVehicleModal() {
    this.showVehicleModal = true;
  }

  closeVehicleModal() {
    this.showVehicleModal = false;
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

    const roleMenus = {
      passenger: [
        { icon: '❤️', label: 'Favorites', route: '/favorites' },
        { icon: '📊', label: 'My statistics', route: '/statistics/user' },
      ],

      driver: [
        { icon: '🚗', label: 'My vehicle', route: '/my-vehicle' },
        { icon: '📊', label: 'My statistics', route: '/statistics/User' },
      ],

      administrator: [
        { icon: '📊', label: 'Platform statistics', route: '/statistics/admin' },
        { icon: '📥', label: 'Requests', route: '/requests' },
        { icon: '👥', label: 'Manage users', route: '/manage-users' },
      ],
    };
    if (this.user != null) {
      return [...(roleMenus[this.user.role] || []), ...commonItems];
    } else return null;
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
