import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="max-w-7xl mx-auto space-y-8">
      <!-- Breadcrumbs -->
      <nav class="flex items-center space-x-2 text-xs text-slate-400">
        <a routerLink="/" class="hover:text-emerald-400 transition-colors">Marketplace</a>
        <span>/</span>
        @if (product()?.categoryName) {
          <span class="text-slate-300">{{ product()?.categoryName }}</span>
          <span>/</span>
        }
        <span class="text-slate-200 font-semibold truncate max-w-xs">{{ product()?.name }}</span>
      </nav>

      <!-- Alert Toast -->
      @if (alertMessage()) {
        <div class="p-4 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs flex items-center justify-between shadow-lg shadow-emerald-500/10 animate-fade-in">
          <div class="flex items-center space-x-2">
            <span>✅</span>
            <span class="font-medium">{{ alertMessage() }}</span>
          </div>
          <div class="flex items-center space-x-3">
            <a routerLink="/cart" class="underline font-bold hover:text-emerald-300">View Cart</a>
            <button (click)="alertMessage.set(null)" class="text-slate-400 hover:text-white text-base">&times;</button>
          </div>
        </div>
      }

      @if (loading()) {
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-10">
          <div class="lg:col-span-6 h-96 bg-slate-900/80 rounded-3xl animate-pulse border border-slate-800"></div>
          <div class="lg:col-span-6 space-y-6">
            <div class="h-8 bg-slate-800 rounded-xl w-3/4 animate-pulse"></div>
            <div class="h-4 bg-slate-800 rounded-lg w-1/3 animate-pulse"></div>
            <div class="h-24 bg-slate-900 rounded-2xl animate-pulse border border-slate-800"></div>
            <div class="h-12 bg-slate-800 rounded-xl w-1/2 animate-pulse"></div>
          </div>
        </div>
      } @else if (!product()) {
        <div class="glass-card rounded-3xl p-16 text-center space-y-4 border border-slate-800">
          <div class="w-16 h-16 mx-auto rounded-full bg-slate-900 flex items-center justify-center text-slate-500 text-2xl">
            🔍
          </div>
          <h2 class="text-xl font-bold text-white">Product Not Found</h2>
          <p class="text-xs text-slate-400 max-w-sm mx-auto">This product listing may have been moved, removed, or is awaiting marketplace moderation.</p>
          <div class="pt-2">
            <a routerLink="/" class="px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs shadow-md transition-all">
              Return to Marketplace
            </a>
          </div>
        </div>
      } @else {
        <!-- Main Product Presentation -->
        <div class="grid grid-cols-1 lg:grid-cols-12 gap-10">
          <!-- Left Column: Media Gallery -->
          <div class="lg:col-span-6 space-y-4">
            <div class="relative h-[420px] sm:h-[480px] rounded-3xl bg-slate-900/90 border border-slate-800 overflow-hidden flex items-center justify-center group shadow-2xl">
              @if (product()?.imageUrl) {
                <img [src]="product()?.imageUrl" [alt]="product()?.name" class="w-full h-full object-contain p-4 group-hover:scale-105 transition-transform duration-500" />
              } @else {
                <div class="text-slate-600 flex flex-col items-center space-y-2">
                  <svg class="w-16 h-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span class="text-xs font-mono text-slate-500">No Image Uploaded</span>
                </div>
              }

              <!-- Stock Pill Overlay -->
              <div class="absolute top-4 right-4">
                @if (product()!.stockQuantity > 5) {
                  <span class="px-3 py-1 rounded-full text-xs font-bold bg-emerald-500/90 text-slate-950 backdrop-blur-md shadow-lg">
                    In Stock ({{ product()!.stockQuantity }} available)
                  </span>
                } @else if (product()!.stockQuantity > 0) {
                  <span class="px-3 py-1 rounded-full text-xs font-bold bg-amber-500/90 text-slate-950 backdrop-blur-md shadow-lg animate-pulse">
                    Only {{ product()!.stockQuantity }} left!
                  </span>
                } @else {
                  <span class="px-3 py-1 rounded-full text-xs font-bold bg-rose-500/90 text-white backdrop-blur-md shadow-lg">
                    Out of Stock
                  </span>
                }
              </div>

              <!-- Category Pill Overlay -->
              @if (product()?.categoryName) {
                <div class="absolute bottom-4 left-4">
                  <span class="px-3 py-1 rounded-xl text-xs font-medium bg-slate-950/80 text-slate-300 border border-slate-800 backdrop-blur-md">
                    📁 {{ product()?.categoryName }}
                  </span>
                </div>
              }
            </div>

            <!-- Merchant Trust Banner -->
            <div class="glass-card rounded-2xl p-4 border border-slate-800 flex items-center justify-between">
              <div class="flex items-center space-x-3">
                <div class="w-10 h-10 rounded-xl bg-gradient-to-br from-cyan-600 to-blue-600 flex items-center justify-center text-lg shadow-md">
                  🏪
                </div>
                <div>
                  <span class="text-[10px] text-slate-400 uppercase tracking-wider block">Sold & Fulfilled by</span>
                  <span class="text-xs font-bold text-white">{{ getSellerName(product()!.sellerId) }}</span>
                </div>
              </div>
              <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-cyan-500/10 text-cyan-400 border border-cyan-500/30">
                Verified Merchant ✓
              </span>
            </div>
          </div>

          <!-- Right Column: Product Info & Purchasing -->
          <div class="lg:col-span-6 space-y-6">
            <div class="space-y-2">
              <div class="flex items-center space-x-2">
                <span class="px-2.5 py-0.5 rounded-full text-[10px] font-mono font-semibold bg-slate-800 text-slate-400 border border-slate-700">
                  {{ product()?.sku || 'SKU-00' + product()?.id }}
                </span>
                <span class="text-[11px] text-slate-500 font-medium">Marketplace Verified Listing</span>
              </div>
              <h1 class="text-3xl sm:text-4xl font-extrabold font-['Outfit'] text-white tracking-tight leading-tight">
                {{ product()?.name }}
              </h1>
            </div>

            <!-- Price Card -->
            <div class="p-6 rounded-3xl bg-gradient-to-r from-slate-900/90 via-slate-900/60 to-slate-950 border border-slate-800 space-y-2">
              <span class="text-xs text-slate-400 uppercase tracking-wider font-semibold">Special Marketplace Price</span>
              <div class="flex items-baseline space-x-3">
                <span class="text-4xl font-extrabold text-white font-['Outfit']">
                  \${{ product()?.price?.toFixed(2) }}
                </span>
                <span class="text-xs text-emerald-400 font-semibold">Inclusive of all local merchant sales tax</span>
              </div>
            </div>

            <!-- Description -->
            <div class="space-y-2">
              <h3 class="text-xs font-bold uppercase tracking-wider text-slate-400">Product Description & Specs</h3>
              <div class="p-5 rounded-2xl bg-slate-900/60 border border-slate-800/80 text-xs text-slate-300 leading-relaxed whitespace-pre-line">
                {{ product()?.description || 'No detailed description provided by the seller.' }}
              </div>
            </div>

            <!-- Quantity & Actions -->
            @if (authService.isAdmin()) {
              <!-- Admin Notice (Cannot buy) -->
              <div class="p-5 rounded-2xl bg-purple-500/10 border border-purple-500/30 space-y-2">
                <div class="flex items-center space-x-2 text-purple-300 font-bold text-xs">
                  <span>🛡️</span>
                  <span>Marketplace Administrator Mode</span>
                </div>
                <p class="text-[11px] text-slate-400">
                  Administrative accounts govern the marketplace and do not place customer orders. To moderate this listing or verify changes, use the Trust & Compliance Console.
                </p>
                <div class="pt-1">
                  <a routerLink="/admin" class="inline-flex items-center px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs transition-colors">
                    Open Moderation Console
                  </a>
                </div>
              </div>
            } @else {
              <!-- Customer Purchasing Actions -->
              <div class="space-y-4 pt-2">
                <div class="flex items-center space-x-4">
                  <div class="flex items-center space-x-2 bg-slate-900 rounded-2xl border border-slate-800 p-1.5">
                    <button 
                      (click)="decrementQty()" 
                      [disabled]="quantity <= 1"
                      class="w-8 h-8 rounded-xl bg-slate-800 text-slate-300 hover:text-white disabled:opacity-30 flex items-center justify-center font-bold text-sm transition-colors">
                      -
                    </button>
                    <span class="text-sm font-bold px-3 text-white font-['Outfit']">{{ quantity }}</span>
                    <button 
                      (click)="incrementQty()" 
                      [disabled]="quantity >= (product()?.stockQuantity || 0)"
                      class="w-8 h-8 rounded-xl bg-slate-800 text-slate-300 hover:text-white disabled:opacity-30 flex items-center justify-center font-bold text-sm transition-colors">
                      +
                    </button>
                  </div>
                  <span class="text-xs text-slate-400">
                    Subtotal: <span class="font-bold text-white font-['Outfit']">\${{ ((product()?.price || 0) * quantity).toFixed(2) }}</span>
                  </span>
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <button 
                    (click)="addToCart()"
                    [disabled]="(product()?.stockQuantity || 0) <= 0"
                    class="py-3.5 px-6 rounded-2xl bg-slate-800 hover:bg-slate-700 text-white font-bold text-xs border border-slate-700 disabled:opacity-40 disabled:cursor-not-allowed flex items-center justify-center space-x-2 transition-all shadow-md active:scale-[0.99]">
                    <svg class="w-4 h-4 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                    </svg>
                    <span>Add to Cart</span>
                  </button>

                  <button 
                    (click)="buyNow()"
                    [disabled]="(product()?.stockQuantity || 0) <= 0"
                    class="py-3.5 px-6 rounded-2xl bg-gradient-to-r from-emerald-500 to-teal-400 hover:from-emerald-400 hover:to-teal-300 text-slate-950 font-extrabold text-xs disabled:opacity-40 disabled:cursor-not-allowed flex items-center justify-center space-x-2 transition-all shadow-xl shadow-emerald-500/20 active:scale-[0.99]">
                    <span>⚡ Buy Now</span>
                  </button>
                </div>
              </div>
            }

            <!-- Delivery Guarantees -->
            <div class="grid grid-cols-3 gap-3 pt-4 border-t border-slate-800/80 text-center">
              <div class="p-3 rounded-2xl bg-slate-900/40 border border-slate-800 space-y-1">
                <div class="text-base">🚀</div>
                <span class="text-[10px] font-semibold text-slate-300 block">Fast Dispatch</span>
                <span class="text-[9px] text-slate-500 block">Within 24-48 Hours</span>
              </div>
              <div class="p-3 rounded-2xl bg-slate-900/40 border border-slate-800 space-y-1">
                <div class="text-base">🔒</div>
                <span class="text-[10px] font-semibold text-slate-300 block">Secure Saga</span>
                <span class="text-[9px] text-slate-500 block">Stock Reservation</span>
              </div>
              <div class="p-3 rounded-2xl bg-slate-900/40 border border-slate-800 space-y-1">
                <div class="text-base">📦</div>
                <span class="text-[10px] font-semibold text-slate-300 block">Split Sub-Orders</span>
                <span class="text-[9px] text-slate-500 block">Per-Seller Tracking</span>
              </div>
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(-8px); }
      to { opacity: 1; transform: translateY(0); }
    }
    .animate-fade-in {
      animation: fadeIn 0.3s cubic-bezier(0.16, 1, 0.3, 1);
    }
  `]
})
export class ProductDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  authService = inject(AuthService);

  product = signal<Product | null>(null);
  loading = signal<boolean>(true);
  alertMessage = signal<string | null>(null);
  quantity = 1;

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const slug = params.get('slug');
      const id = params.get('id');
      if (slug) {
        this.loadProductBySlug(slug);
      } else if (id) {
        this.loadProductById(parseInt(id, 10));
      }
    });
  }

  loadProductBySlug(slug: string): void {
    this.loading.set(true);
    this.productService.getProductBySlug(slug).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.product.set(res.data);
        }
        this.loading.set(false);
      },
      error: () => {
        const numId = parseInt(slug, 10);
        if (!isNaN(numId)) {
          this.loadProductById(numId);
        } else {
          this.loading.set(false);
        }
      }
    });
  }

  loadProductById(id: number): void {
    this.loading.set(true);
    this.productService.getProductById(id).subscribe({
      next: (res) => {
        if (res.success && res.data) {
          this.product.set(res.data);
        }
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  incrementQty(): void {
    const currentProduct = this.product();
    if (currentProduct && this.quantity < currentProduct.stockQuantity) {
      this.quantity++;
    }
  }

  decrementQty(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  addToCart(): void {
    const p = this.product();
    if (!p) return;
    this.cartService.addToCart(p, this.quantity);
    this.alertMessage.set(`Added ${this.quantity}x "${p.name}" to your cart!`);
  }

  buyNow(): void {
    const p = this.product();
    if (!p) return;
    this.cartService.addToCart(p, this.quantity);
    this.router.navigate(['/checkout']);
  }

  getSellerName(sellerId: number): string {
    if (sellerId === 2) return 'Nordic Home & Living';
    return 'Apex Electronics Store';
  }
}
