import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { NavBarComponent } from '../nav-bar/nav-bar.component';
import { DriverFormComponent, DriverFormData } from '../../forms/driver-form/driver-form.component';
import {
  VehicleFormComponent,
  VehicleFormData,
} from '../../forms/vehicle-form/vehicle-form.component';

@Component({
  selector: 'app-account',
  imports: [
    RouterLink,
    CommonModule,
    NavBarComponent,
    RouterOutlet,
    DriverFormComponent,
    VehicleFormComponent,
  ],
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css'],
})
export class AccountComponent implements OnInit {
  userRole: 'user' | 'driver' | 'admin' = 'admin';

  driverFormData: DriverFormData = {
    firstName: 'Bojana',
    lastName: 'Paunovic',
    address: 'Kopernikova 23, Novi Sad',
    phone: '0612018550',
    email: 'paunovicboka@gmail.com',
  };

  originalDriverData: DriverFormData = {
    firstName: 'Bojana',
    lastName: 'Paunovic',
    address: 'Kopernikova 23, Novi Sad',
    phone: '0612018550',
    email: 'paunovicboka@gmail.com',
  };

  vehicleData: VehicleFormData = {
    model: '',
    type: 'standard',
    licensePlate: '',
    seats: 4,
    babyTransport: false,
    petTransport: false,
  };

  hoursWorkedToday = 7;
  maxHoursPerDay = 8;
  showVehicleModal = false;
  successMessage: string | null = null;
  userProfileImage: string = 'accountpic.png';

  ngOnInit() {
    this.initParticles();
  }

  getProgressPercentage(): number {
    return (this.hoursWorkedToday / this.maxHoursPerDay) * 100;
  }

  showSuccess(message: string) {
    this.successMessage = message;
    setTimeout(() => {
      this.successMessage = null;
    }, 3000);
  }

  onDriverFormSubmit(data: DriverFormData) {
    if (this.userRole === 'driver') {
      console.log('Sent to admin:', data);
      this.showSuccess('Changes sent to admin successfully.');
    } else {
      console.log('Saving:', data);
      this.originalDriverData = { ...data };
      this.showSuccess('Changes saved successfully.');
    }
  }

  onVehicleFormSubmit(data: VehicleFormData) {
    console.log('Vehicle changes sent to admin:', data);
    this.showSuccess('Vehicle changes sent to admin successfully.');
    this.closeVehicleModal();
  }

  onProfileImageChange(newImage: string) {
    this.userProfileImage = newImage;
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
      user: [
        { icon: '❤️', label: 'Favorites', route: '/favorites' },
        { icon: '📊', label: 'My statistics', route: '/statistics/user' },
      ],

      driver: [
        { icon: '🚗', label: 'My vehicle', route: '/my-vehicle' },
        { icon: '📊', label: 'My statistics', route: '/statistics/driver' },
      ],

      admin: [
        { icon: '📊', label: 'Platform statistics', route: '/statistics/admin' },
        { icon: '📥', label: 'Requests', route: '/requests' },
        { icon: '👥', label: 'Manage users', route: '/manage-users' },
      ],
    };
    return [...(roleMenus[this.userRole] || []), ...commonItems];
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
