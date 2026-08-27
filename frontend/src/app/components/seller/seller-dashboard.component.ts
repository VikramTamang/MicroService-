import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';
import { Category, CreateProductRequest, Product } from '../../models/product.model';
import { FulfillSubOrderRequest, SubOrder } from '../../models/order.model';
import { SellerProfile } from '../../models/user.model';

@Component({
  selector: 'app-seller-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-8 max-w-7xl mx-auto">
      <!-- Store Header Banner -->
      <div class="glass-card rounded-3xl p-6 border border-slate-800 flex flex-col md:flex-row md:items-center justify-between gap-6 relative overflow-hidden">
        <div class="absolute -right-10 -bottom-10 w-64 h-64 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none"></div>

        <div class="flex items-center space-x-5">
          <div class="w-16 h-16 rounded-2xl bg-slate-900 border border-slate-800 flex items-center justify-center text-2xl overflow-hidden flex-shrink-0 shadow-lg">
            @if (sellerProfile()?.logoUrl) {
              <img [src]="sellerProfile()?.logoUrl" alt="Store Logo" class="w-full h-full object-cover" />
            } @else {
              🏪
            }
          </div>
          <div>
            <div class="flex items-center space-x-3">
              <h1 class="text-2xl font-extrabold font-['Outfit'] text-white">
                {{ sellerProfile()?.storeName || 'Merchant Store Hub' }}
              </h1>
              <span [class]="getVerificationBadgeClass(sellerProfile()?.verificationStatus || 'APPROVED')" 
                    class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border">
                {{ sellerProfile()?.verificationStatus || 'APPROVED' }}
              </span>
            </div>
            <p class="text-xs text-slate-400 mt-1 max-w-xl">
              {{ sellerProfile()?.storeDescription || 'Manage product catalog, track customer sub-orders, and fulfill shipments.' }}
            </p>
          </div>
        </div>

        <!-- Tab Controls -->
        <div class="flex items-center space-x-2 bg-slate-900/90 p-1.5 rounded-xl border border-slate-800 flex-shrink-0">
          <button 
            (click)="activeTab = 'products'" 
            [class]="activeTab === 'products' ? 'bg-emerald-500 text-slate-950 font-bold' : 'text-slate-400 hover:text-white'"
            class="px-4 py-2 rounded-lg text-xs transition-all duration-200">
            My Products ({{ products().length }})
          </button>
          <button 
            (click)="activeTab = 'orders'" 
            [class]="activeTab === 'orders' ? 'bg-emerald-500 text-slate-950 font-bold' : 'text-slate-400 hover:text-white'"
            class="px-4 py-2 rounded-lg text-xs transition-all duration-200">
            Sub-Orders ({{ subOrders().length }})
          </button>
        </div>
      </div>

      <!-- Notification Banner -->
      @if (alertMessage()) {
        <div class="p-3.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs flex items-center justify-between">
          <span>{{ alertMessage() }}</span>
          <button (click)="alertMessage.set(null)" class="text-slate-400 hover:text-white">&times;</button>
        </div>
      }

      <!-- TAB 1: SELLER PRODUCTS -->
      @if (activeTab === 'products') {
        <div class="space-y-6">
          <div class="flex justify-between items-center">
            <div>
              <h3 class="text-lg font-bold text-white">Merchant Inventory</h3>
              <p class="text-xs text-slate-400">Newly added items are set to PENDING_REVIEW until approved by marketplace moderation.</p>
            </div>
            <button 
              (click)="openCreateModal = true"
              class="px-4 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs flex items-center space-x-1.5 shadow-md shadow-emerald-500/20 transition-all">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
              <span>Add Product</span>
            </button>
          </div>

          <div class="glass-card rounded-2xl overflow-hidden border border-slate-800">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/80 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">Product Details</th>
                    <th class="p-4">SKU</th>
                    <th class="p-4">Price</th>
                    <th class="p-4">Stock</th>
                    <th class="p-4">Moderation Status</th>
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
                          <span class="text-[10px] text-slate-500">{{ p.categoryName || 'General' }}</span>
                        </div>
                      </td>
                      <td class="p-4 font-mono text-slate-400">{{ p.sku || 'N/A' }}</td>
                      <td class="p-4 font-semibold text-emerald-400 font-['Outfit']">\${{ p.price.toFixed(2) }}</td>
                      <td class="p-4">
                        <input 
                          type="number" 
                          [value]="p.stockQuantity" 
                          (change)="updateStock(p.id, $event)"
                          class="w-20 p-1.5 bg-slate-900 border border-slate-700 rounded-lg text-xs text-white focus:outline-none focus:border-emerald-500" />
                      </td>
                      <td class="p-4">
                        <div>
                          <span [class]="getProductStatusBadgeClass(p.status)" class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border inline-block">
                            {{ p.status }}
                          </span>
                          @if (p.status === 'REJECTED' && p.rejectionReason) {
                            <p class="text-[10px] text-rose-400 mt-1 max-w-xs">
                              Reason: {{ p.rejectionReason }}
                            </p>
                          }
                        </div>
                      </td>
                      <td class="p-4 text-right space-x-2">
                        <button (click)="deleteProduct(p.id)" class="p-1.5 rounded-lg bg-slate-900 hover:bg-rose-500/20 text-slate-400 hover:text-rose-400 border border-slate-800 transition-colors" title="Deactivate">
                          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      </td>
                    </tr>
                  }
                  @if (products().length === 0) {
                    <tr>
                      <td colspan="6" class="p-8 text-center text-slate-500">
                        No product listings found. Click "Add Product" to create your first listing.
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- TAB 2: SELLER SUB-ORDERS FULFILLMENT -->
      @if (activeTab === 'orders') {
        <div class="space-y-6">
          <div class="flex justify-between items-center">
            <div>
              <h3 class="text-lg font-bold text-white">Store Sub-Orders & Fulfillment</h3>
              <p class="text-xs text-slate-400">Accept incoming customer orders, pack items, and dispatch with tracking information.</p>
            </div>
          </div>

          <div class="glass-card rounded-2xl overflow-hidden border border-slate-800">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/80 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">Sub-Order #</th>
                    <th class="p-4">Items</th>
                    <th class="p-4">Subtotal</th>
                    <th class="p-4">Status</th>
                    <th class="p-4">Tracking</th>
                    <th class="p-4 text-right">Fulfillment Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/60">
                  @for (sub of subOrders(); track sub.id) {
                    <tr class="hover:bg-slate-800/30 transition-colors">
                      <td class="p-4 font-mono font-bold text-emerald-400">
                        {{ sub.subOrderNumber }}
                        <span class="block text-[10px] text-slate-500 font-sans">{{ sub.createdAt | date:'short' }}</span>
                      </td>
                      <td class="p-4">
                        <div class="space-y-1">
                          @for (item of sub.items; track item.id) {
                            <div class="text-slate-200">
                              <span class="font-semibold">{{ item.quantity }}x</span> {{ item.productName }}
                              <span class="text-slate-500 font-mono text-[10px]">(\${{ item.unitPrice.toFixed(2) }})</span>
                            </div>
                          }
                        </div>
                      </td>
                      <td class="p-4 font-bold text-white font-['Outfit']">\${{ sub.subtotal.toFixed(2) }}</td>
                      <td class="p-4">
                        <span [class]="getSubOrderStatusBadgeClass(sub.status)" class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border">
                          {{ sub.status }}
                        </span>
                      </td>
                      <td class="p-4">
                        @if (sub.trackingCode) {
                          <div>
                            <span class="text-[10px] font-semibold text-slate-300">{{ sub.carrier }}</span>
                            <span class="block font-mono text-emerald-400 text-[11px]">{{ sub.trackingCode }}</span>
                          </div>
                        } @else {
                          <span class="text-slate-600 text-[11px]">Unassigned</span>
                        }
                      </td>
                      <td class="p-4 text-right space-x-2">
                        @if (sub.status === 'PLACED') {
                          <button (click)="confirmOrder(sub.subOrderNumber)" class="px-3 py-1.5 rounded-lg bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-[11px] shadow-sm">
                            Confirm Order
                          </button>
                        } @else if (sub.status === 'CONFIRMED') {
                          <button (click)="packOrder(sub.subOrderNumber)" class="px-3 py-1.5 rounded-lg bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-bold text-[11px] shadow-sm">
                            Pack Items
                          </button>
                        } @else if (sub.status === 'PACKED') {
                          <button (click)="openShipModal(sub.subOrderNumber)" class="px-3 py-1.5 rounded-lg bg-indigo-500 hover:bg-indigo-400 text-white font-bold text-[11px] shadow-sm">
                            Dispatch / Ship
                          </button>
                        } @else if (sub.status === 'SHIPPED') {
                          <span class="text-[11px] text-indigo-400 font-semibold">In Transit 🚚</span>
                        } @else if (sub.status === 'DELIVERED') {
                          <span class="text-[11px] text-emerald-400 font-semibold">Fulfilled ✅</span>
                        } @else {
                          <span class="text-[11px] text-slate-500">{{ sub.status }}</span>
                        }
                      </td>
                    </tr>
                  }
                  @if (subOrders().length === 0) {
                    <tr>
                      <td colspan="6" class="p-8 text-center text-slate-500">
                        No customer sub-orders assigned to your store yet.
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- Create Product Modal (Drag & Drop) -->
      @if (openCreateModal) {
        <div class="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4">
          <div class="glass-card rounded-3xl p-6 max-w-lg w-full border border-slate-700 space-y-5 shadow-2xl max-h-[90vh] overflow-y-auto">
            <div class="flex justify-between items-center border-b border-slate-800 pb-3">
              <div>
                <h3 class="text-lg font-bold text-white">Create Merchant Product</h3>
                <p class="text-xs text-slate-400">Listing will be submitted to Admin Moderation (PENDING_REVIEW)</p>
              </div>
              <button (click)="closeModal()" class="text-slate-400 hover:text-white text-lg">&times;</button>
            </div>

            <form (ngSubmit)="saveProduct()" class="space-y-4 text-xs">
              <div class="space-y-1.5">
                <label class="font-medium text-slate-300">Product Name</label>
                <input type="text" [(ngModel)]="newProduct.name" name="name" required class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500" />
              </div>

              <div class="grid grid-cols-2 gap-3">
                <div class="space-y-1.5">
                  <label class="font-medium text-slate-300">Price ($USD)</label>
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

              <!-- Drag & Drop Upload -->
              <div class="space-y-1.5">
                <label class="font-medium text-slate-300">Product Image (Drag & Drop or Browse)</label>
                @if (previewUrl() || newProduct.imageUrl) {
                  <div class="relative rounded-2xl overflow-hidden border border-slate-700 bg-slate-900/90 p-2.5">
                    <div class="relative h-40 w-full rounded-xl overflow-hidden bg-slate-950 flex items-center justify-center">
                      <img [src]="previewUrl() || newProduct.imageUrl" alt="Preview" class="w-full h-full object-contain" />
                      @if (isUploading()) {
                        <div class="absolute inset-0 bg-slate-950/80 flex items-center justify-center text-xs font-semibold text-emerald-400">
                          Uploading...
                        </div>
                      }
                    </div>
                    <div class="flex items-center justify-between mt-2 px-1">
                      <span class="text-[11px] text-slate-400 truncate max-w-[200px]">{{ selectedFileName() || 'Image Selected' }}</span>
                      <button type="button" (click)="removeImage()" class="text-rose-400 hover:text-rose-300 text-xs">Remove</button>
                    </div>
                  </div>
                } @else {
                  <div 
                    (dragover)="onDragOver($event)"
                    (dragleave)="onDragLeave($event)"
                    (drop)="onFileDrop($event)"
                    [class.border-emerald-500]="isDragging()"
                    class="border-2 border-dashed border-slate-700 rounded-2xl p-6 text-center bg-slate-900/60 cursor-pointer hover:border-emerald-500/60 transition-colors">
                    <input #fileInput type="file" accept="image/*" (change)="onFileSelected($event)" class="hidden" />
                    <div (click)="fileInput.click()" class="space-y-2">
                      <div class="text-2xl">🖼️</div>
                      <p class="text-xs font-bold text-white"><span class="text-emerald-400">Click to upload</span> or drag and drop</p>
                      <p class="text-[10px] text-slate-400">JPG, PNG, WEBP up to 10MB</p>
                    </div>
                  </div>
                }
              </div>

              <div class="space-y-1.5">
                <label class="font-medium text-slate-300">Description</label>
                <textarea [(ngModel)]="newProduct.description" name="desc" rows="3" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 focus:outline-none focus:border-emerald-500"></textarea>
              </div>

              <div class="flex justify-end space-x-3 pt-3">
                <button type="button" (click)="closeModal()" class="px-4 py-2 rounded-xl bg-slate-800 text-slate-300 text-xs">Cancel</button>
                <button type="submit" [disabled]="isUploading()" class="px-5 py-2 rounded-xl bg-emerald-500 text-slate-950 font-bold text-xs disabled:opacity-50">
                  Submit Listing
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <!-- Ship Order Modal -->
      @if (shippingSubOrderNumber()) {
        <div class="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4">
          <div class="glass-card rounded-3xl p-6 max-w-md w-full border border-slate-700 space-y-4 shadow-2xl">
            <h3 class="text-base font-bold text-white">Dispatch Sub-Order</h3>
            <p class="text-xs text-slate-400 font-mono">Order: {{ shippingSubOrderNumber() }}</p>

            <form (ngSubmit)="submitShipment()" class="space-y-3 text-xs">
              <div class="space-y-1">
                <label class="font-medium text-slate-300">Carrier / Logistics Provider</label>
                <input type="text" [(ngModel)]="shipRequest.carrier" name="carrier" required placeholder="e.g. DHL Express, FedEx, UPS" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200" />
              </div>
              <div class="space-y-1">
                <label class="font-medium text-slate-300">Tracking Code / Waybill #</label>
                <input type="text" [(ngModel)]="shipRequest.trackingCode" name="tracking" required placeholder="e.g. DHL-99882190" class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 font-mono" />
              </div>
              <div class="flex justify-end space-x-2 pt-2">
                <button type="button" (click)="shippingSubOrderNumber.set(null)" class="px-3 py-1.5 rounded-lg bg-slate-800 text-slate-300 text-xs">Cancel</button>
                <button type="submit" class="px-4 py-1.5 rounded-lg bg-indigo-500 hover:bg-indigo-400 text-white font-bold text-xs">Confirm Shipment</button>
              </div>
            </form>
          </div>
        </div>
      }
    </div>
  `
})
export class SellerDashboardComponent implements OnInit {
  private productService = inject(ProductService);
  private orderService = inject(OrderService);
  private authService = inject(AuthService);

  activeTab: 'products' | 'orders' = 'products';
  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  subOrders = signal<SubOrder[]>([]);
  sellerProfile = signal<SellerProfile | null>(null);
  alertMessage = signal<string | null>(null);

  openCreateModal = false;
  shippingSubOrderNumber = signal<string | null>(null);
  shipRequest: FulfillSubOrderRequest = { carrier: 'DHL Express', trackingCode: '' };

  // Image Upload signals
  isDragging = signal<boolean>(false);
  isUploading = signal<boolean>(false);
  previewUrl = signal<string | null>(null);
  selectedFileName = signal<string | null>(null);

  newProduct: CreateProductRequest = {
    name: '',
    description: '',
    price: 49.99,
    stockQuantity: 25,
    imageUrl: ''
  };

  ngOnInit(): void {
    this.sellerProfile.set(this.authService.sellerProfile());
    this.loadData();
  }

  loadData(): void {
    this.productService.getSellerProducts(0, 50).subscribe({
      next: (res) => {
        if (res.success) this.products.set(res.data.content);
      }
    });

    this.productService.getCategories().subscribe({
      next: (res) => {
        if (res.success) this.categories.set(res.data);
      }
    });

    this.orderService.getSellerSubOrders(0, 50).subscribe({
      next: (res) => {
        if (res.success) this.subOrders.set(res.data.content);
      }
    });
  }

  // --- Drag & Drop Handlers ---
  onDragOver(e: DragEvent): void { e.preventDefault(); this.isDragging.set(true); }
  onDragLeave(e: DragEvent): void { e.preventDefault(); this.isDragging.set(false); }
  onFileDrop(e: DragEvent): void {
    e.preventDefault();
    this.isDragging.set(false);
    const file = e.dataTransfer?.files?.[0];
    if (file) this.processFile(file);
  }
  onFileSelected(e: Event): void {
    const input = e.target as HTMLInputElement;
    const file = input.files?.[0];
    if (file) this.processFile(file);
  }

  private processFile(file: File): void {
    this.selectedFileName.set(file.name);
    const reader = new FileReader();
    reader.onload = () => this.previewUrl.set(reader.result as string);
    reader.readAsDataURL(file);

    this.isUploading.set(true);
    this.productService.uploadImage(file).subscribe({
      next: (res) => {
        this.isUploading.set(false);
        if (res.success && res.data?.imageUrl) {
          this.newProduct.imageUrl = res.data.imageUrl;
        }
      },
      error: () => {
        this.isUploading.set(false);
        if (this.previewUrl()) this.newProduct.imageUrl = this.previewUrl()!;
      }
    });
  }

  removeImage(): void {
    this.previewUrl.set(null);
    this.selectedFileName.set(null);
    this.newProduct.imageUrl = '';
  }

  closeModal(): void {
    this.openCreateModal = false;
    this.removeImage();
  }

  saveProduct(): void {
    this.productService.createSellerProduct(this.newProduct).subscribe({
      next: (res) => {
        if (res.success) {
          this.alertMessage.set('Product submitted for review! Moderation status: PENDING_REVIEW');
          this.closeModal();
          this.loadData();
          this.newProduct = { name: '', description: '', price: 49.99, stockQuantity: 25, imageUrl: '' };
        }
      },
      error: (err) => alert(err.error?.message || 'Failed to submit product')
    });
  }

  updateStock(productId: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    const stock = parseInt(input.value, 10);
    if (!isNaN(stock) && stock >= 0) {
      this.productService.updateSellerStock(productId, stock).subscribe({
        next: () => this.alertMessage.set('Stock level updated successfully.')
      });
    }
  }

  deleteProduct(id: number): void {
    if (confirm('Deactivate this product listing?')) {
      this.productService.deleteSellerProduct(id).subscribe({
        next: () => {
          this.alertMessage.set('Product listing deactivated.');
          this.loadData();
        }
      });
    }
  }

  // --- Sub-Order Fulfillment Handlers ---
  confirmOrder(subOrderNumber: string): void {
    this.orderService.confirmSubOrder(subOrderNumber).subscribe({
      next: () => {
        this.alertMessage.set(`Sub-order ${subOrderNumber} confirmed.`);
        this.loadData();
      }
    });
  }

  packOrder(subOrderNumber: string): void {
    this.orderService.packSubOrder(subOrderNumber).subscribe({
      next: () => {
        this.alertMessage.set(`Sub-order ${subOrderNumber} packed and ready for dispatch.`);
        this.loadData();
      }
    });
  }

  openShipModal(subOrderNumber: string): void {
    this.shippingSubOrderNumber.set(subOrderNumber);
    this.shipRequest = { carrier: 'DHL Express', trackingCode: 'DHL-' + Math.floor(100000 + Math.random() * 900000) };
  }

  submitShipment(): void {
    const subNum = this.shippingSubOrderNumber();
    if (!subNum) return;
    this.orderService.shipSubOrder(subNum, this.shipRequest).subscribe({
      next: () => {
        this.alertMessage.set(`Sub-order ${subNum} dispatched with tracking ${this.shipRequest.trackingCode}`);
        this.shippingSubOrderNumber.set(null);
        this.loadData();
      }
    });
  }

  getVerificationBadgeClass(status: string): string {
    switch (status) {
      case 'APPROVED': return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'PENDING': return 'bg-amber-500/10 text-amber-400 border-amber-500/30';
      case 'REJECTED': return 'bg-rose-500/10 text-rose-400 border-rose-500/30';
      default: return 'bg-slate-800 text-slate-400 border-slate-700';
    }
  }

  getProductStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ACTIVE': return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'APPROVED': return 'bg-cyan-500/10 text-cyan-400 border-cyan-500/30';
      case 'PENDING_REVIEW': return 'bg-amber-500/10 text-amber-400 border-amber-500/30';
      case 'REJECTED': return 'bg-rose-500/10 text-rose-400 border-rose-500/30';
      case 'SUSPENDED': return 'bg-purple-500/10 text-purple-400 border-purple-500/30';
      default: return 'bg-slate-800 text-slate-400 border-slate-700';
    }
  }

  getSubOrderStatusBadgeClass(status: string): string {
    switch (status) {
      case 'PLACED': return 'bg-blue-500/10 text-blue-400 border-blue-500/30';
      case 'CONFIRMED': return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30';
      case 'PACKED': return 'bg-cyan-500/10 text-cyan-400 border-cyan-500/30';
      case 'SHIPPED': return 'bg-indigo-500/10 text-indigo-400 border-indigo-500/30';
      case 'DELIVERED': return 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40';
      case 'CANCELLED': return 'bg-rose-500/10 text-rose-400 border-rose-500/30';
      default: return 'bg-slate-800 text-slate-400 border-slate-700';
    }
  }
}
