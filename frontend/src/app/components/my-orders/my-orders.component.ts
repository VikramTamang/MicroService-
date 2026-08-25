import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { Order, OrderStatus } from '../../models/order.model';

@Component({
  selector: 'app-my-orders',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="max-w-5xl mx-auto space-y-8">
      <div class="border-b border-slate-800 pb-6 flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-extrabold font-['Outfit'] text-white">My Orders</h1>
          <p class="text-xs text-slate-400 mt-1">Track your past purchases and delivery statuses</p>
        </div>
        <a routerLink="/" class="px-4 py-2 rounded-xl bg-slate-900 border border-slate-800 text-xs font-semibold text-slate-300 hover:text-white hover:bg-slate-800 transition-colors">
          Browse Catalog
        </a>
      </div>

      @if (loading()) {
        <div class="space-y-4">
          @for (i of [1,2,3]; track i) {
            <div class="glass-card rounded-2xl p-6 animate-pulse space-y-4">
              <div class="h-4 bg-slate-800 rounded w-1/4"></div>
              <div class="h-10 bg-slate-800 rounded"></div>
            </div>
          }
        </div>
      } @else if (orders().length === 0) {
        <div class="glass-card rounded-3xl p-16 text-center space-y-4 border border-slate-800">
          <div class="w-16 h-16 mx-auto rounded-full bg-slate-900 flex items-center justify-center text-slate-500">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <h3 class="text-lg font-bold text-white">No orders yet</h3>
          <p class="text-xs text-slate-400 max-w-sm mx-auto">When you purchase items through our microservices gateway, they will show up here.</p>
          <div class="pt-2">
            <a routerLink="/" class="px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs shadow-md transition-all">
              Start Shopping
            </a>
          </div>
        </div>
      } @else {
        <div class="space-y-4">
          @for (order of orders(); track order.id) {
            <div class="glass-card rounded-2xl p-6 border border-slate-800 space-y-4">
              <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 pb-4 border-b border-slate-800/80">
                <div>
                  <div class="flex items-center space-x-3">
                    <span class="font-mono text-sm font-bold text-white">{{ order.trackingNumber }}</span>
                    <span [class]="getStatusClass(order.status)" class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border">
                      {{ order.status }}
                    </span>
                  </div>
                  <p class="text-[11px] text-slate-500 mt-1">Placed on {{ order.createdAt | date:'medium' }}</p>
                </div>
                <div class="text-right">
                  <span class="text-xs text-slate-400 block">Total</span>
                  <span class="text-base font-bold text-emerald-400 font-['Outfit']">\${{ order.totalAmount.toFixed(2) }}</span>
                </div>
              </div>

              <!-- Item Breakdown -->
              <div class="space-y-2">
                <p class="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Ordered Items</p>
                <div class="grid grid-cols-1 md:grid-cols-2 gap-2">
                  @for (item of order.items; track item.id) {
                    <div class="p-2.5 rounded-xl bg-slate-900/60 border border-slate-800/60 flex justify-between items-center text-xs">
                      <div>
                        <span class="font-medium text-slate-200">{{ item.productName }}</span>
                        <span class="text-[10px] text-slate-500 block">Qty: {{ item.quantity }} × \${{ item.unitPrice.toFixed(2) }}</span>
                      </div>
                      <span class="font-semibold text-white font-['Outfit']">\${{ item.subtotal.toFixed(2) }}</span>
                    </div>
                  }
                </div>
              </div>

              <!-- Shipping Info -->
              <div class="pt-2 flex flex-col sm:flex-row justify-between text-[11px] text-slate-400 border-t border-slate-800/40">
                <span>Shipping: {{ order.shippingAddress }}, {{ order.shippingCity }} {{ order.shippingPostalCode }}</span>
                <span>Payment: {{ order.paymentMethod }}</span>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `
})
export class MyOrdersComponent implements OnInit {
  private orderService = inject(OrderService);

  orders = signal<Order[]>([]);
  loading = signal<boolean>(true);

  ngOnInit(): void {
    this.orderService.getMyOrders().subscribe({
      next: (res) => {
        if (res.success) {
          this.orders.set(res.data);
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load orders:', err);
        this.loading.set(false);
      }
    });
  }

  getStatusClass(status: OrderStatus): string {
    switch (status) {
      case 'CONFIRMED':
        return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'PROCESSING':
        return 'bg-cyan-500/10 text-cyan-400 border-cyan-500/30';
      case 'SHIPPED':
        return 'bg-blue-500/10 text-blue-400 border-blue-500/30';
      case 'DELIVERED':
        return 'bg-teal-500/10 text-teal-400 border-teal-500/30';
      case 'CANCELLED':
        return 'bg-rose-500/10 text-rose-400 border-rose-500/30';
      default:
        return 'bg-slate-500/10 text-slate-400 border-slate-500/30';
    }
  }
}
