import { Component, Inject, OnInit } from '@angular/core';
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
import { DriverDetails } from '../../model/driver.model';
import { VehicleTypeService } from '../../service/vehicle-type.service';

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
  userFormData!: UserFormData;
  vehicleData!: VehicleFormData;
  originalUserFormData!: UserFormData;
  originalVehicleData!: VehicleFormData;

  hoursWorkedToday = 0;
  maxHoursPerDay = 8;
  isDriverActive = false;
  showVehicleModal = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;
  userProfileImage: string = 'accountpic.png';
  isLoading = false;

  constructor(
    private http: HttpClient,
    private cd: ChangeDetectorRef,
    private vehicleTypeService: VehicleTypeService
  ) {
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

    if (this.isDriver()) {
      this.loadDriverDetails();
    } else {
      this.loadUserData();
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
          image: userData.image || '',
        };

        if (userData.image) {
          this.userProfileImage = userData.image;
        } else {
          this.userProfileImage = 'accountpic.png';
        }

        this.originalUserFormData = { ...this.userFormData };
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
      next: (response) => {
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

          if (response.createUserDTO.image) {
            this.userProfileImage = response.createUserDTO.image;
          } else {
            this.userProfileImage = 'accountpic.png';
          }

          this.originalUserFormData = { ...this.userFormData };
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
    if (!this.user?.id) return;

    this.isLoading = true;

    if (this.isDriver()) {
      const driverId = this.user.id;

      const payload = {
        password: null,
        createUserDTO: {
          id: driverId,
          name: data.firstName,
          lastName: data.lastName,
          homeAddress: data.address,
          phone: data.phone,
          image: data.image || null,
        },
        vehicleDTO: null,
      };

      this.http
        .post(`${environment.apiHost}/drivers/${driverId}/change-request`, payload)
        .subscribe({
          next: () => {
            this.isLoading = false;
            this.showSuccess('Profile changes sent to admin successfully.');
            this.originalUserFormData = { ...data };
            this.cd.detectChanges();
          },
          error: (error) => {
            console.error('Error sending change request:', error);
            this.isLoading = false;
            this.showError('Failed to send profile change request.');
            this.cd.detectChanges();
          },
        });
      return;
    }

    const passengerPayload = {
      id: this.user.id,
      name: data.firstName,
      lastName: data.lastName,
      homeAddress: data.address,
      phone: data.phone,
      image: data.image || null,
    };

    this.http
      .put(`${environment.apiHost}/account/${this.user.id}/profile`, passengerPayload)
      .subscribe({
        next: (updatedUser: any) => {
          this.userFormData = { ...data };
          this.originalUserFormData = { ...data };
          if (this.user) {
            this.user = { ...this.user, ...updatedUser };
          }
          this.isLoading = false;
          this.showSuccess('Profile changed successfully.');
          this.cd.detectChanges();
        },
        error: (error) => {
          console.error('Error saving user data:', error);
          this.isLoading = false;
          this.showError('Failed to save user data.');
          this.cd.detectChanges();
        },
      });
  }

  onVehicleFormSubmit(data: VehicleFormData) {
    if (!this.user?.id || !this.isDriver()) return;

    const driverId = this.user.id;
    this.vehicleData = { ...data };

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
    this.http.post(`${environment.apiHost}/drivers/${driverId}/change-request`, payload).subscribe({
      next: () => {
        this.isLoading = false;
        this.showSuccess('Vehicle changes sent to admin successfully.');
        this.originalVehicleData = { ...data };
        this.closeVehicleModal();
        this.cd.detectChanges();
      },
      error: (error) => {
        console.error('Error sending vehicle change request:', error);
        console.error('Error details:', error.error);
        this.isLoading = false;
        this.showError('Failed to send vehicle change request.');
        this.cd.detectChanges();
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
