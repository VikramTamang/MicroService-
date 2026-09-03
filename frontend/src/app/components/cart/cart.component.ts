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
    <div class="max-w-6xl mx-auto space-y-8">
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <h1 class="text-3xl font-extrabold font-['Outfit'] text-white">Shopping Cart</h1>
          <p class="text-xs text-slate-400 mt-1">
            Items from multiple merchants are automatically grouped into independent store shipments
          </p>
        </div>
        @if (cartService.items().length > 0) {
          <div class="flex items-center space-x-4">
            <a routerLink="/" class="text-xs font-semibold text-emerald-400 hover:text-emerald-300 transition-colors">
              + Add More Products
            </a>
            <button (click)="cartService.clearCart()" class="text-xs text-rose-400 hover:text-rose-300 transition-colors">
              Clear All Items
            </button>
          </div>
        }
      </div>

      @if (cartService.items().length === 0) {
        <div class="glass-card rounded-3xl p-16 text-center space-y-5 border border-slate-800">
          <div class="w-20 h-20 mx-auto rounded-full bg-slate-900 flex items-center justify-center text-slate-500 border border-slate-800 text-3xl">
            🛒
          </div>
          <div class="space-y-1">
            <h3 class="text-xl font-bold text-white">Your cart is currently empty</h3>
            <p class="text-xs text-slate-400 max-w-sm mx-auto">Explore approved marketplace products from verified sellers.</p>
          </div>
          <div class="pt-2">
            <a routerLink="/" class="inline-flex items-center px-6 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs transition-all shadow-lg shadow-emerald-500/20">
              Browse Marketplace
            </a>
          </div>
        </div>
      } @else {
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <!-- Multi-Seller Grouped Items List -->
          <div class="lg:col-span-2 space-y-6">
            @for (group of cartService.sellerGroups(); track group.sellerId) {
              <div class="glass-card rounded-3xl overflow-hidden border border-slate-800/80 shadow-xl space-y-4 p-5 sm:p-6">
                <!-- Merchant Store Header -->
                <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-4 border-b border-slate-800">
                  <div class="flex items-center space-x-3">
                    <div class="w-9 h-9 rounded-xl bg-slate-900 border border-slate-700 flex items-center justify-center text-base">
                      🏪
                    </div>
                    <div>
                      <h3 class="font-bold text-sm text-white">{{ group.sellerStoreName }}</h3>
                      <span class="text-[10px] text-emerald-400 font-medium">Independent Merchant Package</span>
                    </div>
                  </div>
                  <div class="flex items-center space-x-2">
                    <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-slate-900 text-slate-300 border border-slate-700">
                      Standard Split Shipping: \$5.00
                    </span>
                  </div>
                </div>

                <!-- Products in this Store -->
                <div class="divide-y divide-slate-800/60">
                  @for (item of group.items; track item.product.id) {
                    <div class="py-4 first:pt-0 last:pb-0 flex flex-col sm:flex-row items-center justify-between gap-4">
                      <!-- Product Info -->
                      <div class="flex items-center space-x-4 w-full sm:w-auto">
                        <div class="w-16 h-16 rounded-xl bg-slate-900 overflow-hidden flex-shrink-0 border border-slate-800 flex items-center justify-center">
                          @if (item.product.imageUrl) {
                            <img [src]="item.product.imageUrl" [alt]="item.product.name" class="w-full h-full object-contain p-1" />
                          } @else {
                            <span class="text-slate-600 text-xs">No Image</span>
                          }
                        </div>
                        <div class="space-y-0.5">
                          <h4 class="font-bold text-sm text-slate-200 line-clamp-1">{{ item.product.name }}</h4>
                          <p class="text-xs text-emerald-400 font-semibold font-['Outfit']">\${{ item.product.price.toFixed(2) }}</p>
                          <p class="text-[10px] text-slate-500">Available: {{ item.product.stockQuantity }} units</p>
                        </div>
                      </div>

                      <!-- Quantity Controls & Item Subtotal -->
                      <div class="flex items-center justify-between sm:justify-end space-x-6 w-full sm:w-auto">
                        <div class="flex items-center space-x-2 bg-slate-900 rounded-xl border border-slate-800 p-1">
                          <button 
                            (click)="cartService.updateQuantity(item.product.id, item.quantity - 1)" 
                            class="w-7 h-7 rounded-lg bg-slate-800 text-slate-300 hover:text-white flex items-center justify-center text-xs font-bold transition-colors">
                            -
                          </button>
                          <span class="text-xs font-bold px-2 text-white font-['Outfit']">{{ item.quantity }}</span>
                          <button 
                            (click)="cartService.updateQuantity(item.product.id, item.quantity + 1)" 
                            [disabled]="item.quantity >= item.product.stockQuantity"
                            class="w-7 h-7 rounded-lg bg-slate-800 disabled:opacity-40 text-slate-300 hover:text-white flex items-center justify-center text-xs font-bold transition-colors">
                            +
                          </button>
                        </div>

                        <div class="text-right">
                          <p class="text-sm font-bold text-white font-['Outfit']">
                            \${{ (item.product.price * item.quantity).toFixed(2) }}
                          </p>
                          <button (click)="cartService.removeFromCart(item.product.id)" class="text-[10px] text-slate-500 hover:text-rose-400 transition-colors mt-0.5">
                            Remove
                          </button>
                        </div>
                      </div>
                    </div>
                  }
                </div>

                <!-- Store Subtotal Footer -->
                <div class="pt-3 border-t border-slate-800/80 flex justify-between items-center text-xs text-slate-400">
                  <span>Store Package Subtotal ({{ group.items.length }} items):</span>
                  <span class="font-bold text-white font-['Outfit']">\${{ group.subtotal.toFixed(2) }}</span>
                </div>
              </div>
            }
          </div>

          <!-- Order Summary Sidebar -->
          <div class="lg:col-span-1">
            <div class="glass-card rounded-3xl p-6 border border-slate-800 space-y-6 sticky top-24 shadow-2xl">
              <h3 class="text-base font-bold text-white font-['Outfit'] border-b border-slate-800 pb-3">
                Marketplace Order Summary
              </h3>

              <div class="space-y-3 text-xs text-slate-300">
                <div class="flex justify-between">
                  <span class="text-slate-400">Total Items ({{ cartService.itemCount() }})</span>
                  <span class="font-semibold text-white">\${{ cartService.subtotal().toFixed(2) }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-slate-400">
                    Split Shipping ({{ cartService.sellerGroups().length }} store{{ cartService.sellerGroups().length > 1 ? 's' : '' }})
                  </span>
                  <span class="font-semibold text-emerald-400 font-['Outfit']">\${{ cartService.totalShipping().toFixed(2) }}</span>
                </div>
                <div class="flex justify-between text-[11px] text-slate-500 italic">
                  <span>Standard \$5.00 flat delivery per independent merchant package</span>
                </div>
                <div class="border-t border-slate-800 pt-3 flex justify-between text-base font-bold text-white">
                  <span>Final Total</span>
                  <span class="text-emerald-400 font-['Outfit'] text-lg">\${{ cartService.grandTotal().toFixed(2) }}</span>
                </div>
              </div>

              <button 
                (click)="proceedToCheckout()"
                class="w-full py-3.5 rounded-2xl bg-gradient-to-r from-emerald-500 to-teal-400 hover:from-emerald-400 hover:to-teal-300 text-slate-950 font-extrabold text-xs shadow-xl shadow-emerald-500/20 active:scale-[0.99] transition-all duration-200">
                Proceed to Multi-Seller Checkout →
              </button>

              <div class="pt-2 text-center space-y-2">
                <span class="text-[10px] text-slate-500 flex items-center justify-center space-x-1.5">
                  <svg class="w-3.5 h-3.5 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                  </svg>
                  <span>Protected by Spring Cloud API Gateway</span>
                </span>
                <p class="text-[10px] text-slate-600">
                  Each merchant independently fulfills and dispatches their portion with unique tracking codes.
                </p>
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

