import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { SellerService } from '../../services/seller.service';
import { OrderService } from '../../services/order.service';
import { Product, ProductAuditLog } from '../../models/product.model';
import { SellerProfile, User, UserStatus } from '../../models/user.model';
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
            <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-purple-500/20 text-purple-400 border border-purple-500/30">MARKETPLACE MODERATION & GOVERNANCE</span>
            <h1 class="text-3xl font-extrabold font-['Outfit'] text-white">Trust & Compliance Console</h1>
          </div>
          <p class="text-xs text-slate-400 mt-1">
            Supervise seller onboardings, moderate catalog product submissions, govern customer accounts, and inspect platform split orders.
          </p>
        </div>

        <button (click)="loadData()" class="self-start sm:self-auto px-4 py-2 rounded-xl bg-slate-900 hover:bg-slate-800 border border-slate-700 text-xs font-semibold text-slate-300 transition-colors flex items-center space-x-2">
          <span>🔄</span>
          <span>Refresh Metrics</span>
        </button>
      </div>

      <!-- Action Center KPI Cards -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <!-- KPI 1: Pending Products -->
        <div (click)="activeTab = 'product_review'" class="cursor-pointer glass-card rounded-2xl p-5 border border-slate-800 hover:border-amber-500/40 transition-all space-y-2">
          <div class="flex items-center justify-between">
            <span class="text-xs font-semibold text-slate-400 uppercase tracking-wider">Pending Products</span>
            <span class="w-8 h-8 rounded-xl bg-amber-500/10 text-amber-400 flex items-center justify-center text-sm">📦</span>
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-white font-['Outfit']">{{ pendingProducts().length }}</span>
            <span class="text-[11px] text-amber-400 font-medium">Awaiting review</span>
          </div>
        </div>

        <!-- KPI 2: Pending Sellers -->
        <div (click)="activeTab = 'seller_moderation'" class="cursor-pointer glass-card rounded-2xl p-5 border border-slate-800 hover:border-cyan-500/40 transition-all space-y-2">
          <div class="flex items-center justify-between">
            <span class="text-xs font-semibold text-slate-400 uppercase tracking-wider">Seller Applications</span>
            <span class="w-8 h-8 rounded-xl bg-cyan-500/10 text-cyan-400 flex items-center justify-center text-sm">🏪</span>
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-white font-['Outfit']">{{ pendingSellers().length }}</span>
            <span class="text-[11px] text-cyan-400 font-medium">Verification pending</span>
          </div>
        </div>

        <!-- KPI 3: Total Active Listings -->
        <div (click)="activeTab = 'catalog_inventory'" class="cursor-pointer glass-card rounded-2xl p-5 border border-slate-800 hover:border-emerald-500/40 transition-all space-y-2">
          <div class="flex items-center justify-between">
            <span class="text-xs font-semibold text-slate-400 uppercase tracking-wider">Catalog Inventory</span>
            <span class="w-8 h-8 rounded-xl bg-emerald-500/10 text-emerald-400 flex items-center justify-center text-sm">🏷️</span>
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-white font-['Outfit']">{{ allProducts().length }}</span>
            <span class="text-[11px] text-slate-400 font-medium">Total products</span>
          </div>
        </div>

        <!-- KPI 4: Registered Customers -->
        <div (click)="activeTab = 'customer_management'" class="cursor-pointer glass-card rounded-2xl p-5 border border-slate-800 hover:border-purple-500/40 transition-all space-y-2">
          <div class="flex items-center justify-between">
            <span class="text-xs font-semibold text-slate-400 uppercase tracking-wider">Registered Buyers</span>
            <span class="w-8 h-8 rounded-xl bg-purple-500/10 text-purple-400 flex items-center justify-center text-sm">👥</span>
          </div>
          <div class="flex items-baseline space-x-2">
            <span class="text-3xl font-extrabold text-white font-['Outfit']">{{ customers().length }}</span>
            <span class="text-[11px] text-purple-400 font-medium">Marketplace users</span>
          </div>
        </div>
      </div>

      <!-- Tab Controls Navigation -->
      <div class="flex items-center space-x-2 overflow-x-auto pb-2 border-b border-slate-800/80">
        <button 
          (click)="activeTab = 'product_review'" 
          [class]="activeTab === 'product_review' ? 'bg-purple-600 text-white font-bold shadow-md shadow-purple-600/20' : 'bg-slate-900/80 text-slate-400 hover:text-white border border-slate-800'"
          class="px-4 py-2.5 rounded-xl text-xs whitespace-nowrap transition-all duration-200 flex items-center space-x-2">
          <span>📦 Product Approvals</span>
          @if (pendingProducts().length > 0) {
            <span class="px-1.5 py-0.5 rounded-full text-[10px] bg-amber-500 text-slate-950 font-extrabold">{{ pendingProducts().length }}</span>
          }
        </button>

        <button 
          (click)="activeTab = 'catalog_inventory'" 
          [class]="activeTab === 'catalog_inventory' ? 'bg-purple-600 text-white font-bold shadow-md shadow-purple-600/20' : 'bg-slate-900/80 text-slate-400 hover:text-white border border-slate-800'"
          class="px-4 py-2.5 rounded-xl text-xs whitespace-nowrap transition-all duration-200">
          🏷️ All Catalog Listings ({{ allProducts().length }})
        </button>

        <button 
          (click)="activeTab = 'seller_moderation'" 
          [class]="activeTab === 'seller_moderation' ? 'bg-purple-600 text-white font-bold shadow-md shadow-purple-600/20' : 'bg-slate-900/80 text-slate-400 hover:text-white border border-slate-800'"
          class="px-4 py-2.5 rounded-xl text-xs whitespace-nowrap transition-all duration-200 flex items-center space-x-2">
          <span>🏪 Seller Verifications</span>
          @if (pendingSellers().length > 0) {
            <span class="px-1.5 py-0.5 rounded-full text-[10px] bg-cyan-500 text-slate-950 font-extrabold">{{ pendingSellers().length }}</span>
          }
        </button>

        <button 
          (click)="activeTab = 'customer_management'" 
          [class]="activeTab === 'customer_management' ? 'bg-purple-600 text-white font-bold shadow-md shadow-purple-600/20' : 'bg-slate-900/80 text-slate-400 hover:text-white border border-slate-800'"
          class="px-4 py-2.5 rounded-xl text-xs whitespace-nowrap transition-all duration-200">
          👥 Customer Management ({{ customers().length }})
        </button>

        <button 
          (click)="activeTab = 'platform_orders'" 
          [class]="activeTab === 'platform_orders' ? 'bg-purple-600 text-white font-bold shadow-md shadow-purple-600/20' : 'bg-slate-900/80 text-slate-400 hover:text-white border border-slate-800'"
          class="px-4 py-2.5 rounded-xl text-xs whitespace-nowrap transition-all duration-200">
          📋 Platform Orders Audit ({{ platformOrders().length }})
        </button>
      </div>

      <!-- Alert Toast -->
      @if (alertMessage()) {
        <div class="p-4 rounded-2xl bg-purple-500/10 border border-purple-500/30 text-purple-300 text-xs flex items-center justify-between shadow-lg">
          <div class="flex items-center space-x-2">
            <span>🛡️</span>
            <span>{{ alertMessage() }}</span>
          </div>
          <button (click)="alertMessage.set(null)" class="text-slate-400 hover:text-white">&times;</button>
        </div>
      }

      <!-- TAB 1: PRODUCT APPROVALS -->
      @if (activeTab === 'product_review') {
        <div class="space-y-6">
          <div>
            <h3 class="text-lg font-bold text-white">Pending Product Submissions</h3>
            <p class="text-xs text-slate-400">Approve compliant merchant listings or reject with explicit feedback to trigger seller notification.</p>
          </div>

          <div class="glass-card rounded-3xl overflow-hidden border border-slate-800 shadow-xl">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/90 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">Product Details</th>
                    <th class="p-4">Merchant ID</th>
                    <th class="p-4">Price</th>
                    <th class="p-4">Inventory</th>
                    <th class="p-4">Submitted At</th>
                    <th class="p-4 text-right">Moderation Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/60">
                  @for (p of pendingProducts(); track p.id) {
                    <tr class="hover:bg-slate-800/30 transition-colors">
                      <td class="p-4 font-medium text-white flex items-center space-x-3">
                        <div class="w-12 h-12 rounded-xl bg-slate-800 overflow-hidden flex-shrink-0 border border-slate-700 flex items-center justify-center">
                          @if (p.imageUrl) {
                            <img [src]="p.imageUrl" [alt]="p.name" class="w-full h-full object-cover" />
                          } @else {
                            <span class="text-slate-600 text-[10px]">No Img</span>
                          }
                        </div>
                        <div>
                          <span class="block font-bold text-sm text-white">{{ p.name }}</span>
                          <span class="text-[11px] text-slate-400 line-clamp-1">{{ p.description }}</span>
                        </div>
                      </td>
                      <td class="p-4 font-mono text-cyan-400 font-semibold">Seller #{{ p.sellerId }}</td>
                      <td class="p-4 font-bold text-white font-['Outfit']">\${{ p.price.toFixed(2) }}</td>
                      <td class="p-4">{{ p.stockQuantity }} units</td>
                      <td class="p-4 text-slate-500">{{ p.createdAt | date:'short' }}</td>
                      <td class="p-4 text-right space-x-2">
                        <button (click)="approveProduct(p.id)" class="px-3.5 py-1.5 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs shadow-md shadow-emerald-500/20 transition-all">
                          Approve ✅
                        </button>
                        <button (click)="openRejectModal(p.id)" class="px-3.5 py-1.5 rounded-xl bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 border border-rose-500/30 font-bold text-xs transition-colors">
                          Reject ❌
                        </button>
                        <button (click)="viewAuditLogs(p.id)" class="px-2.5 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs border border-slate-700 transition-colors">
                          Audit Log
                        </button>
                        <button (click)="deleteProduct(p.id)" class="px-2.5 py-1.5 rounded-xl bg-rose-950/60 hover:bg-rose-900 text-rose-300 text-xs border border-rose-800 transition-colors" title="Delete listing">
                          🗑️
                        </button>
                      </td>
                    </tr>
                  }
                  @if (pendingProducts().length === 0) {
                    <tr>
                      <td colspan="6" class="p-12 text-center text-slate-500">
                        🎉 All product submissions are up to date! No pending review items.
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- TAB 2: CATALOG INVENTORY -->
      @if (activeTab === 'catalog_inventory') {
        <div class="space-y-6">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h3 class="text-lg font-bold text-white">Full Marketplace Catalog</h3>
              <p class="text-xs text-slate-400">Search and audit all active, rejected, and suspended products across all sellers.</p>
            </div>
            <div class="flex items-center space-x-3">
              <input 
                type="text" 
                [(ngModel)]="searchQuery" 
                placeholder="Search products by title, sku, seller..." 
                class="px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500" />
            </div>
          </div>

          <div class="glass-card rounded-3xl overflow-hidden border border-slate-800 shadow-xl">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/90 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">ID</th>
                    <th class="p-4">Product</th>
                    <th class="p-4">Merchant</th>
                    <th class="p-4">Price / Stock</th>
                    <th class="p-4">Status</th>
                    <th class="p-4 text-right">Action</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/60">
                  @for (p of filteredAllProducts(); track p.id) {
                    <tr class="hover:bg-slate-800/30 transition-colors">
                      <td class="p-4 font-mono text-slate-500">#{{ p.id }}</td>
                      <td class="p-4 font-medium text-white flex items-center space-x-3">
                        <div class="w-10 h-10 rounded-xl bg-slate-800 overflow-hidden flex-shrink-0 border border-slate-700 flex items-center justify-center">
                          @if (p.imageUrl) {
                            <img [src]="p.imageUrl" [alt]="p.name" class="w-full h-full object-cover" />
                          } @else {
                            <span class="text-slate-600 text-[10px]">No Img</span>
                          }
                        </div>
                        <div>
                          <span class="block font-bold text-sm text-white">{{ p.name }}</span>
                          <span class="text-[10px] text-slate-500 font-mono">{{ p.sku || 'SKU-00' + p.id }}</span>
                        </div>
                      </td>
                      <td class="p-4 font-mono text-cyan-400">Seller #{{ p.sellerId }}</td>
                      <td class="p-4">
                        <span class="font-bold text-white font-['Outfit']">\${{ p.price.toFixed(2) }}</span>
                        <span class="block text-[10px] text-slate-400">{{ p.stockQuantity }} units</span>
                      </td>
                      <td class="p-4">
                        <span [class]="getProductStatusClass(p.status)" class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border">
                          {{ p.status }}
                        </span>
                      </td>
                      <td class="p-4 text-right space-x-2">
                        @if (p.status === 'ACTIVE') {
                          <button (click)="suspendProduct(p.id)" class="px-2.5 py-1.5 rounded-lg bg-amber-500/10 hover:bg-amber-500/20 text-amber-400 border border-amber-500/30 text-xs font-semibold">
                            Suspend
                          </button>
                        } @else if (p.status === 'SUSPENDED' || p.status === 'REJECTED') {
                          <button (click)="approveProduct(p.id)" class="px-2.5 py-1.5 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 text-xs font-semibold">
                            Approve
                          </button>
                        }
                        <button (click)="viewAuditLogs(p.id)" class="px-2.5 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs border border-slate-700">
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
                      <td colspan="6" class="p-12 text-center text-slate-500">No matching products found.</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- TAB 3: SELLER MODERATION -->
      @if (activeTab === 'seller_moderation') {
        <div class="space-y-6">
          <div>
            <h3 class="text-lg font-bold text-white">Seller Onboarding Applications</h3>
            <p class="text-xs text-slate-400">Verify business registration credentials and approve verified merchant accounts.</p>
          </div>

          <div class="glass-card rounded-3xl overflow-hidden border border-slate-800 shadow-xl">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/90 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">Store Profile</th>
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
                        <span class="block text-[10px] text-slate-400 font-mono">{{ s.storeSlug }}</span>
                        @if (s.storeDescription) {
                          <span class="block text-[11px] text-slate-400 font-normal mt-0.5 line-clamp-1">{{ s.storeDescription }}</span>
                        }
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
                          <button (click)="approveSeller(s.id)" class="px-3.5 py-1.5 rounded-xl bg-emerald-500 text-slate-950 font-bold text-xs shadow-md shadow-emerald-500/20">
                            Approve
                          </button>
                          <button (click)="rejectSeller(s.id)" class="px-3.5 py-1.5 rounded-xl bg-rose-500/10 text-rose-400 border border-rose-500/30 text-xs font-bold">
                            Reject
                          </button>
                        } @else if (s.verificationStatus === 'APPROVED') {
                          <button (click)="suspendSeller(s.id)" class="px-3 py-1.5 rounded-xl bg-amber-500/10 text-amber-400 border border-amber-500/30 text-xs font-bold">
                            Suspend
                          </button>
                        } @else {
                          <button (click)="approveSeller(s.id)" class="px-3 py-1.5 rounded-xl bg-slate-800 text-slate-300 text-xs font-bold hover:bg-slate-700">
                            Reactivate
                          </button>
                        }
                      </td>
                    </tr>
                  }
                  @if (allSellers().length === 0) {
                    <tr>
                      <td colspan="5" class="p-12 text-center text-slate-500">No seller applications on record.</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- TAB 4: CUSTOMER MANAGEMENT -->
      @if (activeTab === 'customer_management') {
        <div class="space-y-6">
          <!-- Customer Management Header & Controls -->
          <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <div class="flex items-center space-x-2">
                <span class="text-xl">👥</span>
                <h3 class="text-lg font-bold text-white font-['Outfit']">Marketplace Customer Accounts & Governance</h3>
              </div>
              <p class="text-xs text-slate-400 mt-1">Manage buyer accounts, inspect delivery destinations, reset credentials, review orders, and moderate account access.</p>
            </div>

            <div class="flex items-center space-x-3">
              <button (click)="openCreateCustomerModal()" class="px-4 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs shadow-lg shadow-purple-600/20 transition-all flex items-center space-x-1.5 cursor-pointer">
                <span>➕</span>
                <span>Add Customer</span>
              </button>
            </div>
          </div>

          <!-- Customer Stats Banner -->
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div class="glass-card rounded-2xl p-4 border border-slate-800 flex items-center justify-between">
              <div>
                <p class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Total Customers</p>
                <p class="text-2xl font-black text-white font-['Outfit']">{{ customers().length }}</p>
              </div>
              <span class="w-10 h-10 rounded-xl bg-purple-500/10 text-purple-400 flex items-center justify-center text-lg">👥</span>
            </div>
            <div class="glass-card rounded-2xl p-4 border border-slate-800 flex items-center justify-between">
              <div>
                <p class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Active Buyers</p>
                <p class="text-2xl font-black text-emerald-400 font-['Outfit']">{{ activeCustomersCount() }}</p>
              </div>
              <span class="w-10 h-10 rounded-xl bg-emerald-500/10 text-emerald-400 flex items-center justify-center text-lg">✅</span>
            </div>
            <div class="glass-card rounded-2xl p-4 border border-slate-800 flex items-center justify-between">
              <div>
                <p class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Suspended Accounts</p>
                <p class="text-2xl font-black text-rose-400 font-['Outfit']">{{ suspendedCustomersCount() }}</p>
              </div>
              <span class="w-10 h-10 rounded-xl bg-rose-500/10 text-rose-400 flex items-center justify-center text-lg">🚫</span>
            </div>
          </div>

          <!-- Filter & Search Bar -->
          <div class="flex flex-col sm:flex-row items-center justify-between gap-3 bg-slate-900/60 p-3 rounded-2xl border border-slate-800">
            <div class="flex items-center space-x-1.5 overflow-x-auto w-full sm:w-auto">
              <button (click)="customerStatusFilter = 'ALL'"
                      [class]="customerStatusFilter === 'ALL' ? 'bg-purple-600 text-white font-bold' : 'text-slate-400 hover:text-white bg-slate-800/60'"
                      class="px-3 py-1.5 rounded-lg text-xs transition-colors whitespace-nowrap cursor-pointer">
                All ({{ customers().length }})
              </button>
              <button (click)="customerStatusFilter = 'ACTIVE'"
                      [class]="customerStatusFilter === 'ACTIVE' ? 'bg-emerald-600 text-white font-bold' : 'text-slate-400 hover:text-white bg-slate-800/60'"
                      class="px-3 py-1.5 rounded-lg text-xs transition-colors whitespace-nowrap cursor-pointer">
                Active ({{ activeCustomersCount() }})
              </button>
              <button (click)="customerStatusFilter = 'SUSPENDED'"
                      [class]="customerStatusFilter === 'SUSPENDED' ? 'bg-rose-600 text-white font-bold' : 'text-slate-400 hover:text-white bg-slate-800/60'"
                      class="px-3 py-1.5 rounded-lg text-xs transition-colors whitespace-nowrap cursor-pointer">
                Suspended ({{ suspendedCustomersCount() }})
              </button>
            </div>

            <div class="w-full sm:w-72 relative">
              <input 
                type="text" 
                [(ngModel)]="customerSearchQuery" 
                placeholder="Search name, email, phone, city..." 
                class="w-full px-3.5 py-1.5 rounded-xl bg-slate-950 border border-slate-800 text-xs text-white placeholder-slate-500 focus:outline-none focus:border-purple-500 pl-8" />
              <span class="absolute left-2.5 top-2 text-xs text-slate-500">🔍</span>
            </div>
          </div>

          <!-- Customers Table -->
          <div class="glass-card rounded-3xl overflow-hidden border border-slate-800 shadow-xl">
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs text-slate-300">
                <thead class="bg-slate-900/90 text-[11px] uppercase tracking-wider text-slate-400 border-b border-slate-800">
                  <tr>
                    <th class="p-4">Customer</th>
                    <th class="p-4">Contact & Location</th>
                    <th class="p-4">Status</th>
                    <th class="p-4">Marketplace Orders</th>
                    <th class="p-4">Registered</th>
                    <th class="p-4 text-right">Moderation Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/60">
                  @for (c of filteredCustomers(); track c.id) {
                    <tr class="hover:bg-slate-800/30 transition-colors">
                      <td class="p-4">
                        <div class="flex items-center space-x-3">
                          <div class="w-9 h-9 rounded-xl flex items-center justify-center font-bold text-xs overflow-hidden border border-slate-700/60 bg-gradient-to-tr from-emerald-600 to-teal-400 text-slate-950 flex-shrink-0">
                            @if (c.avatarUrl && !isAvatarFailed(c.id)) {
                              <img [src]="c.avatarUrl" alt="Avatar" class="w-full h-full object-cover" (error)="markAvatarFailed(c.id)" />
                            } @else {
                              <span>{{ getInitials(c.firstName, c.lastName) }}</span>
                            }
                          </div>
                          <div>
                            <span class="font-bold text-white block">{{ c.firstName }} {{ c.lastName }}</span>
                            <span class="text-slate-400 text-[11px] font-mono">{{ c.email }}</span>
                            <span class="text-slate-500 text-[10px] font-mono block">ID: #{{ c.id }}</span>
                          </div>
                        </div>
                      </td>
                      <td class="p-4">
                        <div class="space-y-0.5">
                          <span class="text-slate-300 block font-mono">{{ c.phoneNumber || 'No phone set' }}</span>
                          @if (c.city || c.address) {
                            <span class="text-slate-400 text-[11px] block">{{ c.address ? c.address + ', ' : '' }}{{ c.city || '' }} {{ c.postalCode || '' }}</span>
                          } @else {
                            <span class="text-slate-500 text-[11px] italic block">Address not provided</span>
                          }
                        </div>
                      </td>
                      <td class="p-4">
                        <span [class]="c.status === 'ACTIVE' ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30' : 'bg-rose-500/10 text-rose-400 border-rose-500/30'" class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border">
                          {{ c.status }}
                        </span>
                      </td>
                      <td class="p-4">
                        <span class="px-2.5 py-1 rounded-lg bg-slate-800/80 text-slate-300 font-mono text-[11px] font-bold border border-slate-700/80 inline-flex items-center space-x-1">
                          <span>📦</span>
                          <span>{{ getCustomerOrderCount(c.email) }} Orders</span>
                        </span>
                      </td>
                      <td class="p-4 text-slate-400 font-mono text-[11px]">
                        {{ c.createdAt ? (c.createdAt | date:'mediumDate') : 'Sept 2026' }}
                      </td>
                      <td class="p-4 text-right space-x-1.5 whitespace-nowrap">
                        <button (click)="inspectCustomer(c)" title="Inspect Profile & Orders" class="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors cursor-pointer">
                          👁️
                        </button>
                        <button (click)="openEditCustomerModal(c)" title="Edit Customer Details" class="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 hover:text-white transition-colors cursor-pointer">
                          ✏️
                        </button>
                        <button (click)="openResetPasswordModal(c)" title="Reset Customer Password" class="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-amber-400 hover:text-amber-300 transition-colors cursor-pointer">
                          🔑
                        </button>
                        @if (c.status === 'ACTIVE') {
                          <button (click)="toggleCustomerStatus(c.id, 'SUSPENDED')" title="Suspend Account" class="px-2.5 py-1.5 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 border border-rose-500/30 font-bold text-xs transition-colors cursor-pointer">
                            🚫 Suspend
                          </button>
                        } @else {
                          <button (click)="toggleCustomerStatus(c.id, 'ACTIVE')" title="Reactivate Account" class="px-2.5 py-1.5 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 font-bold text-xs transition-colors cursor-pointer">
                            🟢 Activate
                          </button>
                        }
                        <button (click)="deleteCustomer(c)" title="Delete Account" class="p-1.5 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 hover:text-rose-300 border border-rose-500/30 transition-colors cursor-pointer">
                          🗑️
                        </button>
                      </td>
                    </tr>
                  }
                  @if (filteredCustomers().length === 0) {
                    <tr>
                      <td colspan="6" class="p-12 text-center text-slate-500">
                        No customer accounts found matching your filter criteria.
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- TAB 5: PLATFORM ORDERS -->
      @if (activeTab === 'platform_orders') {
        <div class="space-y-6">
          <div>
            <h3 class="text-lg font-bold text-white">Platform Orders (View-Only Dispute & Fulfillment Audit)</h3>
            <p class="text-xs text-slate-400">Inspect multi-seller split orders and track logistics progress across carriers.</p>
          </div>

          <div class="space-y-4">
            @for (order of platformOrders(); track order.id) {
              <div class="glass-card rounded-3xl p-6 border border-slate-800 space-y-4 shadow-xl">
                <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-800/80 pb-3">
                  <div class="flex items-center space-x-3">
                    <span class="font-mono font-bold text-emerald-400 text-sm">{{ order.orderNumber }}</span>
                    <span class="text-xs text-slate-400">Buyer: {{ order.customerEmail }}</span>
                  </div>
                  <div class="flex items-center space-x-3">
                    <span class="font-bold text-white font-['Outfit']">\${{ order.totalAmount.toFixed(2) }}</span>
                    <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-indigo-500/10 text-indigo-400 border border-indigo-500/30">
                      {{ order.derivedStatus }}
                    </span>
                  </div>
                </div>

                <!-- Sub-Orders Breakdown -->
                <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                  @for (sub of order.subOrders; track sub.id) {
                    <div class="bg-slate-900/90 rounded-2xl p-4 border border-slate-800 space-y-2.5">
                      <div class="flex justify-between items-center text-xs">
                        <span class="font-bold text-white">Shipment (Merchant #{{ sub.sellerId }})</span>
                        <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-slate-800 text-slate-300 border border-slate-700">
                          {{ sub.status }}
                        </span>
                      </div>
                      <div class="text-[11px] text-slate-400 font-mono">
                        {{ sub.subOrderNumber }}
                      </div>
                      <div class="text-xs text-slate-300 space-y-1 pt-1 border-t border-slate-800">
                        @for (i of sub.items; track i.id) {
                          <div class="flex justify-between">
                            <span>{{ i.quantity }}x {{ i.productName }}</span>
                            <span class="font-semibold text-white font-['Outfit']">\${{ i.subtotal.toFixed(2) }}</span>
                          </div>
                        }
                      </div>
                      @if (sub.trackingCode) {
                        <div class="text-[10px] text-emerald-400 font-mono pt-1">
                          🚚 {{ sub.carrier }}: {{ sub.trackingCode }}
                        </div>
                      }
                    </div>
                  }
                </div>
              </div>
            }
            @if (platformOrders().length === 0) {
              <div class="glass-card rounded-3xl p-12 text-center text-slate-500">
                No customer marketplace orders placed yet.
              </div>
            }
          </div>
        </div>
      }

      <!-- Rejection Modal -->
      @if (rejectingProductId()) {
        <div class="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4">
          <div class="glass-card rounded-3xl p-6 max-w-md w-full border border-slate-700 space-y-4 shadow-2xl">
            <h3 class="text-base font-bold text-white">Reject Product Listing</h3>
            <p class="text-xs text-slate-400">Please provide a clear reason so the seller can correct and resubmit.</p>

            <div class="space-y-2">
              <label class="text-xs font-medium text-slate-300">Rejection Reason</label>
              <textarea [(ngModel)]="rejectionReason" rows="3" placeholder="e.g. Incomplete specifications, low image resolution, or policy violation..." class="w-full p-2.5 bg-slate-900 border border-slate-800 rounded-xl text-slate-200 text-xs focus:outline-none focus:border-rose-500"></textarea>
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
      <!-- Customer Inspection Modal -->
      @if (inspectingCustomer()) {
        <div class="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4">
          <div class="glass-card rounded-3xl p-6 sm:p-8 max-w-2xl w-full border border-slate-700 space-y-6 shadow-2xl max-h-[90vh] overflow-y-auto">
            <div class="flex items-start justify-between border-b border-slate-800 pb-4">
              <div class="flex items-center space-x-4">
                <div class="w-14 h-14 rounded-2xl flex items-center justify-center font-black font-['Outfit'] text-lg overflow-hidden border-2 border-slate-700 bg-gradient-to-tr from-emerald-600 to-teal-400 text-slate-950">
                  @if (inspectingCustomer()?.avatarUrl) {
                    <img [src]="inspectingCustomer()?.avatarUrl" alt="Avatar" class="w-full h-full object-cover" />
                  } @else {
                    <span>{{ getInitials(inspectingCustomer()?.firstName || '', inspectingCustomer()?.lastName || '') }}</span>
                  }
                </div>
                <div>
                  <div class="flex items-center space-x-2">
                    <h3 class="text-lg font-bold text-white font-['Outfit']">{{ inspectingCustomer()?.firstName }} {{ inspectingCustomer()?.lastName }}</h3>
                    <span [class]="inspectingCustomer()?.status === 'ACTIVE' ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30' : 'bg-rose-500/10 text-rose-400 border-rose-500/30'" class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border">
                      {{ inspectingCustomer()?.status }}
                    </span>
                  </div>
                  <p class="text-xs text-slate-400 font-mono mt-0.5">{{ inspectingCustomer()?.email }}</p>
                </div>
              </div>
              <button (click)="inspectingCustomer.set(null)" class="text-slate-400 hover:text-white text-xl font-bold cursor-pointer">&times;</button>
            </div>

            <!-- Profile Info Grid -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
              <div class="p-3.5 bg-slate-900/80 rounded-2xl border border-slate-800 space-y-1">
                <span class="text-slate-500 uppercase text-[10px] font-bold">Contact Phone</span>
                <p class="font-mono text-slate-200 text-sm">{{ inspectingCustomer()?.phoneNumber || 'Not provided' }}</p>
              </div>
              <div class="p-3.5 bg-slate-900/80 rounded-2xl border border-slate-800 space-y-1">
                <span class="text-slate-500 uppercase text-[10px] font-bold">Member Since</span>
                <p class="text-slate-200 text-sm">{{ inspectingCustomer()?.createdAt ? (inspectingCustomer()?.createdAt | date:'mediumDate') : 'Sept 2026' }}</p>
              </div>
              <div class="sm:col-span-2 p-3.5 bg-slate-900/80 rounded-2xl border border-slate-800 space-y-1">
                <span class="text-slate-500 uppercase text-[10px] font-bold">Default Delivery Destination</span>
                <p class="text-slate-200 text-sm">
                  @if (inspectingCustomer()?.address || inspectingCustomer()?.city) {
                    <span>{{ inspectingCustomer()?.address }}, {{ inspectingCustomer()?.city }} {{ inspectingCustomer()?.postalCode }}</span>
                  } @else {
                    <span class="text-slate-500 italic">No delivery address registered</span>
                  }
                </p>
              </div>
            </div>

            <!-- Customer Orders History -->
            <div class="space-y-3">
              <h4 class="text-xs font-bold text-slate-300 uppercase tracking-wider flex items-center justify-between">
                <span>Recent Orders ({{ getCustomerOrders(inspectingCustomer()?.email || '').length }})</span>
                <span class="text-[11px] text-emerald-400 font-mono font-normal">
                  Total Spent: \${{ getCustomerTotalSpent(inspectingCustomer()?.email || '') }}
                </span>
              </h4>

              <div class="space-y-2 max-h-48 overflow-y-auto">
                @for (ord of getCustomerOrders(inspectingCustomer()?.email || ''); track ord.id) {
                  <div class="p-3 bg-slate-900/90 rounded-xl border border-slate-800 flex items-center justify-between text-xs">
                    <div>
                      <span class="font-mono font-bold text-emerald-400">{{ ord.orderNumber }}</span>
                      <span class="text-slate-500 ml-2 text-[11px]">{{ ord.createdAt | date:'shortDate' }}</span>
                    </div>
                    <div class="flex items-center space-x-3">
                      <span class="font-bold text-white font-['Outfit']">\${{ ord.totalAmount.toFixed(2) }}</span>
                      <span class="px-2 py-0.5 rounded-full text-[10px] font-bold bg-indigo-500/10 text-indigo-400 border border-indigo-500/30">
                        {{ ord.derivedStatus }}
                      </span>
                    </div>
                  </div>
                }
                @if (getCustomerOrders(inspectingCustomer()?.email || '').length === 0) {
                  <div class="p-4 rounded-xl bg-slate-900/40 border border-slate-800 text-center text-slate-500 text-xs">
                    This customer has not placed any marketplace orders yet.
                  </div>
                }
              </div>
            </div>

            <div class="flex items-center justify-between pt-4 border-t border-slate-800">
              <div class="space-x-2">
                @if (inspectingCustomer()?.status === 'ACTIVE') {
                  <button (click)="toggleCustomerStatus(inspectingCustomer()!.id, 'SUSPENDED'); inspectingCustomer.set(null)" class="px-3 py-1.5 rounded-xl bg-rose-500/10 hover:bg-rose-500/20 text-rose-400 border border-rose-500/30 text-xs font-bold transition-colors cursor-pointer">
                    🚫 Suspend Account
                  </button>
                } @else {
                  <button (click)="toggleCustomerStatus(inspectingCustomer()!.id, 'ACTIVE'); inspectingCustomer.set(null)" class="px-3 py-1.5 rounded-xl bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 text-xs font-bold transition-colors cursor-pointer">
                    🟢 Reactivate Account
                  </button>
                }
              </div>
              <button (click)="inspectingCustomer.set(null)" class="px-4 py-2 rounded-xl bg-slate-800 text-slate-300 hover:text-white text-xs font-semibold cursor-pointer">
                Close
              </button>
            </div>
          </div>
        </div>
      }

      <!-- Create / Edit Customer Modal -->
      @if (customerModalOpen()) {
        <div class="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4">
          <div class="glass-card rounded-3xl p-6 sm:p-8 max-w-lg w-full border border-slate-700 space-y-5 shadow-2xl">
            <div class="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 class="text-base font-bold text-white font-['Outfit']">
                {{ customerModalMode() === 'create' ? '➕ Create Customer Account' : '✏️ Edit Customer Profile' }}
              </h3>
              <button (click)="customerModalOpen.set(false)" class="text-slate-400 hover:text-white text-lg cursor-pointer">&times;</button>
            </div>

            <form (ngSubmit)="saveCustomer()" class="space-y-4">
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
                <div>
                  <label class="block text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-1">First Name</label>
                  <input type="text" [(ngModel)]="customerForm.firstName" name="firstName" required
                         class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white text-xs focus:outline-none focus:border-purple-500"
                         placeholder="e.g. Sarah" />
                </div>
                <div>
                  <label class="block text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-1">Last Name</label>
                  <input type="text" [(ngModel)]="customerForm.lastName" name="lastName" required
                         class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white text-xs focus:outline-none focus:border-purple-500"
                         placeholder="e.g. Jenkins" />
                </div>

                <div [class]="customerModalMode() === 'create' ? 'sm:col-span-1' : 'sm:col-span-2'">
                  <label class="block text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-1">Email Address</label>
                  <input type="email" [(ngModel)]="customerForm.email" name="email" required [disabled]="customerModalMode() === 'edit'"
                         class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white text-xs focus:outline-none focus:border-purple-500 disabled:opacity-50 font-mono"
                         placeholder="e.g. sarah@example.com" />
                </div>

                @if (customerModalMode() === 'create') {
                  <div>
                    <label class="block text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-1">Password</label>
                    <input type="password" [(ngModel)]="customerForm.password" name="password" required minlength="6"
                           class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white text-xs focus:outline-none focus:border-purple-500"
                           placeholder="••••••••••••" />
                  </div>
                }

                <div class="sm:col-span-2">
                  <label class="block text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-1">Phone Number</label>
                  <input type="text" [(ngModel)]="customerForm.phoneNumber" name="phoneNumber"
                         class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white text-xs focus:outline-none focus:border-purple-500"
                         placeholder="e.g. +1 555 234 8912" />
                </div>

                <div class="sm:col-span-2">
                  <label class="block text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-1">Street Address</label>
                  <input type="text" [(ngModel)]="customerForm.address" name="address"
                         class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white text-xs focus:outline-none focus:border-purple-500"
                         placeholder="e.g. 742 Evergreen Terrace" />
                </div>

                <div>
                  <label class="block text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-1">City</label>
                  <input type="text" [(ngModel)]="customerForm.city" name="city"
                         class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white text-xs focus:outline-none focus:border-purple-500"
                         placeholder="e.g. Springfield" />
                </div>

                <div>
                  <label class="block text-[11px] font-bold uppercase tracking-wider text-slate-400 mb-1">Postal / Zip Code</label>
                  <input type="text" [(ngModel)]="customerForm.postalCode" name="postalCode"
                         class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white text-xs focus:outline-none focus:border-purple-500"
                         placeholder="e.g. 97477" />
                </div>
              </div>

              <div class="flex items-center justify-end space-x-2 pt-3 border-t border-slate-800">
                <button type="button" (click)="customerModalOpen.set(false)" class="px-3.5 py-2 rounded-xl bg-slate-800 text-slate-300 text-xs font-semibold cursor-pointer">Cancel</button>
                <button type="submit" [disabled]="savingCustomer()" class="px-5 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs shadow-md shadow-purple-600/20 disabled:opacity-50 cursor-pointer">
                  {{ savingCustomer() ? 'Saving...' : (customerModalMode() === 'create' ? 'Create Customer' : 'Save Changes') }}
                </button>
              </div>
            </form>
          </div>
        </div>
      }

      <!-- Reset Password Modal -->
      @if (resettingPasswordUser()) {
        <div class="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-md flex items-center justify-center p-4">
          <div class="glass-card rounded-3xl p-6 max-w-md w-full border border-slate-700 space-y-4 shadow-2xl">
            <div class="flex items-center justify-between border-b border-slate-800 pb-3">
              <h3 class="text-base font-bold text-white font-['Outfit']">🔑 Reset Password</h3>
              <button (click)="resettingPasswordUser.set(null)" class="text-slate-400 hover:text-white text-lg cursor-pointer">&times;</button>
            </div>

            <p class="text-xs text-slate-400">
              Reset account credentials for <strong class="text-white">{{ resettingPasswordUser()?.firstName }} {{ resettingPasswordUser()?.lastName }}</strong> ({{ resettingPasswordUser()?.email }}).
            </p>

            <div class="space-y-2">
              <div class="flex items-center justify-between">
                <label class="text-[11px] font-bold text-slate-300 uppercase tracking-wider">New Password</label>
                <button type="button" (click)="generateRandomPassword()" class="text-[10px] text-purple-400 hover:text-purple-300 font-semibold cursor-pointer">
                  🎲 Generate Password
                </button>
              </div>
              <input type="text" [(ngModel)]="newCustomerPassword" placeholder="Enter new password (min 6 chars)" class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-800 text-white font-mono text-xs focus:outline-none focus:border-purple-500" />
            </div>

            <div class="flex items-center justify-end space-x-2 pt-2 border-t border-slate-800">
              <button (click)="resettingPasswordUser.set(null)" class="px-3.5 py-1.5 rounded-xl bg-slate-800 text-slate-300 text-xs font-semibold cursor-pointer">Cancel</button>
              <button (click)="submitResetPassword()" [disabled]="!newCustomerPassword || newCustomerPassword.length < 6" class="px-4 py-1.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs shadow-md shadow-amber-500/20 disabled:opacity-50 cursor-pointer">
                Update Password
              </button>
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

  activeTab: 'product_review' | 'seller_moderation' | 'customer_management' | 'platform_orders' | 'catalog_inventory' = 'product_review';

  pendingProducts = signal<Product[]>([]);
  allProducts = signal<Product[]>([]);
  allSellers = signal<SellerProfile[]>([]);
  pendingSellers = signal<SellerProfile[]>([]);
  customers = signal<User[]>([]);
  platformOrders = signal<ParentOrder[]>([]);
  alertMessage = signal<string | null>(null);

  searchQuery = '';
  customerSearchQuery = '';
  customerStatusFilter: 'ALL' | 'ACTIVE' | 'SUSPENDED' = 'ALL';

  activeCustomersCount = computed(() => this.customers().filter(c => c.status === 'ACTIVE').length);
  suspendedCustomersCount = computed(() => this.customers().filter(c => c.status === 'SUSPENDED').length);

  // Customer inspector & modal states
  inspectingCustomer = signal<User | null>(null);
  customerModalOpen = signal<boolean>(false);
  customerModalMode = signal<'create' | 'edit'>('create');
  savingCustomer = signal<boolean>(false);

  customerForm = {
    id: 0,
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phoneNumber: '',
    address: '',
    city: '',
    postalCode: ''
  };

  resettingPasswordUser = signal<User | null>(null);
  newCustomerPassword = '';

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

    this.sellerService.getCustomers().subscribe({
      next: (res) => {
        if (res.success) {
          this.customers.set(res.data);
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

  filteredCustomers(): User[] {
    const q = this.customerSearchQuery.trim().toLowerCase();
    let list = this.customers();
    if (this.customerStatusFilter !== 'ALL') {
      list = list.filter(c => c.status === this.customerStatusFilter);
    }
    if (!q) return list;
    return list.filter(c => 
      (c.firstName && c.firstName.toLowerCase().includes(q)) ||
      (c.lastName && c.lastName.toLowerCase().includes(q)) ||
      (c.email && c.email.toLowerCase().includes(q)) ||
      (c.phoneNumber && c.phoneNumber.toLowerCase().includes(q)) ||
      (c.city && c.city.toLowerCase().includes(q)) ||
      c.id.toString().includes(q)
    );
  }

  getCustomerOrderCount(email: string): number {
    if (!email) return 0;
    return this.platformOrders().filter(o => o.customerEmail?.toLowerCase() === email.toLowerCase()).length;
  }

  getCustomerOrders(email: string): ParentOrder[] {
    if (!email) return [];
    return this.platformOrders().filter(o => o.customerEmail?.toLowerCase() === email.toLowerCase());
  }

  getCustomerTotalSpent(email: string): string {
    const orders = this.getCustomerOrders(email);
    const total = orders.reduce((sum, o) => sum + (o.totalAmount || 0), 0);
    return total.toFixed(2);
  }

  getInitials(first: string, last: string): string {
    const f = first ? first.charAt(0) : '';
    const l = last ? last.charAt(0) : '';
    return `${f}${l}`.toUpperCase() || 'U';
  }

  inspectCustomer(c: User): void {
    this.inspectingCustomer.set(c);
  }

  openCreateCustomerModal(): void {
    this.customerModalMode.set('create');
    this.customerForm = {
      id: 0,
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      phoneNumber: '',
      address: '',
      city: '',
      postalCode: ''
    };
    this.customerModalOpen.set(true);
  }

  openEditCustomerModal(c: User): void {
    this.customerModalMode.set('edit');
    this.customerForm = {
      id: c.id,
      firstName: c.firstName,
      lastName: c.lastName,
      email: c.email,
      password: '',
      phoneNumber: c.phoneNumber || '',
      address: c.address || '',
      city: c.city || '',
      postalCode: c.postalCode || ''
    };
    this.customerModalOpen.set(true);
  }

  saveCustomer(): void {
    this.savingCustomer.set(true);

    if (this.customerModalMode() === 'create') {
      this.sellerService.createCustomer(this.customerForm).subscribe({
        next: (res) => {
          this.savingCustomer.set(false);
          this.customerModalOpen.set(false);
          this.alertMessage.set(`Customer account for ${this.customerForm.email} created successfully!`);
          this.loadData();
        },
        error: (err) => {
          this.savingCustomer.set(false);
          const msg = err.error?.message || 'Failed to create customer';
          alert(msg);
        }
      });
    } else {
      const updateReq = {
        firstName: this.customerForm.firstName,
        lastName: this.customerForm.lastName,
        phoneNumber: this.customerForm.phoneNumber,
        address: this.customerForm.address,
        city: this.customerForm.city,
        postalCode: this.customerForm.postalCode
      };

      this.sellerService.updateCustomer(this.customerForm.id, updateReq).subscribe({
        next: (res) => {
          this.savingCustomer.set(false);
          this.customerModalOpen.set(false);
          this.alertMessage.set(`Customer #${this.customerForm.id} updated successfully!`);
          this.loadData();
        },
        error: (err) => {
          this.savingCustomer.set(false);
          const msg = err.error?.message || 'Failed to update customer';
          alert(msg);
        }
      });
    }
  }

  openResetPasswordModal(c: User): void {
    this.resettingPasswordUser.set(c);
    this.newCustomerPassword = '';
  }

  generateRandomPassword(): void {
    const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%';
    let pass = '';
    for (let i = 0; i < 10; i++) {
      pass += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    this.newCustomerPassword = pass;
  }

  submitResetPassword(): void {
    const user = this.resettingPasswordUser();
    if (!user || !this.newCustomerPassword) return;

    this.sellerService.resetUserPassword(user.id, this.newCustomerPassword).subscribe({
      next: () => {
        this.alertMessage.set(`Password for ${user.email} was reset successfully to: ${this.newCustomerPassword}`);
        this.resettingPasswordUser.set(null);
      },
      error: (err) => {
        const msg = err.error?.message || 'Failed to reset password';
        alert(msg);
      }
    });
  }

  deleteCustomer(c: User): void {
    if (confirm(`Are you sure you want to permanently delete customer ${c.firstName} ${c.lastName} (${c.email})? This action cannot be undone.`)) {
      this.sellerService.deleteCustomer(c.id).subscribe({
        next: () => {
          this.alertMessage.set(`Customer #${c.id} was permanently deleted.`);
          this.loadData();
        },
        error: (err) => {
          const msg = err.error?.message || 'Failed to delete customer';
          alert(msg);
        }
      });
    }
  }

  toggleCustomerStatus(userId: number, newStatus: UserStatus): void {
    this.sellerService.updateUserStatus(userId, newStatus, 'Admin moderation action').subscribe({
      next: () => {
        this.alertMessage.set(`Customer #${userId} status set to ${newStatus}.`);
        this.loadData();
      }
    });
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

  suspendProduct(id: number): void {
    this.productService.suspendProduct(id, 'Admin suspension').subscribe({
      next: () => {
        this.alertMessage.set(`Product #${id} suspended.`);
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

  failedAvatarUserIds = signal<Set<number>>(new Set());

  isAvatarFailed(userId: number): boolean {
    return this.failedAvatarUserIds().has(userId);
  }

  markAvatarFailed(userId: number): void {
    const updated = new Set(this.failedAvatarUserIds());
    updated.add(userId);
    this.failedAvatarUserIds.set(updated);
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


