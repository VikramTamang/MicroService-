import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../services/order.service';
import { ParentOrder, SubOrderStatus } from '../../models/order.model';

@Component({
  selector: 'app-my-orders',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="max-w-5xl mx-auto space-y-8">
      <div class="border-b border-slate-800 pb-6 flex items-center justify-between">
        <div>
          <h1 class="text-3xl font-extrabold font-['Outfit'] text-white">My Orders</h1>
          <p class="text-xs text-slate-400 mt-1">Track your marketplace purchases and independent merchant shipments</p>
        </div>
        <a routerLink="/" class="px-4 py-2 rounded-xl bg-slate-900 border border-slate-800 text-xs font-semibold text-slate-300 hover:text-white hover:bg-slate-800 transition-colors">
          Browse Marketplace
        </a>
      </div>

      @if (alertMessage()) {
        <div class="p-3.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs flex items-center justify-between">
          <span>{{ alertMessage() }}</span>
          <button (click)="alertMessage.set(null)" class="text-slate-400 hover:text-white">&times;</button>
        </div>
      }

      @if (loading()) {
        <div class="space-y-4">
          @for (i of [1,2,3]; track i) {
            <div class="glass-card rounded-2xl p-6 animate-pulse space-y-4">
              <div class="h-4 bg-slate-800 rounded w-1/4"></div>
              <div class="h-10 bg-slate-800 rounded"></div>
            </div>
          }
        </div>
      } @else if (parentOrders().length === 0) {
        <div class="glass-card rounded-3xl p-16 text-center space-y-4 border border-slate-800">
          <div class="w-16 h-16 mx-auto rounded-full bg-slate-900 flex items-center justify-center text-slate-500 text-2xl">
            📦
          </div>
          <h3 class="text-lg font-bold text-white">No marketplace orders yet</h3>
          <p class="text-xs text-slate-400 max-w-sm mx-auto">When you purchase items through our multi-seller gateway, they will show up here.</p>
          <div class="pt-2">
            <a routerLink="/" class="px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs shadow-md transition-all">
              Start Shopping
            </a>
          </div>
        </div>
      } @else {
        <div class="space-y-6">
          @for (order of parentOrders(); track order.id) {
            <div class="glass-card rounded-3xl p-6 border border-slate-800 space-y-5 shadow-xl">
              <!-- Parent Order Header -->
              <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-4 border-b border-slate-800">
                <div>
                  <div class="flex items-center space-x-3">
                    <span class="font-mono text-sm font-bold text-emerald-400">{{ order.orderNumber }}</span>
                    <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-indigo-500/10 text-indigo-400 border border-indigo-500/30">
                      {{ order.derivedStatus }}
                    </span>
                  </div>
                  <p class="text-[11px] text-slate-500 mt-1">Placed on {{ order.createdAt | date:'medium' }}</p>
                </div>
                <div class="text-right">
                  <span class="text-xs text-slate-400 block">Total Paid</span>
                  <span class="text-base font-bold text-white font-['Outfit']">\${{ order.totalAmount.toFixed(2) }}</span>
                </div>
              </div>

              <!-- Sub-Orders Breakdown (Per Seller Shipment) -->
              <div class="space-y-4">
                <p class="text-xs font-bold text-slate-400 uppercase tracking-wider">
                  Merchant Shipments ({{ order.subOrders.length || 1 }})
                </p>

                <div class="grid grid-cols-1 gap-4">
                  @for (sub of order.subOrders; track sub.id) {
                    <div class="p-4 rounded-2xl bg-slate-900/80 border border-slate-800 space-y-3">
                      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800/60 pb-2.5">
                        <div class="flex items-center space-x-2">
                          <span class="text-base">🏪</span>
                          <div>
                            <span class="text-xs font-bold text-white">{{ sub.sellerStoreName || 'Apex Electronics Store' }}</span>
                            <span class="block font-mono text-[10px] text-slate-500">{{ sub.subOrderNumber }}</span>
                          </div>
                        </div>
                        <div class="flex items-center space-x-3">
                          <span [class]="getSubStatusClass(sub.status)" class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border">
                            {{ sub.status }}
                          </span>
                          @if (sub.status === 'PLACED') {
                            <button (click)="cancelSubOrder(sub.subOrderNumber)" class="px-2 py-1 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 border border-rose-500/30 text-[10px] font-semibold">
                              Cancel Shipment
                            </button>
                          }
                        </div>
                      </div>

                      <!-- Sub Order Items -->
                      <div class="space-y-1.5">
                        @for (item of sub.items; track item.id) {
                          <div class="flex justify-between items-center text-xs text-slate-300">
                            <div>
                              <span class="font-medium text-slate-200">{{ item.productName }}</span>
                              <span class="text-[10px] text-slate-500 block">Qty: {{ item.quantity }} × \${{ item.unitPrice.toFixed(2) }}</span>
                            </div>
                            <span class="font-semibold text-white font-['Outfit']">\${{ item.subtotal.toFixed(2) }}</span>
                          </div>
                        }
                      </div>

                      <!-- Tracking / Dispatch Info -->
                      @if (sub.trackingCode) {
                        <div class="p-2.5 rounded-xl bg-slate-950 border border-slate-800/80 flex items-center justify-between text-xs">
                          <div class="flex items-center space-x-2">
                            <span class="text-indigo-400 font-bold">🚚 {{ sub.carrier }}</span>
                            <span class="font-mono text-emerald-400 text-[11px]">{{ sub.trackingCode }}</span>
                          </div>
                          <span class="text-[10px] text-slate-400">Shipped</span>
                        </div>
                      }
                    </div>
                  }
                </div>
              </div>

              <!-- Shipping Info Footer -->
              <div class="pt-2 flex flex-col sm:flex-row justify-between text-[11px] text-slate-500 border-t border-slate-800/60">
                <span>Destination: {{ order.shippingAddress }}, {{ order.shippingCity }} {{ order.shippingPostalCode }}</span>
                <span>Payment: {{ order.paymentMethod }} ({{ order.paymentStatus }})</span>
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

  parentOrders = signal<ParentOrder[]>([]);
  loading = signal<boolean>(true);
  alertMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getMyParentOrders(0, 50).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.parentOrders.set(res.data.content);
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load orders:', err);
        this.loading.set(false);
      }
    });
  }

  cancelSubOrder(subOrderNumber: string): void {
    if (confirm('Cancel this merchant shipment?')) {
      this.orderService.cancelSubOrderAsCustomer(subOrderNumber, 'Customer requested cancellation').subscribe({
        next: () => {
          this.alertMessage.set(`Sub-order ${subOrderNumber} cancelled successfully.`);
          this.loadOrders();
        },
        error: (err) => alert(err.error?.message || 'Failed to cancel sub-order')
      });
    }
  }

  getSubStatusClass(status: SubOrderStatus): string {
    switch (status) {
      case 'PLACED': return 'bg-blue-500/10 text-blue-400 border-blue-500/30';
      case 'CONFIRMED': return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'PACKED': return 'bg-cyan-500/10 text-cyan-400 border-cyan-500/30';
      case 'SHIPPED': return 'bg-indigo-500/10 text-indigo-400 border-indigo-500/30';
      case 'DELIVERED': return 'bg-teal-500/20 text-teal-300 border-teal-500/40';
      case 'CANCELLED': return 'bg-rose-500/10 text-rose-400 border-rose-500/30';
      default: return 'bg-slate-800 text-slate-400 border-slate-700';
    }
  }
}
