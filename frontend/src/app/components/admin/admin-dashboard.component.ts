import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { OrderService } from '../../services/order.service';
import { Category, CreateProductRequest, Product } from '../../models/product.model';
import { Order, OrderStatus } from '../../models/order.model';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-8 max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-800 pb-6">
        <div>
          <div class="flex items-center space-x-2">
            <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">ADMINISTRATOR</span>
            <h1 class="text-3xl font-extrabold font-['Outfit'] text-white">Management Console</h1>
          </div>
          <p class="text-xs text-slate-400 mt-1">Direct control over product inventory, stock levels, and order fulfillment</p>
        </div>

        <!-- Tab Controls -->
        <div class="flex items-center space-x-2 bg-slate-900/90 p-1.5 rounded-xl border border-slate-800">
          <button 
            (click)="activeTab = 'products'" 
            [class]="activeTab === 'products' ? 'bg-emerald-500 text-slate-950 font-bold' : 'text-slate-400 hover:text-white'"
            class="px-4 py-2 rounded-lg text-xs transition-all duration-200">
            Products ({{ products().length }})
          </button>
          <button 
            (click)="activeTab = 'orders'" 
            [class]="activeTab === 'orders' ? 'bg-emerald-500 text-slate-950 font-bold' : 'text-slate-400 hover:text-white'"
            class="px-4 py-2 rounded-lg text-xs transition-all duration-200">
            Orders ({{ orders().length }})
          </button>
          <button 
            (click)="activeTab = 'system'" 
            [class]="activeTab === 'system' ? 'bg-emerald-500 text-slate-950 font-bold' : 'text-slate-400 hover:text-white'"
            class="px-4 py-2 rounded-lg text-xs transition-all duration-200">
            Topology
          </button>
        </div>
      </div>

      <!-- Notification Alert -->
      @if (alertMessage()) {
        <div class="p-3.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs flex items-center justify-between">
          <span>{{ alertMessage() }}</span>
          <button (click)="alertMessage.set(null)" class="text-slate-400 hover:text-white">&times;</button>
        </div>
      }

      <!-- TAB 1: PRODUCTS MANAGEMENT -->
      @if (activeTab === 'products') {
        <div class="space-y-6">
          <div class="flex justify-between items-center">
            <h3 class="text-lg font-bold text-white">Product Catalog Inventory</h3>
            <button 
              (click)="openCreateModal = true"
              class="px-4 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs flex items-center space-x-1.5 shadow-md shadow-emerald-500/20 transition-all">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              <span>Add New Product</span>
            </button>
          </div>

          <div class="glass-card rounded-2xl overflow-hidden border border-slate-800">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/80 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">Product</th>
                    <th class="p-4">Category</th>
                    <th class="p-4">Price</th>
                    <th class="p-4">Stock Level</th>
                    <th class="p-4">Status</th>
                    <th class="p-4 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/60">
                  @for (p of products(); track p.id) {
                    <tr class="hover:bg-slate-800/30 transition-colors">
                      <td class="p-4 font-medium text-white flex items-center space-x-3">
                        <div class="w-10 h-10 rounded-lg bg-slate-800 overflow-hidden flex-shrink-0">
                          @if (p.imageUrl) {
                            <img [src]="p.imageUrl" [alt]="p.name" class="w-full h-full object-cover" />
                          }
                        </div>
                        <div>
                          <span class="block font-bold">{{ p.name }}</span>
                          <span class="text-[10px] text-slate-500 font-mono">{{ p.slug }}</span>
                        </div>
                      </td>
                      <td class="p-4 text-slate-400">{{ p.categoryName || 'General' }}</td>
                      <td class="p-4 font-semibold text-emerald-400 font-['Outfit']">\${{ p.price.toFixed(2) }}</td>
                      <td class="p-4">
                        <span [class]="p.stockQuantity > 10 ? 'text-slate-200' : 'text-amber-400 font-bold'">
                          {{ p.stockQuantity }} units
                        </span>
                      </td>
                      <td class="p-4">
                        <span [class]="p.active ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30' : 'bg-rose-500/10 text-rose-400 border-rose-500/30'" class="px-2 py-0.5 rounded-full text-[10px] font-bold border">
                          {{ p.active ? 'Active' : 'Disabled' }}
                        </span>
                      </td>
                      <td class="p-4 text-right space-x-2">
                        <button (click)="deleteProduct(p.id)" class="p-1.5 rounded-lg bg-slate-900 hover:bg-rose-500/20 text-slate-400 hover:text-rose-400 border border-slate-800 transition-colors" title="Delete">
                          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- TAB 2: ORDERS FULFILLMENT -->
      @if (activeTab === 'orders') {
        <div class="space-y-6">
          <h3 class="text-lg font-bold text-white">System Orders & Fulfillment</h3>

          <div class="glass-card rounded-2xl overflow-hidden border border-slate-800">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/80 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">Tracking Number</th>
                    <th class="p-4">Customer</th>
                    <th class="p-4">Items</th>
                    <th class="p-4">Total</th>
                    <th class="p-4">Status & Update</th>
                    <th class="p-4">Date</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/60">
                  @for (order of orders(); track order.id) {
                    <tr class="hover:bg-slate-800/30 transition-colors">
                      <td class="p-4 font-mono font-bold text-emerald-400">{{ order.trackingNumber }}</td>
                      <td class="p-4">
                        <span class="block text-white font-medium">{{ order.userEmail }}</span>
                        <span class="text-[10px] text-slate-500">{{ order.shippingCity }}</span>
                      </td>
                      <td class="p-4 text-slate-400">{{ order.items.length || 0 }} items</td>
                      <td class="p-4 font-bold text-white font-['Outfit']">\${{ order.totalAmount.toFixed(2) }}</td>
                      <td class="p-4">
                        <select 
                          [ngModel]="order.status" 
                          (ngModelChange)="updateStatus(order.id, $event)"
                          class="p-1.5 bg-slate-900 border border-slate-700 rounded-lg text-xs text-white focus:outline-none focus:border-emerald-500">
                          <option value="CONFIRMED">CONFIRMED</option>
                          <option value="PROCESSING">PROCESSING</option>
                          <option value="SHIPPED">SHIPPED</option>
                          <option value="DELIVERED">DELIVERED</option>
                          <option value="CANCELLED">CANCELLED</option>
                        </select>
                      </td>
                      <td class="p-4 text-slate-500 text-[11px]">{{ order.createdAt | date:'short' }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- TAB 3: SYSTEM TOPOLOGY -->
      @if (activeTab === 'system') {
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="glass-card rounded-2xl p-6 border border-slate-800 space-y-4">
            <div class="flex items-center space-x-3">
              <span class="w-3 h-3 rounded-full bg-emerald-400 animate-pulse"></span>
              <h4 class="font-bold text-white">Eureka Server</h4>
            </div>
            <p class="text-xs text-slate-400">Service discovery registry tracking heartbeat of all downstream nodes.</p>
            <div class="p-3 bg-slate-900 rounded-xl font-mono text-xs text-emerald-400">Port: 8761</div>
          </div>

          <div class="glass-card rounded-2xl p-6 border border-slate-800 space-y-4">
            <div class="flex items-center space-x-3">
              <span class="w-3 h-3 rounded-full bg-emerald-400 animate-pulse"></span>
              <h4 class="font-bold text-white">Spring Cloud Gateway</h4>
            </div>
            <p class="text-xs text-slate-400">Single entrance gateway verifying JWT tokens and load-balancing traffic.</p>
            <div class="p-3 bg-slate-900 rounded-xl font-mono text-xs text-emerald-400">Port: 8080</div>
          </div>

          <div class="glass-card rounded-2xl p-6 border border-slate-800 space-y-4">
            <div class="flex items-center space-x-3">
              <span class="w-3 h-3 rounded-full bg-emerald-400 animate-pulse"></span>
              <h4 class="font-bold text-white">Order & Feign RPC</h4>
            </div>
            <p class="text-xs text-slate-400">Resilience4j circuit-breaker protected inter-service calls.</p>
            <div class="p-3 bg-slate-900 rounded-xl font-mono text-xs text-emerald-400">Port: 8083</div>
          </div>
        </div>
      }

      <!-- Create Product Modal -->
      @if (openCreateModal) {
        <div class="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4">
          <div class="glass-card rounded-3xl p-6 max-w-lg w-full border border-slate-700 space-y-5 shadow-2xl">
            <div class="flex justify-between items-center border-b border-slate-800 pb-3">
              <h3 class="text-lg font-bold text-white">Add New Product</h3>
              <button (click)="openCreateModal = false" class="text-slate-400 hover:text-white">&times;</button>
            </div>

            <form (ngSubmit)="saveProduct()" class="space-y-4 text-xs">
              <div class="space-y-1.5">
                <label class="font-medium text-slate-300">Product Name</label>
                <input type="text" [(ngModel)]="newProduct.name" name="name" required class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
              </div>

              <div class="grid grid-cols-2 gap-3">
                <div class="space-y-1.5">
                  <label class="font-medium text-slate-300">Price (\USD)</label>
                  <input type="number" step="0.01" [(ngModel)]="newProduct.price" name="price" required class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
                </div>
                <div class="space-y-1.5">
                  <label class="font-medium text-slate-300">Stock Quantity</label>
                  <input type="number" [(ngModel)]="newProduct.stockQuantity" name="stock" required class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
                </div>
              </div>

              <div class="space-y-1.5">
                <label class="font-medium text-slate-300">Category</label>
                <select [(ngModel)]="newProduct.categoryId" name="cat" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500">
                  <option [ngValue]="undefined">Select Category</option>
                  @for (c of categories(); track c.id) {
                    <option [ngValue]="c.id">{{ c.name }}</option>
                  }
                </select>
              </div>

              <div class="space-y-1.5">
                <label class="font-medium text-slate-300">Image URL</label>
                <input type="text" [(ngModel)]="newProduct.imageUrl" name="img" placeholder="https://..." class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
              </div>

              <div class="space-y-1.5">
                <label class="font-medium text-slate-300">Description</label>
                <textarea [(ngModel)]="newProduct.description" name="desc" rows="3" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500"></textarea>
              </div>

              <div class="flex justify-end space-x-3 pt-3">
                <button type="button" (click)="openCreateModal = false" class="px-4 py-2 rounded-xl bg-slate-850 hover:bg-slate-800 text-slate-300 font-semibold text-xs border border-slate-800">
                  Cancel
                </button>
                <button type="submit" class="px-5 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs shadow-md">
                  Save Product
                </button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>
  `
})
export class AdminDashboardComponent implements OnInit {
  private productService = inject(ProductService);
  private orderService = inject(OrderService);

  activeTab: 'products' | 'orders' | 'system' = 'products';
  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  orders = signal<Order[]>([]);
  alertMessage = signal<string | null>(null);

  openCreateModal = false;
  newProduct: CreateProductRequest = {
    name: '',
    description: '',
    price: 99.99,
    stockQuantity: 50,
    imageUrl: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=600&q=80'
  };

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.productService.getProducts(undefined, undefined, 0, 100).subscribe({
      next: (res) => {
        if (res.success) this.products.set(res.data.content);
      }
    });

    this.productService.getCategories().subscribe({
      next: (res) => {
        if (res.success) this.categories.set(res.data);
      }
    });

    this.orderService.getAllOrders(0, 100).subscribe({
      next: (res) => {
        if (res.success) this.orders.set(res.data.content);
      }
    });
  }

  saveProduct(): void {
    this.productService.createProduct(this.newProduct).subscribe({
      next: (res) => {
        if (res.success) {
          this.alertMessage.set('Product created successfully!');
          this.openCreateModal = false;
          this.loadData();
        }
      },
      error: (err) => alert(err.error?.message || 'Failed to create product')
    });
  }

  deleteProduct(id: number): void {
    if (confirm('Are you sure you want to deactivate this product?')) {
      this.productService.deleteProduct(id).subscribe({
        next: () => {
          this.alertMessage.set('Product deactivated successfully.');
          this.loadData();
        }
      });
    }
  }

  updateStatus(orderId: number, status: OrderStatus): void {
    this.orderService.updateOrderStatus(orderId, status).subscribe({
      next: (res) => {
        if (res.success) {
          this.alertMessage.set(`Order #${orderId} updated to status ${status}`);
          this.loadData();
        }
      }
    });
  }
}
