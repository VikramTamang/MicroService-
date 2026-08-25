import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { RegisterRequest } from '../../../models/user.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="max-w-md mx-auto py-8">
      <div class="glass-card rounded-3xl p-8 border border-slate-800 shadow-2xl space-y-6">
        <div class="text-center space-y-2">
          <div class="w-12 h-12 rounded-2xl bg-gradient-to-tr from-emerald-600 to-teal-400 mx-auto flex items-center justify-center shadow-lg shadow-emerald-500/20">
            <svg class="w-6 h-6 text-slate-950 font-bold" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
            </svg>
          </div>
          <h2 class="text-2xl font-bold font-['Outfit'] text-white">Create Account</h2>
          <p class="text-xs text-slate-400">Join ApexStore with microservices-powered identity</p>
        </div>

        @if (errorMessage()) {
          <div class="p-3.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-400 text-xs flex items-center space-x-2">
            <svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>{{ errorMessage() }}</span>
          </div>
        }

        <form (ngSubmit)="onRegister()" class="space-y-4 text-xs">
          <div class="grid grid-cols-2 gap-3">
            <div class="space-y-1.5">
              <label class="font-medium text-slate-300">First Name</label>
              <input type="text" [(ngModel)]="firstName" name="firstName" required placeholder="Alex" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
            </div>
            <div class="space-y-1.5">
              <label class="font-medium text-slate-300">Last Name</label>
              <input type="text" [(ngModel)]="lastName" name="lastName" required placeholder="Rivers" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
            </div>
          </div>

          <div class="space-y-1.5">
            <label class="font-medium text-slate-300">Email Address</label>
            <input type="email" [(ngModel)]="email" name="email" required placeholder="alex.rivers@example.com" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
          </div>

          <div class="space-y-1.5">
            <label class="font-medium text-slate-300">Password</label>
            <input type="password" [(ngModel)]="password" name="password" required placeholder="At least 6 characters" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
          </div>

          <div class="space-y-1.5">
            <label class="font-medium text-slate-300">City & Street Address (Optional)</label>
            <input type="text" [(ngModel)]="address" name="address" placeholder="789 Pine Street" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
          </div>

          <button 
            type="submit" 
            [disabled]="loading() || !firstName || !lastName || !email || !password"
            class="w-full py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 disabled:opacity-40 text-slate-950 font-bold text-xs shadow-lg shadow-emerald-500/20 active:scale-[0.99] transition-all duration-200 flex items-center justify-center space-x-2">
            @if (loading()) {
              <div class="w-4 h-4 border-2 border-slate-950 border-t-transparent rounded-full animate-spin"></div>
              <span>Registering...</span>
            } @else {
              <span>Create Account</span>
            }
          </button>
        </form>

        <div class="text-center text-xs text-slate-400 pt-2">
          Already have an account? 
          <a routerLink="/login" class="text-emerald-400 font-semibold hover:underline ml-1">Sign in</a>
        </div>
      </div>
    </div>
  `
})
export class RegisterComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  firstName = '';
  lastName = '';
  email = '';
  password = '';
  address = '';
  loading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  onRegister(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    const request: RegisterRequest = {
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      password: this.password,
      address: this.address
    };

    this.authService.register(request).subscribe({
      next: (res) => {
        if (res.success) {
          this.router.navigate(['/']);
        }
        this.loading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to register account. Please verify input.');
        this.loading.set(false);
      }
    });
  }
}
