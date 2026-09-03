import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { Category, Product } from '../../models/product.model';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="space-y-10">
      <!-- Hero Section -->
      <section class="relative overflow-hidden rounded-3xl bg-gradient-to-br from-slate-900 via-slate-900 to-slate-950 border border-slate-800/80 p-8 sm:p-12 shadow-2xl">
        <div class="absolute -right-20 -top-20 w-96 h-96 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div class="absolute -left-20 -bottom-20 w-96 h-96 bg-teal-500/10 rounded-full blur-3xl pointer-events-none"></div>
        
        <div class="relative z-10 max-w-3xl space-y-5">
          <div class="inline-flex items-center space-x-2 px-3 py-1.5 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs font-semibold">
            <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
            <span>Multi-Seller Verified Marketplace</span>
          </div>
          
          <h1 class="text-3xl sm:text-5xl font-extrabold tracking-tight font-['Outfit'] leading-tight text-white">
            Discover Quality Products from <span class="bg-gradient-to-r from-emerald-400 via-teal-300 to-cyan-400 bg-clip-text text-transparent">Trusted Sellers</span>
          </h1>
          
          <p class="text-slate-300 text-xs sm:text-sm leading-relaxed max-w-2xl">
            Browse verified listings from independent merchants with automated stock reservation, per-seller split shipments, and full order lifecycle tracking.
          </p>

          <div class="flex flex-wrap items-center gap-3 pt-2">
            <div class="px-4 py-2 rounded-xl bg-slate-950/60 border border-slate-800 flex items-center space-x-2">
              <span class="text-xs text-slate-400">Approved Listings:</span>
              <span class="text-sm font-bold text-white">{{ products().length }}</span>
            </div>
            <div class="px-4 py-2 rounded-xl bg-slate-950/60 border border-slate-800 flex items-center space-x-2">
              <span class="text-xs text-slate-400">Platform:</span>
              <span class="text-xs font-semibold text-emerald-400">Spring Cloud + Microservices</span>
            </div>
          </div>
        </div>
      </section>

      <!-- Alert Toast -->
      @if (alertMessage()) {
        <div class="p-3.5 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 text-xs flex items-center justify-between shadow-md">
          <div class="flex items-center space-x-2">
            <span>✅</span>
            <span>{{ alertMessage() }}</span>
          </div>
          <div class="flex items-center space-x-3">
            <a routerLink="/cart" class="underline font-bold hover:text-emerald-300">View Cart</a>
            <button (click)="alertMessage.set(null)" class="text-slate-400 hover:text-white">&times;</button>
          </div>
        </div>
      }

      <!-- Search & Filters -->
      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <!-- Search Input -->
        <div class="relative flex-1 max-w-md">
          <svg class="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <input 
            type="text" 
            [(ngModel)]="searchQuery" 
            (input)="onSearchChange()"
            placeholder="Search approved products, categories, keywords..." 
            class="w-full pl-10 pr-4 py-2.5 bg-slate-900/90 border border-slate-800 rounded-xl text-sm text-slate-200 placeholder-slate-500 focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-all duration-200"
          />
        </div>

        <!-- Category Pills -->
        <div class="flex items-center space-x-2 overflow-x-auto pb-2 md:pb-0">
          <button 
            (click)="selectCategory(undefined)"
            [class]="selectedCategoryId === undefined ? 'bg-emerald-500 text-slate-950 font-bold' : 'bg-slate-900 text-slate-300 hover:bg-slate-800 border border-slate-800'"
            class="px-4 py-2 rounded-xl text-xs whitespace-nowrap transition-all duration-200">
            All Products
          </button>
          @for (cat of categories(); track cat.id) {
            <button 
              (click)="selectCategory(cat.id)"
              [class]="selectedCategoryId === cat.id ? 'bg-emerald-500 text-slate-950 font-bold' : 'bg-slate-900 text-slate-300 hover:bg-slate-800 border border-slate-800'"
              class="px-4 py-2 rounded-xl text-xs whitespace-nowrap transition-all duration-200">
              {{ cat.name }}
            </button>
          }
        </div>
      </div>

      <!-- Products Grid -->
      @if (loading()) {
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          @for (i of [1,2,3,4,5,6,7,8]; track i) {
            <div class="glass-card rounded-2xl p-4 animate-pulse space-y-4">
              <div class="w-full h-48 bg-slate-800 rounded-xl"></div>
              <div class="h-4 bg-slate-800 rounded w-3/4"></div>
              <div class="h-4 bg-slate-800 rounded w-1/2"></div>
            </div>
          }
        </div>
      } @else if (products().length === 0) {
        <div class="glass-card rounded-3xl p-16 text-center space-y-4">
          <div class="w-16 h-16 mx-auto rounded-full bg-slate-800 flex items-center justify-center text-slate-400">
            <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
            </svg>
          </div>
          <h3 class="text-lg font-bold text-slate-200">No approved products found</h3>
          <p class="text-xs text-slate-400 max-w-sm mx-auto">Try clearing search filters or checking other category tabs.</p>
        </div>
      } @else {
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          @for (product of products(); track product.id) {
            <div class="glass-card rounded-2xl overflow-hidden flex flex-col group border border-slate-800/80 hover:border-emerald-500/40 transition-all duration-300">
              <!-- Image Container (Clickable to details) -->
              <a [routerLink]="['/products', product.slug || product.id]" class="relative h-48 bg-slate-900 overflow-hidden flex items-center justify-center cursor-pointer">
                @if (product.imageUrl) {
                  <img [src]="product.imageUrl" [alt]="product.name" class="w-full h-full object-contain p-2 group-hover:scale-105 transition-transform duration-500" />
                } @else {
                  <div class="text-slate-600">
                    <svg class="w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                }
                
                <!-- Stock Badge -->
                <div class="absolute top-3 right-3">
                  @if (product.stockQuantity > 5) {
                    <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-emerald-500/90 text-slate-950 backdrop-blur-md shadow">
                      {{ product.stockQuantity }} in stock
                    </span>
                  } @else if (product.stockQuantity > 0) {
                    <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-amber-500/90 text-slate-950 backdrop-blur-md shadow animate-pulse">
                      Only {{ product.stockQuantity }} left
                    </span>
                  } @else {
                    <span class="px-2.5 py-1 rounded-full text-[10px] font-bold bg-rose-500/90 text-white backdrop-blur-md">
                      Out of Stock
                    </span>
                  }
                </div>

                @if (product.categoryName) {
                  <div class="absolute bottom-3 left-3">
                    <span class="px-2.5 py-1 rounded-md text-[10px] font-medium bg-slate-950/80 text-slate-300 border border-slate-800 backdrop-blur-md">
                      {{ product.categoryName }}
                    </span>
                  </div>
                }
              </a>

              <!-- Product Details -->
              <div class="p-5 flex-1 flex flex-col justify-between space-y-4">
                <div class="space-y-1.5">
                  <div class="flex items-center justify-between text-[10px] text-slate-400">
                    <span class="font-medium text-emerald-400">🏪 {{ getSellerName(product.sellerId) }}</span>
                  </div>
                  <a [routerLink]="['/products', product.slug || product.id]" class="block font-bold text-base text-slate-100 group-hover:text-emerald-400 transition-colors line-clamp-1">
                    {{ product.name }}
                  </a>
                  <p class="text-xs text-slate-400 line-clamp-2 leading-relaxed">
                    {{ product.description }}
                  </p>
                </div>

                <div class="pt-3 border-t border-slate-800/80 flex items-center justify-between">
                  <div>
                    <span class="text-[10px] text-slate-400 uppercase tracking-wider block">Price</span>
                    <span class="text-lg font-bold text-white font-['Outfit']">
                      \${{ product.price.toFixed(2) }}
                    </span>
                  </div>

                  @if (authService.isAdmin()) {
                    <a [routerLink]="['/products', product.slug || product.id]" class="px-3 py-1.5 rounded-xl bg-purple-600/20 text-purple-300 hover:bg-purple-600/40 text-xs font-semibold border border-purple-500/30 transition-colors">
                      Inspect 🔍
                    </a>
                  } @else {
                    <div class="flex items-center space-x-1.5">
                      <a [routerLink]="['/products', product.slug || product.id]" class="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold transition-colors" title="View details">
                        👁️
                      </a>
                      <button 
                        (click)="addToCart(product)"
                        [disabled]="product.stockQuantity <= 0"
                        class="px-3 py-2 rounded-xl bg-emerald-500 hover:bg-emerald-400 disabled:opacity-40 disabled:cursor-not-allowed text-slate-950 font-bold text-xs flex items-center space-x-1 shadow-md shadow-emerald-500/10 active:scale-95 transition-all duration-200">
                        <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
                        </svg>
                        <span>Add</span>
                      </button>
                    </div>
                  }
                </div>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `
})
export class CatalogComponent implements OnInit {
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  authService = inject(AuthService);

  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  loading = signal<boolean>(true);
  alertMessage = signal<string | null>(null);

  searchQuery = '';
  selectedCategoryId?: number;

  ngOnInit(): void {
    this.loadCategories();
    this.loadProducts();
  }

  loadCategories(): void {
    this.productService.getCategories().subscribe({
      next: (res) => {
        if (res.success) {
          this.categories.set(res.data);
        }
      },
      error: (err) => console.error('Failed to load categories:', err)
    });
  }

  loadProducts(): void {
    this.loading.set(true);
    this.productService.getProducts(this.selectedCategoryId, this.searchQuery).subscribe({
      next: (res) => {
        if (res.success) {
          this.products.set(res.data.content);
        }
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Failed to load products:', err);
        this.loading.set(false);
      }
    });
  }

  selectCategory(categoryId?: number): void {
    this.selectedCategoryId = categoryId;
    this.loadProducts();
  }

  onSearchChange(): void {
    this.loadProducts();
  }

  addToCart(product: Product): void {
    this.cartService.addToCart(product);
    this.alertMessage.set(`Added 1x "${product.name}" to your cart!`);
  }

  getSellerName(sellerId: number): string {
    if (sellerId === 2) return 'Nordic Home & Living';
    return 'Apex Electronics Store';
  }
}

