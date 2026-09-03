import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <header class="sticky top-0 z-50 glass-panel border-b border-slate-800/80">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <!-- Logo & Brand -->
          <div class="flex items-center space-x-3">
            <a routerLink="/" class="flex items-center space-x-2.5 group">
              <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-600 via-emerald-500 to-teal-400 flex items-center justify-center shadow-lg shadow-emerald-500/20 group-hover:scale-105 transition-transform duration-300">
                <svg class="w-6 h-6 text-slate-950 font-bold" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
                </svg>
              </div>
              <div class="flex flex-col">
                <span class="text-xl font-bold tracking-tight font-['Outfit'] bg-gradient-to-r from-white via-slate-100 to-emerald-400 bg-clip-text text-transparent">ApexStore</span>
                <span class="text-[10px] text-emerald-400 font-semibold tracking-widest -mt-1 uppercase">Marketplace</span>
              </div>
            </a>
          </div>

          <!-- Navigation Links -->
          <nav class="hidden md:flex items-center space-x-1 text-sm font-medium">
            @if (authService.isAdmin()) {
              <!-- Admin Navigation (Operational / Moderation only) -->
              <a routerLink="/admin" routerLinkActive="text-purple-400 bg-purple-500/10" class="px-3.5 py-2 rounded-lg text-purple-300 hover:text-white hover:bg-purple-500/20 transition-all duration-200 flex items-center space-x-1.5 border border-purple-500/30">
                <span>🛡️</span>
                <span>Moderation Console</span>
              </a>
              <a routerLink="/" routerLinkActive="text-slate-200 bg-slate-800/60" [routerLinkActiveOptions]="{exact: true}" class="px-3 py-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800/60 transition-all duration-200">
                Live Storefront ↗
              </a>
            } @else if (authService.isSeller()) {
              <!-- Seller Navigation -->
              <a routerLink="/" routerLinkActive="text-emerald-400 bg-emerald-500/10" [routerLinkActiveOptions]="{exact: true}" class="px-3.5 py-2 rounded-lg text-slate-300 hover:text-white hover:bg-slate-800/60 transition-all duration-200">
                Marketplace
              </a>
              <a routerLink="/seller" routerLinkActive="text-cyan-400 bg-cyan-500/10" class="px-3.5 py-2 rounded-lg text-cyan-300 hover:text-cyan-200 hover:bg-cyan-500/20 transition-all duration-200 flex items-center space-x-1.5 border border-cyan-500/30">
                <span>🏪</span>
                <span>Seller Hub</span>
              </a>
              @if (authService.isAuthenticated()) {
                <a routerLink="/my-orders" routerLinkActive="text-emerald-400 bg-emerald-500/10" class="px-3.5 py-2 rounded-lg text-slate-300 hover:text-white hover:bg-slate-800/60 transition-all duration-200">
                  My Orders
                </a>
              }
            } @else {
              <!-- Customer / Guest Navigation -->
              <a routerLink="/" routerLinkActive="text-emerald-400 bg-emerald-500/10" [routerLinkActiveOptions]="{exact: true}" class="px-3.5 py-2 rounded-lg text-slate-300 hover:text-white hover:bg-slate-800/60 transition-all duration-200">
                Marketplace
              </a>
              @if (authService.isAuthenticated()) {
                <a routerLink="/my-orders" routerLinkActive="text-emerald-400 bg-emerald-500/10" class="px-3.5 py-2 rounded-lg text-slate-300 hover:text-white hover:bg-slate-800/60 transition-all duration-200">
                  My Orders
                </a>
              }
            }
          </nav>

          <!-- Right Actions (Cart, Auth) -->
          <div class="flex items-center space-x-3">
            <!-- Cart Button (Hidden for Admin accounts) -->
            @if (!authService.isAdmin()) {
              <a routerLink="/cart" class="relative p-2.5 rounded-xl bg-slate-900/80 border border-slate-800 text-slate-300 hover:text-emerald-400 hover:border-emerald-500/40 transition-all duration-200 group" title="Shopping Cart">
                <svg class="w-5 h-5 group-hover:scale-110 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                </svg>
                @if (cartService.itemCount() > 0) {
                  <span class="absolute -top-1.5 -right-1.5 bg-gradient-to-r from-emerald-500 to-teal-400 text-slate-950 font-bold text-xs w-5 h-5 rounded-full flex items-center justify-center shadow-md animate-pulse">
                    {{ cartService.itemCount() }}
                  </span>
                }
              </a>
            }

            <!-- User Auth Menu -->
            @if (authService.isAuthenticated()) {
              <div class="flex items-center space-x-3 pl-2 border-l border-slate-800">
                <div class="text-right hidden sm:block">
                  <p class="text-xs font-semibold text-slate-200">{{ authService.userFullName() }}</p>
                  <p class="text-[10px] font-medium">
                    @if (authService.currentUser()?.role === 'ROLE_ADMIN') {
                      <span class="text-purple-400 font-bold">🛡️ Administrator</span>
                    } @else if (authService.currentUser()?.role === 'ROLE_SELLER') {
                      <span class="text-cyan-400 font-semibold">🏪 {{ authService.sellerProfile()?.storeName || 'Merchant' }}</span>
                    } @else {
                      <span class="text-emerald-400">👤 Customer</span>
                    }
                  </p>
                </div>
                <button (click)="authService.logout()" title="Sign Out" class="p-2 rounded-xl bg-slate-900 border border-slate-800 text-slate-400 hover:text-rose-400 hover:border-rose-500/30 transition-all duration-200">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                  </svg>
                </button>
              </div>
            } @else {
              <div class="flex items-center space-x-2">
                <a routerLink="/login" class="px-3.5 py-1.5 text-sm font-medium text-slate-300 hover:text-white rounded-lg hover:bg-slate-800/80 transition-colors">
                  Sign In
                </a>
                <a routerLink="/register" class="px-3.5 py-1.5 text-sm font-medium text-slate-950 bg-emerald-400 hover:bg-emerald-300 rounded-lg font-semibold shadow-sm shadow-emerald-500/20 transition-all duration-200">
                  Sign Up
                </a>
              </div>
            }
          </div>
        </div>
      </div>
    </header>
  `
})
export class NavbarComponent {
  authService = inject(AuthService);
  cartService = inject(CartService);
}
