import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="max-w-md mx-auto py-8">
      <div class="glass-card rounded-3xl p-8 border border-slate-800 shadow-2xl space-y-6">
        <div class="text-center space-y-2">
          <div class="w-12 h-12 rounded-2xl bg-gradient-to-tr from-emerald-600 to-teal-400 mx-auto flex items-center justify-center shadow-lg shadow-emerald-500/20">
            <svg class="w-6 h-6 text-slate-950 font-bold" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>
          <h2 class="text-2xl font-bold font-['Outfit'] text-white">Welcome Back</h2>
          <p class="text-xs text-slate-400">Sign in to your ApexStore microservices account</p>
        </div>

        @if (errorMessage()) {
          <div class="p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-400 text-xs flex items-center space-x-2">
            <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>{{ errorMessage() }}</span>
          </div>
        }

        <form (ngSubmit)="onLogin()" class="space-y-4 text-xs">
          <div class="space-y-1.5">
            <label class="font-medium text-slate-300">Email Address</label>
            <input 
              type="email" 
              [(ngModel)]="email" 
              name="email"
              required
              placeholder="customer@example.com" 
              class="w-full p-3 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" 
            />
          </div>

          <div class="space-y-1.5">
            <label class="font-medium text-slate-300">Password</label>
            <input 
              type="password" 
              [(ngModel)]="password" 
              name="password"
              required
              placeholder="••••••••" 
              class="w-full p-3 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" 
            />
          </div>

          <button 
            type="submit" 
            [disabled]="loading() || !email || !password"
            class="w-full py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 disabled:opacity-40 text-slate-950 font-bold text-xs shadow-lg shadow-emerald-500/20 active:scale-[0.99] transition-all duration-200 flex items-center justify-center space-x-2">
            @if (loading()) {
              <div class="w-4 h-4 border-2 border-slate-950 border-t-transparent rounded-full animate-spin"></div>
              <span>Signing In...</span>
            } @else {
              <span>Sign In</span>
            }
          </button>
        </form>

        <!-- Quick Fill Helper Buttons for Demo -->
        <div class="pt-4 border-t border-slate-800/80 space-y-2">
          <p class="text-[10px] text-slate-500 uppercase tracking-wider text-center">Quick Demo Credentials</p>
          <div class="grid grid-cols-2 gap-2">
            <button type="button" (click)="fillDemo('seller1@example.com', 'Password@123')" class="p-2 rounded-lg bg-slate-900 hover:bg-slate-850 border border-slate-800 text-[11px] text-amber-400 font-medium transition-colors text-center">
              🏪 Seller 1 (Apex)
            </button>
            <button type="button" (click)="fillDemo('seller2@example.com', 'Password@123')" class="p-2 rounded-lg bg-slate-900 hover:bg-slate-850 border border-slate-800 text-[11px] text-amber-400 font-medium transition-colors text-center">
              🏪 Seller 2 (Nordic)
            </button>
            <button type="button" (click)="fillDemo('admin@example.com', 'Password@123')" class="p-2 rounded-lg bg-slate-900 hover:bg-slate-850 border border-slate-800 text-[11px] text-purple-400 font-medium transition-colors text-center">
              🔑 Admin Demo
            </button>
            <button type="button" (click)="fillDemo('customer@example.com', 'Password@123')" class="p-2 rounded-lg bg-slate-900 hover:bg-slate-850 border border-slate-800 text-[11px] text-emerald-400 font-medium transition-colors text-center">
              👤 Customer Demo
            </button>
          </div>
        </div>

        <div class="text-center text-xs text-slate-400 pt-2">
          Don't have an account? 
          <a routerLink="/register" class="text-emerald-400 font-semibold hover:underline ml-1">Create account</a>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  email = '';
  password = '';
  loading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  fillDemo(demoEmail: string, demoPass: string): void {
    this.email = demoEmail;
    this.password = demoPass;
  }

  onLogin(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        if (res.success) {
          const returnUrl = this.route.snapshot.queryParams['returnUrl'];
          if (returnUrl) {
            this.router.navigateByUrl(returnUrl);
          } else if (this.authService.isAdmin()) {
            this.router.navigate(['/admin']);
          } else if (this.authService.isSeller()) {
            this.router.navigate(['/seller']);
          } else {
            this.router.navigate(['/']);
          }
        }
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Invalid email or password. Please verify credentials.');
        this.loading.set(false);
      }
    });
  }
}
