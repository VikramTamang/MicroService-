import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { SellerService } from '../../services/seller.service';
import { OrderService } from '../../services/order.service';
import { Product, ProductAuditLog } from '../../models/product.model';
import { SellerProfile } from '../../models/user.model';
import { ParentOrder } from '../../models/order.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-8 max-w-7xl mx-auto">
      <!-- Admin Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <div class="flex items-center space-x-2">
            <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-purple-500/20 text-purple-400 border border-purple-500/30">MARKETPLACE MODERATION</span>
            <h1 class="text-3xl font-extrabold font-['Outfit'] text-white">Trust & Compliance Console</h1>
          </div>
          <p class="text-xs text-slate-400 mt-1">Review merchant product listings, verify seller onboarding applications, and inspect order disputes</p>
        </div>

        <!-- Tab Controls -->
        <div class="flex items-center space-x-2 bg-slate-900/90 p-1.5 rounded-xl border border-slate-800">
          <button 
            (click)="activeTab = 'product_review'" 
            [class]="activeTab === 'product_review' ? 'bg-purple-600 text-white font-bold' : 'text-slate-400 hover:text-white'"
            class="px-4 py-2 rounded-lg text-xs transition-all duration-200">
            Product Approvals ({{ pendingProducts().length }})
          </button>
          <button 
            (click)="activeTab = 'catalog_inventory'" 
            [class]="activeTab === 'catalog_inventory' ? 'bg-purple-600 text-white font-bold' : 'text-slate-400 hover:text-white'"
            class="px-4 py-2 rounded-lg text-xs transition-all duration-200">
            All Products & Dummy Data ({{ allProducts().length }})
          </button>
          <button 
            (click)="activeTab = 'seller_moderation'" 
            [class]="activeTab === 'seller_moderation' ? 'bg-purple-600 text-white font-bold' : 'text-slate-400 hover:text-white'"
            class="px-4 py-2 rounded-lg text-xs transition-all duration-200">
            Seller Verifications ({{ pendingSellers().length }})
          </button>
          <button 
            (click)="activeTab = 'platform_orders'" 
            [class]="activeTab === 'platform_orders' ? 'bg-purple-600 text-white font-bold' : 'text-slate-400 hover:text-white'"
            class="px-4 py-2 rounded-lg text-xs transition-all duration-200">
            Platform Orders (View-Only)
          </button>
        </div>
      </div>

      <!-- Alert -->
      @if (alertMessage()) {
        <div class="p-3.5 rounded-xl bg-purple-500/10 border border-purple-500/30 text-purple-300 text-xs flex items-center justify-between">
          <span>{{ alertMessage() }}</span>
          <button (click)="alertMessage.set(null)" class="text-slate-400 hover:text-white">&times;</button>
        </div>
      }

      <!-- TAB 1: PRODUCT APPROVALS -->
      @if (activeTab === 'product_review') {
        <div class="space-y-6">
          <div class="flex justify-between items-center">
            <div>
              <h3 class="text-lg font-bold text-white">Pending Product Submissions</h3>
              <p class="text-xs text-slate-400">Review specifications, image quality, and pricing compliance before publishing to the catalog.</p>
            </div>
          </div>

          <div class="glass-card rounded-2xl overflow-hidden border border-slate-800">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/80 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">Product</th>
                    <th class="p-4">Seller ID</th>
                    <th class="p-4">Price</th>
                    <th class="p-4">Stock</th>
                    <th class="p-4">Submission Date</th>
                    <th class="p-4 text-right">Moderation Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/60">
                  @for (p of pendingProducts(); track p.id) {
                    <tr class="hover:bg-slate-800/30 transition-colors">
                      <td class="p-4 font-medium text-white flex items-center space-x-3">
                        <div class="w-12 h-12 rounded-xl bg-slate-800 overflow-hidden flex-shrink-0 border border-slate-700">
                          @if (p.imageUrl) {
                            <img [src]="p.imageUrl" [alt]="p.name" class="w-full h-full object-cover" />
                          }
                        </div>
                        <div>
                          <span class="block font-bold text-sm">{{ p.name }}</span>
                          <span class="text-[11px] text-slate-400 line-clamp-1">{{ p.description }}</span>
                        </div>
                      </td>
                      <td class="p-4 font-mono text-emerald-400">Seller #{{ p.sellerId }}</td>
                      <td class="p-4 font-bold text-white font-['Outfit']">\${{ p.price.toFixed(2) }}</td>
                      <td class="p-4">{{ p.stockQuantity }} units</td>
                      <td class="p-4 text-slate-500">{{ p.createdAt | date:'short' }}</td>
                      <td class="p-4 text-right space-x-2">
                        <button (click)="approveProduct(p.id)" class="px-3.5 py-1.5 rounded-lg bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs shadow-md shadow-emerald-500/20">
                          Approve ✅
                        </button>
                        <button (click)="openRejectModal(p.id)" class="px-3.5 py-1.5 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 border border-rose-500/30 font-bold text-xs">
                          Reject ❌
                        </button>
                        <button (click)="viewAuditLogs(p.id)" class="px-2.5 py-1.5 rounded-lg bg-slate-850 hover:bg-slate-800 text-slate-300 text-xs border border-slate-700">
                          Audit
                        </button>
                        <button (click)="deleteProduct(p.id)" class="px-2.5 py-1.5 rounded-lg bg-rose-950/60 hover:bg-rose-900/80 text-rose-300 text-xs border border-rose-800" title="Delete listing permanently">
                          🗑️
                        </button>
                      </td>
                    </tr>
                  }
                  @if (pendingProducts().length === 0) {
                    <tr>
                      <td colspan="6" class="p-8 text-center text-slate-500">
                        🎉 All product listings are reviewed! No pending submissions.
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- TAB: CATALOG INVENTORY & DUMMY DATA PURGE -->
      @if (activeTab === 'catalog_inventory') {
        <div class="space-y-6">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h3 class="text-lg font-bold text-white">Marketplace Catalog Inventory</h3>
              <p class="text-xs text-slate-400">View all products across active, pending, and suspended states. Delete or purge dummy test data permanently.</p>
            </div>
            <div class="flex items-center space-x-3">
              <input 
                type="text" 
                [(ngModel)]="searchQuery" 
                placeholder="Search products..." 
                class="px-3.5 py-1.5 rounded-xl bg-slate-900 border border-slate-800 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500" />
              <button (click)="loadData()" class="px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-xs font-semibold text-slate-300 transition-colors">
                Refresh 🔄
              </button>
            </div>
          </div>

          <div class="glass-card rounded-2xl overflow-hidden border border-slate-800">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/80 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">ID</th>
                    <th class="p-4">Product Details</th>
                    <th class="p-4">Seller ID</th>
                    <th class="p-4">Category</th>
                    <th class="p-4">Price / Stock</th>
                    <th class="p-4">Lifecycle Status</th>
                    <th class="p-4 text-right">Action</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/60">
                  @for (p of filteredAllProducts(); track p.id) {
                    <tr class="hover:bg-slate-800/30 transition-colors">
                      <td class="p-4 font-mono text-slate-500">#{{ p.id }}</td>
                      <td class="p-4 font-medium text-white flex items-center space-x-3">
                        <div class="w-10 h-10 rounded-xl bg-slate-800 overflow-hidden flex-shrink-0 border border-slate-700">
                          @if (p.imageUrl) {
                            <img [src]="p.imageUrl" [alt]="p.name" class="w-full h-full object-cover" />
                          }
                        </div>
                        <div>
                          <span class="block font-bold text-sm text-white">{{ p.name }}</span>
                          <span class="text-[11px] text-slate-400 line-clamp-1">{{ p.description }}</span>
                        </div>
                      </td>
                      <td class="p-4 font-mono text-purple-400">Seller #{{ p.sellerId }}</td>
                      <td class="p-4 text-slate-300">{{ p.categoryName || 'General' }}</td>
                      <td class="p-4">
                        <span class="font-bold text-white font-['Outfit']">\${{ p.price.toFixed(2) }}</span>
                        <span class="block text-[10px] text-slate-400">{{ p.stockQuantity }} in stock</span>
                      </td>
                      <td class="p-4">
                        <span [class]="getProductStatusClass(p.status)" class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border">
                          {{ p.status }}
                        </span>
                      </td>
                      <td class="p-4 text-right space-x-2">
                        <button (click)="viewAuditLogs(p.id)" class="px-2.5 py-1.5 rounded-lg bg-slate-850 hover:bg-slate-800 text-slate-300 text-xs border border-slate-700">
                          Audit
                        </button>
                        <button (click)="deleteProduct(p.id)" class="px-3 py-1.5 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 border border-rose-500/30 text-xs font-bold transition-colors">
                          Delete 🗑️
                        </button>
                      </td>
                    </tr>
                  }
                  @if (filteredAllProducts().length === 0) {
                    <tr>
                      <td colspan="7" class="p-8 text-center text-slate-500">
                        No products found.
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- TAB 2: SELLER MODERATION -->
      @if (activeTab === 'seller_moderation') {
        <div class="space-y-6">
          <div class="flex justify-between items-center">
            <div>
              <h3 class="text-lg font-bold text-white">Seller Onboarding Applications</h3>
              <p class="text-xs text-slate-400">Verify business credentials, tax IDs, and approve merchant storefronts.</p>
            </div>
          </div>

          <div class="glass-card rounded-2xl overflow-hidden border border-slate-800">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/80 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">Store Name</th>
                    <th class="p-4">Business Reg #</th>
                    <th class="p-4">Tax ID</th>
                    <th class="p-4">Status</th>
                    <th class="p-4 text-right">Verification Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/60">
                  @for (s of allSellers(); track s.id) {
                    <tr class="hover:bg-slate-800/30 transition-colors">
                      <td class="p-4 font-bold text-white">
                        {{ s.storeName }}
                        <span class="block text-[10px] text-slate-500 font-mono">{{ s.storeSlug }}</span>
                      </td>
                      <td class="p-4 font-mono text-slate-300">{{ s.businessRegistrationNumber || 'N/A' }}</td>
                      <td class="p-4 font-mono text-slate-300">{{ s.taxIdentificationNumber || 'N/A' }}</td>
                      <td class="p-4">
                        <span [class]="getSellerStatusClass(s.verificationStatus)" class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border">
                          {{ s.verificationStatus }}
                        </span>
                      </td>
                      <td class="p-4 text-right space-x-2">
                        @if (s.verificationStatus === 'PENDING') {
                          <button (click)="approveSeller(s.id)" class="px-3 py-1.5 rounded-lg bg-emerald-500 text-slate-950 font-bold text-xs">
                            Approve
                          </button>
                          <button (click)="rejectSeller(s.id)" class="px-3 py-1.5 rounded-lg bg-rose-500/10 text-rose-400 border border-rose-500/30 text-xs">
                            Reject
                          </button>
                        } @else if (s.verificationStatus === 'APPROVED') {
                          <button (click)="suspendSeller(s.id)" class="px-3 py-1.5 rounded-lg bg-amber-500/10 text-amber-400 border border-amber-500/30 text-xs">
                            Suspend
                          </button>
                        } @else {
                          <button (click)="approveSeller(s.id)" class="px-3 py-1.5 rounded-lg bg-slate-800 text-slate-300 text-xs">
                            Reactivate
                          </button>
                        }
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- TAB 3: PLATFORM ORDERS (VIEW ONLY) -->
      @if (activeTab === 'platform_orders') {
        <div class="space-y-6">
          <div class="flex justify-between items-center">
            <div>
              <h3 class="text-lg font-bold text-white">Platform Orders (View-Only / Dispute Audit)</h3>
              <p class="text-xs text-slate-400">Administrators have view-only access to investigate customer-seller dispute resolutions.</p>
            </div>
          </div>

          <div class="space-y-4">
            @for (order of platformOrders(); track order.id) {
              <div class="glass-card rounded-2xl p-5 border border-slate-800 space-y-4">
                <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800/80 pb-3">
                  <div class="flex items-center space-x-3">
                    <span class="font-mono font-bold text-emerald-400 text-sm">{{ order.orderNumber }}</span>
                    <span class="text-xs text-slate-400">Customer: {{ order.customerEmail }}</span>
                  </div>
                  <div class="flex items-center space-x-3">
                    <span class="font-bold text-white font-['Outfit']">\${{ order.totalAmount.toFixed(2) }}</span>
                    <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-indigo-500/10 text-indigo-400 border border-indigo-500/30">
                      {{ order.derivedStatus }}
                    </span>
                  </div>
                </div>

                <!-- Sub-Orders Breakdown -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
                  @for (sub of order.subOrders; track sub.id) {
                    <div class="bg-slate-900/80 rounded-xl p-3.5 border border-slate-800 space-y-2">
                      <div class="flex justify-between items-center text-xs">
                        <span class="font-bold text-white">Shipment (Seller #{{ sub.sellerId }})</span>
                        <span class="px-2 py-0.5 rounded text-[10px] font-bold bg-slate-800 text-slate-300">
                          {{ sub.status }}
                        </span>
                      </div>
                      <div class="text-[11px] text-slate-400 font-mono">
                        {{ sub.subOrderNumber }}
                      </div>
                      <div class="text-xs text-slate-300">
                        @for (i of sub.items; track i.id) {
                          <div>{{ i.quantity }}x {{ i.productName }} (\${{ i.subtotal.toFixed(2) }})</div>
                        }
                      </div>
                      @if (sub.trackingCode) {
                        <div class="text-[10px] text-emerald-400 font-mono">
                          🚚 {{ sub.carrier }}: {{ sub.trackingCode }}
                        </div>
                      }
                    </div>
                  }
                </div>
              </div>
            }
          </div>
        </div>
      }

      <!-- Rejection Modal (Reason Required) -->
      @if (rejectingProductId()) {
        <div class="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4">
          <div class="glass-card rounded-3xl p-6 max-w-md w-full border border-slate-700 space-y-4 shadow-2xl">
            <h3 class="text-base font-bold text-white">Reject Product Listing</h3>
            <p class="text-xs text-slate-400">Please provide a clear reason so the seller can correct and resubmit the listing.</p>

            <div class="space-y-2">
              <label class="text-xs font-medium text-slate-300">Rejection Reason</label>
              <textarea [(ngModel)]="rejectionReason" rows="3" placeholder="e.g. Low-resolution product image, missing detailed specifications, or price mismatch..." class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-xs focus:outline-none focus:border-rose-500"></textarea>
            </div>

            <div class="flex justify-end space-x-2 pt-2">
              <button (click)="rejectingProductId.set(null)" class="px-3 py-1.5 rounded-lg bg-slate-800 text-slate-300 text-xs">Cancel</button>
              <button (click)="submitRejection()" class="px-4 py-1.5 rounded-lg bg-rose-500 hover:bg-rose-400 text-white font-bold text-xs">Confirm Rejection</button>
            </div>
          </div>
        </div>
      }

      <!-- Audit Logs Modal -->
      @if (viewingAuditLogs()) {
        <div class="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4">
          <div class="glass-card rounded-3xl p-6 max-w-lg w-full border border-slate-700 space-y-4 shadow-2xl max-h-[80vh] overflow-y-auto">
            <div class="flex justify-between items-center border-b border-slate-800 pb-3">
              <h3 class="text-base font-bold text-white">Product Lifecycle Audit History</h3>
              <button (click)="viewingAuditLogs.set(null)" class="text-slate-400 hover:text-white text-lg">&times;</button>
            </div>

            <div class="space-y-3 text-xs">
              @for (log of auditLogs(); track log.id) {
                <div class="p-3 bg-slate-900/80 rounded-xl border border-slate-800 space-y-1">
                  <div class="flex justify-between items-center">
                    <span class="font-bold text-emerald-400">{{ log.action }}</span>
                    <span class="text-[10px] text-slate-500">{{ log.createdAt | date:'short' }}</span>
                  </div>
                  <div class="text-slate-300">Actor: {{ log.actorRole }} #{{ log.actorId }}</div>
                  @if (log.reason) {
                    <div class="text-slate-400 text-[11px] italic">"{{ log.reason }}"</div>
                  }
                </div>
              }
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class AdminDashboardComponent implements OnInit {
  private productService = inject(ProductService);
  private sellerService = inject(SellerService);
  private orderService = inject(OrderService);

  activeTab: 'product_review' | 'seller_moderation' | 'platform_orders' | 'catalog_inventory' = 'product_review';

  pendingProducts = signal<Product[]>([]);
  allProducts = signal<Product[]>([]);
  allSellers = signal<SellerProfile[]>([]);
  pendingSellers = signal<SellerProfile[]>([]);
  platformOrders = signal<ParentOrder[]>([]);
  alertMessage = signal<string | null>(null);

  searchQuery = '';

  rejectingProductId = signal<number | null>(null);
  rejectionReason = '';

  viewingAuditLogs = signal<number | null>(null);
  auditLogs = signal<ProductAuditLog[]>([]);

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.productService.getPendingProducts(0, 50).subscribe({
      next: (res) => {
        if (res.success) this.pendingProducts.set(res.data.content);
      }
    });

    this.productService.getAllAdminProducts(0, 100).subscribe({
      next: (res) => {
        if (res.success) this.allProducts.set(res.data.content);
      }
    });

    this.sellerService.getAllSellers().subscribe({
      next: (res) => {
        if (res.success) {
          this.allSellers.set(res.data);
          this.pendingSellers.set(res.data.filter(s => s.verificationStatus === 'PENDING'));
        }
      }
    });

    this.orderService.getAllParentOrders(0, 50).subscribe({
      next: (res) => {
        if (res.success) this.platformOrders.set(res.data.content);
      }
    });
  }

  filteredAllProducts(): Product[] {
    const q = this.searchQuery.trim().toLowerCase();
    if (!q) return this.allProducts();
    return this.allProducts().filter(p => 
      p.name.toLowerCase().includes(q) || 
      (p.description && p.description.toLowerCase().includes(q)) ||
      (p.categoryName && p.categoryName.toLowerCase().includes(q)) ||
      p.id.toString().includes(q)
    );
  }

  deleteProduct(id: number): void {
    if (confirm(`Are you sure you want to permanently delete product #${id} and purge all associated records?`)) {
      this.productService.deleteAdminProduct(id).subscribe({
        next: () => {
          this.alertMessage.set(`Product #${id} permanently deleted.`);
          this.loadData();
        }
      });
    }
  }

  approveProduct(id: number): void {
    this.productService.approveProduct(id).subscribe({
      next: () => {
        this.alertMessage.set(`Product #${id} approved and published to the marketplace catalog.`);
        this.loadData();
      }
    });
  }

  openRejectModal(id: number): void {
    this.rejectingProductId.set(id);
    this.rejectionReason = '';
  }

  submitRejection(): void {
    const id = this.rejectingProductId();
    if (!id) return;
    this.productService.rejectProduct(id, this.rejectionReason).subscribe({
      next: () => {
        this.alertMessage.set(`Product #${id} rejected.`);
        this.rejectingProductId.set(null);
        this.loadData();
      }
    });
  }

  viewAuditLogs(id: number): void {
    this.viewingAuditLogs.set(id);
    this.productService.getProductAuditLogs(id).subscribe({
      next: (res) => {
        if (res.success) this.auditLogs.set(res.data);
      }
    });
  }

  approveSeller(id: number): void {
    this.sellerService.approveSeller(id).subscribe({
      next: () => {
        this.alertMessage.set(`Seller profile #${id} approved!`);
        this.loadData();
      }
    });
  }

  rejectSeller(id: number): void {
    this.sellerService.rejectSeller(id, 'Business documentation incomplete').subscribe({
      next: () => {
        this.alertMessage.set(`Seller profile #${id} rejected.`);
        this.loadData();
      }
    });
  }

  suspendSeller(id: number): void {
    this.sellerService.suspendSeller(id, 'Policy violation').subscribe({
      next: () => {
        this.alertMessage.set(`Seller profile #${id} suspended.`);
        this.loadData();
      }
    });
  }

  getSellerStatusClass(status: string): string {
    switch (status) {
      case 'APPROVED': return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'PENDING': return 'bg-amber-500/10 text-amber-400 border-amber-500/30';
      case 'REJECTED': return 'bg-rose-500/10 text-rose-400 border-rose-500/30';
      case 'SUSPENDED': return 'bg-purple-500/10 text-purple-400 border-purple-500/30';
      default: return 'bg-slate-800 text-slate-400 border-slate-700';
    }
  }

  getProductStatusClass(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'PENDING_REVIEW': return 'bg-amber-500/10 text-amber-400 border-amber-500/30';
      case 'APPROVED': return 'bg-blue-500/10 text-blue-400 border-blue-500/30';
      case 'REJECTED': return 'bg-rose-500/10 text-rose-400 border-rose-500/30';
      case 'SUSPENDED': return 'bg-purple-500/10 text-purple-400 border-purple-500/30';
      default: return 'bg-slate-800 text-slate-400 border-slate-700';
    }
  }
}
