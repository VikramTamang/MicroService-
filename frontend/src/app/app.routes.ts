import { Routes } from '@angular/router';
import { adminGuard, sellerGuard, customerOnlyGuard, customerAuthGuard, authGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./components/catalog/catalog.component').then(m => m.CatalogComponent)
  },
  {
    path: 'products/:slug',
    loadComponent: () => import('./components/catalog/product-detail.component').then(m => m.ProductDetailComponent)
  },
  {
    path: 'product/:id',
    loadComponent: () => import('./components/catalog/product-detail.component').then(m => m.ProductDetailComponent)
  },
  {
    path: 'cart',
    canActivate: [customerOnlyGuard],
    loadComponent: () => import('./components/cart/cart.component').then(m => m.CartComponent)
  },
  {
    path: 'checkout',
    canActivate: [customerAuthGuard],
    loadComponent: () => import('./components/checkout/checkout.component').then(m => m.CheckoutComponent)
  },
  {
    path: 'my-orders',
    canActivate: [customerAuthGuard],
    loadComponent: () => import('./components/my-orders/my-orders.component').then(m => m.MyOrdersComponent)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('./components/profile/profile.component').then(m => m.ProfileComponent)
  },
  {
    path: 'seller',
    canActivate: [sellerGuard],
    loadComponent: () => import('./components/seller/seller-dashboard.component').then(m => m.SellerDashboardComponent)
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    loadComponent: () => import('./components/admin/admin-dashboard.component').then(m => m.AdminDashboardComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./components/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./components/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
