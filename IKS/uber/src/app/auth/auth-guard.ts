import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { inject } from '@angular/core';

export const authGuard: CanActivateFn = (route, state) => {
  const authService: AuthService = inject(AuthService)
  const router: Router = inject(Router)

  const role = authService.role.asReadonly();

  if (role() == null) {
    router.navigate(['login']);
    return false;
  }
  if (!route.data['role'].includes(role())) {
    router.navigate(['home']);
    return false;
  }
  return true;
}