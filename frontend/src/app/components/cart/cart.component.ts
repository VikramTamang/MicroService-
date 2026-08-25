import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="max-w-5xl mx-auto space-y-8">
      <div class="flex items-center justify-between border-b border-slate-800 pb-6">
        <div>
          <h1 class="text-3xl font-extrabold font-['Outfit'] text-white">Shopping Cart</h1>
          <p class="text-xs text-slate-400 mt-1">Review your selected microservice catalog items before checkout</p>
        </div>
        @if (cartService.items().length > 0) {
          <button (click)="cartService.clearCart()" class="text-xs text-rose-400 hover:text-rose-300 transition-colors">
            Clear all items
          </button>
        }
      </div>

      @if (cartService.items().length === 0) {
        <div class="glass-card rounded-3xl p-16 text-center space-y-5 border border-slate-800">
          <div class="w-20 h-20 mx-auto rounded-full bg-slate-900 flex items-center justify-center text-slate-500 border border-slate-800">
            <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </div>
          <div class="space-y-1">
            <h3 class="text-lg font-bold text-white">Your cart is empty</h3>
            <p class="text-xs text-slate-400 max-w-sm mx-auto">Explore our high-performance hardware and accessories in the catalog.</p>
          </div>
          <div>
            <a routerLink="/" class="inline-flex items-center px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs transition-all shadow-lg shadow-emerald-500/20">
              Browse Products
            </a>
          </div>
        </div>
      } @else {
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <!-- Items List -->
          <div class="lg:col-span-2 space-y-4">
            @for (item of cartService.items(); track item.product.id) {
              <div class="glass-card rounded-2xl p-4 flex flex-col sm:flex-row items-center justify-between gap-4 border border-slate-800">
                <div class="flex items-center space-x-4 w-full sm:w-auto">
                  <div class="w-20 h-20 rounded-xl bg-slate-900 overflow-hidden flex-shrink-0 border border-slate-800">
                    @if (item.product.imageUrl) {
                      <img [src]="item.product.imageUrl" [alt]="item.product.name" class="w-full h-full object-cover" />
                    } @else {
                      <div class="w-full h-full flex items-center justify-center text-slate-600">
                        <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                      </div>
                    }
                  </div>
                  <div>
                    <h4 class="font-bold text-sm text-slate-200">{{ item.product.name }}</h4>
                    <p class="text-xs text-emerald-400 font-semibold mt-1 font-['Outfit']">\${{ item.product.price.toFixed(2) }}</p>
                    <p class="text-[10px] text-slate-500 mt-0.5">Available: {{ item.product.stockQuantity }} units</p>
                  </div>
                </div>

                <!-- Quantity Controls & Subtotal -->
                <div class="flex items-center justify-between sm:justify-end space-x-6 w-full sm:w-auto">
                  <div class="flex items-center space-x-2 bg-slate-900 rounded-xl border border-slate-800 p-1">
                    <button 
                      (click)="cartService.updateQuantity(item.product.id, item.quantity - 1)" 
                      class="w-7 h-7 rounded-lg bg-slate-800 text-slate-300 hover:text-white flex items-center justify-center text-xs font-bold transition-colors">
                      -
                    </button>
                    <span class="text-xs font-semibold px-2 text-white">{{ item.quantity }}</span>
                    <button 
                      (click)="cartService.updateQuantity(item.product.id, item.quantity + 1)" 
                      [disabled]="item.quantity >= item.product.stockQuantity"
                      class="w-7 h-7 rounded-lg bg-slate-800 disabled:opacity-40 text-slate-300 hover:text-white flex items-center justify-center text-xs font-bold transition-colors">
                      +
                    </button>
                  </div>

                  <div class="text-right">
                    <p class="text-sm font-bold text-white font-['Outfit']">\${{ (item.product.price * item.quantity).toFixed(2) }}</p>
                    <button (click)="cartService.removeFromCart(item.product.id)" class="text-[10px] text-slate-500 hover:text-rose-400 transition-colors mt-0.5">
                      Remove
                    </button>
                  </div>
                </div>
              </div>
            }
          </div>

          <!-- Order Summary Card -->
          <div class="lg:col-span-1">
            <div class="glass-card rounded-2xl p-6 border border-slate-800 space-y-6 sticky top-24">
              <h3 class="text-base font-bold text-white font-['Outfit']">Order Summary</h3>

              <div class="space-y-3 text-xs text-slate-300">
                <div class="flex justify-between">
                  <span class="text-slate-400">Subtotal</span>
                  <span class="font-semibold text-white">\${{ cartService.subtotal().toFixed(2) }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-400">Estimated Shipping</span>
                  <span class="font-semibold text-emerald-400">FREE</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-400">Taxes (Calculated)</span>
                  <span class="font-semibold text-white">\${{ (cartService.subtotal() * 0.08).toFixed(2) }}</span>
                </div>
                <div class="border-t border-slate-800 pt-3 flex justify-between text-sm font-bold text-white">
                  <span>Total Amount</span>
                  <span class="text-emerald-400 font-['Outfit']">\${{ (cartService.subtotal() * 1.08).toFixed(2) }}</span>
                </div>
              </div>

              <button 
                (click)="proceedToCheckout()"
                class="w-full py-3 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-400 hover:from-emerald-400 hover:to-teal-300 text-slate-950 font-bold text-xs shadow-lg shadow-emerald-500/20 active:scale-[0.99] transition-all duration-200">
                Proceed to Checkout
              </button>

              <div class="pt-2 text-center">
                <span class="text-[10px] text-slate-500 flex items-center justify-center space-x-1.5">
                  <svg class="w-3.5 h-3.5 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                  </svg>
                  <span>Protected by Spring Cloud API Gateway</span>
                </span>
              </div>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class CartComponent {
  cartService = inject(CartService);
  authService = inject(AuthService);
  private router = inject(Router);

  proceedToCheckout(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/checkout' } });
    } else {
      this.router.navigate(['/checkout']);
    }
  }
}
