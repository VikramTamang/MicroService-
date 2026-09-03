import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { CreateOrderRequest, ParentOrder } from '../../models/order.model';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="max-w-4xl mx-auto space-y-8">
      @if (confirmedOrder()) {
        <!-- Order Confirmation Success Card -->
        <div class="glass-card rounded-3xl p-10 text-center space-y-6 border border-emerald-500/40 shadow-2xl shadow-emerald-500/10">
          <div class="w-20 h-20 mx-auto rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center border border-emerald-500/40">
            <svg class="w-10 h-10" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7" />
            </svg>
          </div>

          <div class="space-y-2">
            <span class="text-xs font-semibold text-emerald-400 uppercase tracking-widest">Marketplace Order Confirmed</span>
            <h1 class="text-3xl font-extrabold text-white font-['Outfit']">Thank you for your order!</h1>
            <p class="text-xs text-slate-400">Your order has been split into independent shipments per merchant.</p>
          </div>

          <div class="p-5 rounded-2xl bg-slate-900/90 border border-slate-800 max-w-md mx-auto space-y-3 text-left">
            <div class="flex justify-between text-xs border-b border-slate-800 pb-2">
              <span class="text-slate-400">Order ID:</span>
              <span class="font-mono font-bold text-emerald-400">{{ confirmedOrder()?.orderNumber }}</span>
            </div>
            <div class="flex justify-between text-xs">
              <span class="text-slate-400">Total Amount:</span>
              <span class="font-bold text-white">\${{ confirmedOrder()?.totalAmount?.toFixed(2) }}</span>
            </div>
            <div class="flex justify-between text-xs">
              <span class="text-slate-400">Merchant Shipments:</span>
              <span class="font-semibold text-slate-200">{{ confirmedOrder()?.subOrders?.length || 1 }} split sub-orders</span>
            </div>
          </div>

          <div class="flex justify-center space-x-4 pt-4">
            <a routerLink="/my-orders" class="px-6 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs transition-all shadow-lg shadow-emerald-500/20">
              Track My Orders
            </a>
            <a routerLink="/" class="px-6 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 text-slate-300 font-semibold text-xs border border-slate-800 transition-colors">
              Continue Shopping
            </a>
          </div>
        </div>
      } @else {
        <!-- Checkout Form -->
        <div class="space-y-6">
          <div class="border-b border-slate-800 pb-4">
            <h1 class="text-3xl font-extrabold font-['Outfit'] text-white">Multi-Seller Checkout</h1>
            <p class="text-xs text-slate-400 mt-1">Items from different sellers will be split into independent shipments automatically.</p>
          </div>

          @if (errorMessage()) {
            <div class="p-4 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-400 text-xs flex items-center space-x-2">
              <span>⚠️ {{ errorMessage() }}</span>
            </div>
          }

          <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <!-- Delivery Info Form -->
            <div class="lg:col-span-2 glass-card rounded-2xl p-6 border border-slate-800 space-y-6">
              <h3 class="text-base font-bold text-white flex items-center space-x-2">
                <span class="w-6 h-6 rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center text-xs font-bold">1</span>
                <span>Shipping Destination</span>
              </h3>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
                <div class="sm:col-span-2 space-y-1.5">
                  <label class="font-medium text-slate-300">Street Address</label>
                  <input type="text" [(ngModel)]="shippingAddress" placeholder="123 Innovation Drive" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
                </div>
                <div class="space-y-1.5">
                  <label class="font-medium text-slate-300">City</label>
                  <input type="text" [(ngModel)]="shippingCity" placeholder="San Francisco" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
                </div>
                <div class="space-y-1.5">
                  <label class="font-medium text-slate-300">Postal Code</label>
                  <input type="text" [(ngModel)]="shippingPostalCode" placeholder="94103" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
                </div>
                <div class="sm:col-span-2 space-y-1.5">
                  <label class="font-medium text-slate-300">Phone Number</label>
                  <input type="text" [(ngModel)]="customerPhone" placeholder="+1 (555) 019-2834" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
                </div>
              </div>

              <h3 class="text-base font-bold text-white flex items-center space-x-2 pt-4 border-t border-slate-800">
                <span class="w-6 h-6 rounded-full bg-emerald-500/20 text-emerald-400 flex items-center justify-center text-xs font-bold">2</span>
                <span>Payment Method</span>
              </h3>

              <div class="grid grid-cols-2 gap-4">
                <label class="p-3 rounded-xl border border-emerald-500/50 bg-emerald-500/10 flex items-center space-x-3 cursor-pointer">
                  <input type="radio" name="payment" value="CREDIT_CARD" [(ngModel)]="paymentMethod" class="text-emerald-500" checked />
                  <span class="text-xs font-semibold text-white">Credit Card (Simulated)</span>
                </label>
                <label class="p-3 rounded-xl border border-slate-800 bg-slate-900/50 flex items-center space-x-3 cursor-pointer">
                  <input type="radio" name="payment" value="CASH_ON_DELIVERY" [(ngModel)]="paymentMethod" class="text-emerald-500" />
                  <span class="text-xs font-semibold text-slate-300">Cash on Delivery</span>
                </label>
              </div>
            </div>

            <!-- Summary Column -->
            <div class="lg:col-span-1 space-y-4">
              <div class="glass-card rounded-2xl p-5 border border-slate-800 space-y-4">
                <h4 class="text-sm font-bold text-white font-['Outfit']">Multi-Seller Order Breakdown</h4>
                
                <!-- Grouped Items by Store -->
                <div class="space-y-3 max-h-60 overflow-y-auto pr-1">
                  @for (group of cartService.sellerGroups(); track group.sellerId) {
                    <div class="p-3 rounded-xl bg-slate-900/90 border border-slate-800 space-y-2">
                      <div class="flex items-center justify-between text-xs font-bold text-emerald-400 border-b border-slate-800/80 pb-1.5">
                        <span>🏪 {{ group.sellerStoreName }}</span>
                        <span class="text-slate-400 font-normal">Split \$5.00</span>
                      </div>
                      <div class="space-y-1.5">
                        @for (item of group.items; track item.product.id) {
                          <div class="flex justify-between text-[11px] text-slate-300">
                            <span class="truncate max-w-[150px]">{{ item.quantity }}x {{ item.product.name }}</span>
                            <span class="font-semibold text-white font-['Outfit']">\${{ (item.product.price * item.quantity).toFixed(2) }}</span>
                          </div>
                        }
                      </div>
                    </div>
                  }
                </div>

                <div class="border-t border-slate-800 pt-3 space-y-2 text-xs">
                  <div class="flex justify-between text-slate-400">
                    <span>Items Subtotal</span>
                    <span class="text-white font-semibold">\${{ cartService.subtotal().toFixed(2) }}</span>
                  </div>
                  <div class="flex justify-between text-slate-400">
                    <span>Total Split Delivery</span>
                    <span class="text-white font-semibold">\${{ cartService.totalShipping().toFixed(2) }}</span>
                  </div>
                  <div class="flex justify-between text-sm font-bold text-white pt-2 border-t border-slate-800">
                    <span>Final Amount</span>
                    <span class="text-emerald-400 font-['Outfit'] text-base">\${{ cartService.grandTotal().toFixed(2) }}</span>
                  </div>
                </div>

                <button 
                  (click)="submitOrder()"
                  [disabled]="isSubmitting() || cartService.items().length === 0"
                  class="w-full py-3.5 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-400 hover:from-emerald-400 hover:to-teal-300 disabled:opacity-40 text-slate-950 font-extrabold text-xs shadow-xl shadow-emerald-500/20 active:scale-[0.99] transition-all duration-200 flex items-center justify-center space-x-2">
                  @if (isSubmitting()) {
                    <div class="w-4 h-4 border-2 border-slate-950 border-t-transparent rounded-full animate-spin"></div>
                    <span>Reserving Stock & Placing Order...</span>
                  } @else {
                    <span>Place Marketplace Order Now</span>
                  }
                </button>
              </div>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class CheckoutComponent implements OnInit {
  cartService = inject(CartService);
  private orderService = inject(OrderService);
  private authService = inject(AuthService);
  private router = inject(Router);

  shippingAddress = '';
  shippingCity = '';
  shippingPostalCode = '';
  customerPhone = '';
  paymentMethod = 'CREDIT_CARD';

  isSubmitting = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  confirmedOrder = signal<ParentOrder | null>(null);

  ngOnInit(): void {
    const user = this.authService.currentUser();
    if (user) {
      this.shippingAddress = user.address || '';
      this.shippingCity = user.city || '';
      this.shippingPostalCode = user.postalCode || '';
      this.customerPhone = user.phoneNumber || '';
    }

    if (this.cartService.items().length === 0 && !this.confirmedOrder()) {
      this.router.navigate(['/cart']);
    }
  }

  submitOrder(): void {
    if (!this.shippingAddress.trim() || !this.shippingCity.trim() || !this.shippingPostalCode.trim()) {
      this.errorMessage.set('Please fill in all required shipping address fields.');
      return;
    }

    this.isSubmitting.set(true);
    this.errorMessage.set(null);

    const orderRequest: CreateOrderRequest = {
      userId: this.authService.currentUser()?.id,
      userEmail: this.authService.currentUser()?.email,
      items: this.cartService.items().map(i => ({
        productId: i.product.id,
        quantity: i.quantity
      })),
      shippingAddress: this.shippingAddress,
      shippingCity: this.shippingCity,
      shippingPostalCode: this.shippingPostalCode,
      customerPhone: this.customerPhone,
      paymentMethod: this.paymentMethod
    };

    this.orderService.checkout(orderRequest).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.confirmedOrder.set(res.data);
          this.cartService.clearCart();
        }
        this.isSubmitting.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to place order. Inter-service stock reservation may have encountered an issue.');
        this.isSubmitting.set(false);
      }
    });
  }
}
