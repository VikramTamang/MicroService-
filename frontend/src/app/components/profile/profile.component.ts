import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { ChangePasswordRequest, UpdateProfileRequest, UpdateSellerProfileRequest, User } from '../../models/user.model';

type ProfileTab = 'personal' | 'store' | 'security' | 'summary';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="max-w-6xl mx-auto space-y-8 pb-16">
      <!-- Profile Hero Banner -->
      <div class="relative overflow-hidden rounded-3xl border border-slate-800/80 bg-gradient-to-br from-slate-900/90 via-slate-950 to-slate-900 p-8 shadow-2xl backdrop-blur-xl">
        <div class="absolute -right-16 -top-16 h-64 w-64 rounded-full bg-emerald-500/10 blur-3xl pointer-events-none"></div>
        <div class="absolute -left-16 -bottom-16 h-64 w-64 rounded-full bg-cyan-500/10 blur-3xl pointer-events-none"></div>

        <div class="relative flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
          <div class="flex items-center space-x-5">
            <!-- User Avatar / Initials with Glow & Upload Trigger -->
            <div class="relative group">
              <div class="w-20 h-20 sm:w-24 sm:h-24 rounded-2xl flex items-center justify-center font-['Outfit'] font-black text-2xl sm:text-3xl shadow-xl transition-all duration-300 overflow-hidden relative border-2 border-slate-700/80 shadow-emerald-500/5 cursor-pointer"
                   (click)="avatarInput.click()"
                   [ngClass]="(!profileUser()?.avatarUrl || avatarLoadFailed()) ? avatarGradientClass() : 'bg-slate-900 text-white'">
                @if (profileUser()?.avatarUrl && !avatarLoadFailed()) {
                  <img [src]="profileUser()?.avatarUrl" alt="Avatar" class="w-full h-full object-cover rounded-2xl" (error)="onAvatarError()" />
                } @else {
                  <span>{{ userInitials() }}</span>
                }

                <!-- Uploading Spinner Overlay -->
                @if (uploadingAvatar()) {
                  <div class="absolute inset-0 bg-slate-950/85 backdrop-blur-sm flex flex-col items-center justify-center rounded-2xl z-10">
                    <span class="animate-spin text-xl">⏳</span>
                    <span class="text-[9px] font-extrabold text-emerald-400 mt-1 uppercase tracking-wider">Uploading</span>
                  </div>
                }

                <!-- Hover Overlay Trigger -->
                <div class="absolute inset-0 bg-slate-950/75 backdrop-blur-[2px] flex flex-col items-center justify-center rounded-2xl opacity-0 group-hover:opacity-100 transition-all duration-200 z-10 pointer-events-none">
                  <span class="text-xl">📷</span>
                  <span class="text-[10px] font-bold text-white mt-1 tracking-wide">Change PP</span>
                </div>
              </div>

              <!-- Floating Quick Camera Badge -->
              <button type="button" (click)="avatarInput.click()" [disabled]="uploadingAvatar()"
                      title="Upload Profile Picture"
                      class="absolute -bottom-1.5 -right-1.5 w-7 h-7 sm:w-8 sm:h-8 rounded-full border-2 border-slate-950 bg-emerald-400 hover:bg-emerald-300 text-slate-950 flex items-center justify-center shadow-lg transition-transform hover:scale-110 cursor-pointer z-20">
                <span class="text-xs font-bold">📷</span>
              </button>

              <input type="file" #avatarInput class="hidden" accept="image/png,image/jpeg,image/webp,image/gif,image/svg+xml" (change)="onAvatarFileSelected($event)">
            </div>

            <!-- Identity Info -->
            <div class="space-y-1.5">
              <div class="flex flex-wrap items-center gap-2.5">
                <h1 class="text-2xl sm:text-3xl font-extrabold text-white tracking-tight font-['Outfit']">
                  {{ profileUser()?.firstName }} {{ profileUser()?.lastName }}
                </h1>
                <span class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider flex items-center space-x-1.5"
                      [ngClass]="roleBadgeClass()">
                  <span>{{ roleIcon() }}</span>
                  <span>{{ roleLabel() }}</span>
                </span>
              </div>
              <p class="text-xs sm:text-sm text-slate-400 flex items-center space-x-2 font-mono">
                <span>📧 {{ profileUser()?.email }}</span>
                @if (profileUser()?.phoneNumber) {
                  <span>•</span>
                  <span>📱 {{ profileUser()?.phoneNumber }}</span>
                }
              </p>
              <div class="flex items-center space-x-2 pt-0.5">
                <button type="button" (click)="avatarInput.click()" class="text-[11px] font-semibold text-emerald-400 hover:text-emerald-300 transition-colors flex items-center space-x-1">
                  <span>📸 Upload Photo</span>
                </button>
                @if (profileUser()?.avatarUrl) {
                  <span class="text-slate-600">•</span>
                  <button type="button" (click)="removeAvatar()" class="text-[11px] font-semibold text-rose-400 hover:text-rose-300 transition-colors flex items-center space-x-1">
                    <span>🗑️ Remove</span>
                  </button>
                }
              </div>
              @if (isSeller() && profileUser()?.sellerProfile) {
                <p class="text-xs text-cyan-300/80 font-medium">
                  Store: <strong class="text-white">{{ profileUser()?.sellerProfile?.storeName }}</strong>
                  <span class="ml-2 text-slate-500">|</span>
                  <span class="ml-2 text-slate-400">Commission: {{ profileUser()?.sellerProfile?.commissionRate || 10 }}%</span>
                </p>
              }
            </div>
          </div>

          <!-- Quick Navigation Actions -->
          <div class="flex flex-wrap items-center gap-2.5 pt-2 md:pt-0">
            @if (isAdmin()) {
              <a routerLink="/admin" class="px-4 py-2 rounded-xl bg-purple-500/20 hover:bg-purple-500/30 text-purple-300 hover:text-white border border-purple-500/40 text-xs font-bold transition-all shadow-sm flex items-center space-x-2">
                <span>🛡️</span>
                <span>Moderation Console</span>
              </a>
            } @else if (isSeller()) {
              <a routerLink="/seller" class="px-4 py-2 rounded-xl bg-cyan-500/20 hover:bg-cyan-500/30 text-cyan-300 hover:text-white border border-cyan-500/40 text-xs font-bold transition-all shadow-sm flex items-center space-x-2">
                <span>🏪</span>
                <span>Seller Dashboard</span>
              </a>
            } @else {
              <a routerLink="/my-orders" class="px-4 py-2 rounded-xl bg-emerald-500/20 hover:bg-emerald-500/30 text-emerald-300 hover:text-white border border-emerald-500/40 text-xs font-bold transition-all shadow-sm flex items-center space-x-2">
                <span>📦</span>
                <span>My Orders</span>
              </a>
            }
          </div>
        </div>

        <!-- Role-adaptive Tab Navigation -->
        <div class="flex items-center space-x-2 border-t border-slate-800/80 mt-8 pt-4 overflow-x-auto scrollbar-none">
          <button (click)="activeTab.set('personal')"
                  [ngClass]="activeTab() === 'personal' ? 'bg-emerald-500/10 text-emerald-400 border-emerald-500/40' : 'text-slate-400 hover:text-slate-200 border-transparent hover:bg-slate-800/50'"
                  class="px-4 py-2 rounded-xl text-xs sm:text-sm font-bold border transition-all duration-200 flex items-center space-x-2 whitespace-nowrap">
            <span>👤</span>
            <span>Personal Information</span>
          </button>

          @if (isSeller()) {
            <button (click)="activeTab.set('store')"
                    [ngClass]="activeTab() === 'store' ? 'bg-cyan-500/10 text-cyan-400 border-cyan-500/40' : 'text-slate-400 hover:text-slate-200 border-transparent hover:bg-slate-800/50'"
                    class="px-4 py-2 rounded-xl text-xs sm:text-sm font-bold border transition-all duration-200 flex items-center space-x-2 whitespace-nowrap">
              <span>🏪</span>
              <span>Store Profile</span>
            </button>
          }

          <button (click)="activeTab.set('security')"
                  [ngClass]="activeTab() === 'security' ? 'bg-amber-500/10 text-amber-400 border-amber-500/40' : 'text-slate-400 hover:text-slate-200 border-transparent hover:bg-slate-800/50'"
                  class="px-4 py-2 rounded-xl text-xs sm:text-sm font-bold border transition-all duration-200 flex items-center space-x-2 whitespace-nowrap">
            <span>🔒</span>
            <span>Security & Password</span>
          </button>

          <button (click)="activeTab.set('summary')"
                  [ngClass]="activeTab() === 'summary' ? 'bg-indigo-500/10 text-indigo-400 border-indigo-500/40' : 'text-slate-400 hover:text-slate-200 border-transparent hover:bg-slate-800/50'"
                  class="px-4 py-2 rounded-xl text-xs sm:text-sm font-bold border transition-all duration-200 flex items-center space-x-2 whitespace-nowrap">
            <span>📊</span>
            <span>Account Overview</span>
          </button>
        </div>
      </div>

      <!-- Alerts / Feedback Banner -->
      @if (alertMessage()) {
        <div class="p-4 rounded-2xl border transition-all animate-fadeIn flex items-center justify-between shadow-lg"
             [ngClass]="alertType() === 'success' ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-300' : 'bg-rose-500/10 border-rose-500/30 text-rose-300'">
          <div class="flex items-center space-x-3">
            <span class="text-base">{{ alertType() === 'success' ? '✅' : '⚠️' }}</span>
            <span class="text-xs sm:text-sm font-semibold">{{ alertMessage() }}</span>
          </div>
          <button (click)="alertMessage.set(null)" class="text-slate-400 hover:text-white font-bold text-base">&times;</button>
        </div>
      }

      <!-- TAB 1: PERSONAL INFORMATION -->
      @if (activeTab() === 'personal') {
        <div class="glass-card rounded-3xl p-6 sm:p-8 border border-slate-800 space-y-6 shadow-xl">
          <div class="border-b border-slate-800 pb-4">
            <h2 class="text-lg font-bold text-white flex items-center space-x-2">
              <span>👤</span>
              <span>Personal Identity & Default Shipping Address</span>
            </h2>
            <p class="text-xs text-slate-400 mt-1">Manage your contact details and default destination for marketplace deliveries.</p>
          </div>

          <form (ngSubmit)="savePersonalInfo()" class="space-y-6">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">First Name</label>
                <input type="text" [(ngModel)]="personalForm.firstName" name="firstName" required
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-colors"
                       placeholder="e.g. Alex" />
              </div>

              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Last Name</label>
                <input type="text" [(ngModel)]="personalForm.lastName" name="lastName" required
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-colors"
                       placeholder="e.g. Mercer" />
              </div>

              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Email Address (Read-Only)</label>
                <div class="relative">
                  <input type="email" [value]="profileUser()?.email" disabled
                         class="w-full px-4 py-3 rounded-xl bg-slate-950 border border-slate-800 text-slate-400 text-sm cursor-not-allowed font-mono" />
                  <span class="absolute right-3.5 top-3.5 text-xs text-emerald-400 font-semibold">🔒 Verified</span>
                </div>
              </div>

              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Contact Phone Number</label>
                <input type="text" [(ngModel)]="personalForm.phoneNumber" name="phoneNumber"
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-colors"
                       placeholder="e.g. +1 (555) 019-2834" />
              </div>

              <div class="sm:col-span-2">
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Street Address</label>
                <input type="text" [(ngModel)]="personalForm.address" name="address"
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-colors"
                       placeholder="e.g. 742 Evergreen Terrace" />
              </div>

              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">City</label>
                <input type="text" [(ngModel)]="personalForm.city" name="city"
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-colors"
                       placeholder="e.g. Springfield" />
              </div>

              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Postal / Zip Code</label>
                <input type="text" [(ngModel)]="personalForm.postalCode" name="postalCode"
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-emerald-500 focus:ring-1 focus:ring-emerald-500 transition-colors"
                       placeholder="e.g. 97477" />
              </div>
            </div>

            <div class="flex items-center justify-end pt-4 border-t border-slate-800">
              <button type="submit" [disabled]="savingPersonal()"
                      class="px-6 py-3 rounded-xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs sm:text-sm shadow-lg shadow-emerald-500/20 transition-all duration-200 disabled:opacity-50 flex items-center space-x-2">
                @if (savingPersonal()) {
                  <span class="inline-block animate-spin">⏳</span>
                  <span>Saving Changes...</span>
                } @else {
                  <span>💾</span>
                  <span>Save Personal Details</span>
                }
              </button>
            </div>
          </form>
        </div>
      }

      <!-- TAB 2: STORE PROFILE (SELLERS ONLY) -->
      @if (activeTab() === 'store' && isSeller()) {
        <div class="glass-card rounded-3xl p-6 sm:p-8 border border-slate-800 space-y-6 shadow-xl">
          <div class="border-b border-slate-800 pb-4 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div>
              <h2 class="text-lg font-bold text-white flex items-center space-x-2">
                <span>🏪</span>
                <span>Merchant Store Branding & Settings</span>
              </h2>
              <p class="text-xs text-slate-400 mt-1">Configure your storefront appearance, public description, and merchant identification.</p>
            </div>
            <div>
              <span class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider"
                    [ngClass]="sellerVerificationBadgeClass()">
                {{ profileUser()?.sellerProfile?.verificationStatus || 'APPROVED' }}
              </span>
            </div>
          </div>

          <form (ngSubmit)="saveStoreProfile()" class="space-y-6">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-5">
              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Store Display Name</label>
                <input type="text" [(ngModel)]="storeForm.storeName" name="storeName" required
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 transition-colors"
                       placeholder="e.g. Apex Audio & Electronics" />
              </div>

              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Store Slug / Identifier</label>
                <input type="text" [value]="profileUser()?.sellerProfile?.storeSlug" disabled
                       class="w-full px-4 py-3 rounded-xl bg-slate-950 border border-slate-800 text-slate-400 text-sm cursor-not-allowed font-mono" />
              </div>

              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Store Logo URL</label>
                <input type="url" [(ngModel)]="storeForm.logoUrl" name="logoUrl"
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 transition-colors"
                       placeholder="https://example.com/logo.png" />
              </div>

              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Store Banner Image URL</label>
                <input type="url" [(ngModel)]="storeForm.bannerUrl" name="bannerUrl"
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 transition-colors"
                       placeholder="https://example.com/banner.jpg" />
              </div>

              <div class="sm:col-span-2">
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Store Bio & Description</label>
                <textarea rows="3" [(ngModel)]="storeForm.storeDescription" name="storeDescription"
                          class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 transition-colors"
                          placeholder="Tell customers about your store, product quality, warranty, and shipping standards..."></textarea>
              </div>

              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Business Registration # (Optional)</label>
                <input type="text" [(ngModel)]="storeForm.businessRegistrationNumber" name="businessRegNo"
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 transition-colors"
                       placeholder="e.g. BRN-84920482" />
              </div>

              <div>
                <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Tax ID / VAT # (Optional)</label>
                <input type="text" [(ngModel)]="storeForm.taxIdentificationNumber" name="taxId"
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-cyan-500 focus:ring-1 focus:ring-cyan-500 transition-colors"
                       placeholder="e.g. VAT-9920194" />
              </div>
            </div>

            <!-- Preview Card -->
            @if (storeForm.logoUrl || storeForm.storeName) {
              <div class="p-4 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-2">
                <span class="text-[11px] font-bold text-slate-400 uppercase tracking-wider">Storefront Header Preview:</span>
                <div class="flex items-center space-x-3.5">
                  @if (storeForm.logoUrl) {
                    <img [src]="storeForm.logoUrl" alt="Store logo" class="w-12 h-12 rounded-xl object-cover border border-slate-800" (error)="onImageError($event)" />
                  } @else {
                    <div class="w-12 h-12 rounded-xl bg-cyan-500/20 text-cyan-300 font-bold flex items-center justify-center text-lg">🏪</div>
                  }
                  <div>
                    <h4 class="text-sm font-bold text-white">{{ storeForm.storeName || 'My Store' }}</h4>
                    <p class="text-xs text-slate-400 line-clamp-1">{{ storeForm.storeDescription || 'No description set' }}</p>
                  </div>
                </div>
              </div>
            }

            <div class="flex items-center justify-end pt-4 border-t border-slate-800">
              <button type="submit" [disabled]="savingStore()"
                      class="px-6 py-3 rounded-xl bg-cyan-500 hover:bg-cyan-400 text-slate-950 font-bold text-xs sm:text-sm shadow-lg shadow-cyan-500/20 transition-all duration-200 disabled:opacity-50 flex items-center space-x-2">
                @if (savingStore()) {
                  <span class="inline-block animate-spin">⏳</span>
                  <span>Updating Store...</span>
                } @else {
                  <span>🏬</span>
                  <span>Save Store Settings</span>
                }
              </button>
            </div>
          </form>
        </div>
      }

      <!-- TAB 3: SECURITY & PASSWORD -->
      @if (activeTab() === 'security') {
        <div class="glass-card rounded-3xl p-6 sm:p-8 border border-slate-800 space-y-6 shadow-xl">
          <div class="border-b border-slate-800 pb-4">
            <h2 class="text-lg font-bold text-white flex items-center space-x-2">
              <span>🔒</span>
              <span>Account Security & Password Management</span>
            </h2>
            <p class="text-xs text-slate-400 mt-1">Keep your account protected with a strong, distinct password.</p>
          </div>

          <form (ngSubmit)="changePassword()" class="space-y-6 max-w-lg">
            <div>
              <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Current Password</label>
              <input type="password" [(ngModel)]="passwordForm.currentPassword" name="currentPassword" required
                     class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-colors"
                     placeholder="••••••••••••" />
            </div>

            <div>
              <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">New Password (Min. 6 characters)</label>
              <input type="password" [(ngModel)]="passwordForm.newPassword" name="newPassword" required minlength="6"
                     class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-colors"
                     placeholder="••••••••••••" />
            </div>

            <div>
              <label class="block text-xs font-bold text-slate-300 uppercase tracking-wider mb-2">Confirm New Password</label>
              <input type="password" [(ngModel)]="passwordForm.confirmPassword" name="confirmPassword" required minlength="6"
                     class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-800 text-white placeholder-slate-500 text-sm focus:outline-none focus:border-amber-500 focus:ring-1 focus:ring-amber-500 transition-colors"
                     placeholder="••••••••••••" />
            </div>

            @if (passwordForm.newPassword && passwordForm.confirmPassword && passwordForm.newPassword !== passwordForm.confirmPassword) {
              <p class="text-xs text-rose-400 font-semibold">⚠️ Passwords do not match</p>
            }

            <div class="pt-2">
              <button type="submit" [disabled]="savingPassword() || (passwordForm.newPassword !== passwordForm.confirmPassword)"
                      class="px-6 py-3 rounded-xl bg-amber-500 hover:bg-amber-400 text-slate-950 font-bold text-xs sm:text-sm shadow-lg shadow-amber-500/20 transition-all duration-200 disabled:opacity-50 flex items-center space-x-2">
                @if (savingPassword()) {
                  <span class="inline-block animate-spin">⏳</span>
                  <span>Updating Password...</span>
                } @else {
                  <span>🔑</span>
                  <span>Update Password</span>
                }
              </button>
            </div>
          </form>
        </div>
      }

      <!-- TAB 4: ACCOUNT OVERVIEW & METRICS -->
      @if (activeTab() === 'summary') {
        <div class="space-y-6">
          <div class="grid grid-cols-1 sm:grid-cols-3 gap-5">
            <div class="glass-card rounded-2xl p-6 border border-slate-800 space-y-2">
              <p class="text-xs font-bold uppercase text-slate-400">Account Type</p>
              <h3 class="text-xl font-black text-white font-['Outfit']">{{ roleLabel() }}</h3>
              <p class="text-[11px] text-slate-500">Privileges: {{ rolePrivileges() }}</p>
            </div>

            <div class="glass-card rounded-2xl p-6 border border-slate-800 space-y-2">
              <p class="text-xs font-bold uppercase text-slate-400">Account Status</p>
              <h3 class="text-xl font-black text-emerald-400 font-['Outfit']">Active & Verified</h3>
              <p class="text-[11px] text-slate-500">JWT Authentication active</p>
            </div>

            <div class="glass-card rounded-2xl p-6 border border-slate-800 space-y-2">
              <p class="text-xs font-bold uppercase text-slate-400">Member Since</p>
              <h3 class="text-xl font-black text-white font-['Outfit']">{{ memberSinceDate() }}</h3>
              <p class="text-[11px] text-slate-500">ApexStore Marketplace ID: #{{ profileUser()?.id }}</p>
            </div>
          </div>

          <!-- Role-tailored action card -->
          <div class="glass-card rounded-3xl p-6 sm:p-8 border border-slate-800 space-y-4">
            <h3 class="text-base font-bold text-white">Quick Portals & Shortcuts</h3>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              @if (isAdmin()) {
                <a routerLink="/admin" class="p-4 rounded-2xl bg-purple-500/10 hover:bg-purple-500/20 border border-purple-500/30 transition-all space-y-1 block">
                  <div class="font-bold text-sm text-purple-300">🛡️ Platform Moderation Console</div>
                  <p class="text-xs text-slate-400">Review pending sellers, catalog products, and dispute tracking.</p>
                </a>
                <a routerLink="/" class="p-4 rounded-2xl bg-slate-900/80 hover:bg-slate-800 border border-slate-800 transition-all space-y-1 block">
                  <div class="font-bold text-sm text-white">🛒 Customer Storefront</div>
                  <p class="text-xs text-slate-400">Browse live active products in the customer-facing catalog.</p>
                </a>
              } @else if (isSeller()) {
                <a routerLink="/seller" class="p-4 rounded-2xl bg-cyan-500/10 hover:bg-cyan-500/20 border border-cyan-500/30 transition-all space-y-1 block">
                  <div class="font-bold text-sm text-cyan-300">🏪 Seller Dashboard</div>
                  <p class="text-xs text-slate-400">Manage catalog inventory, add new products, and ship orders.</p>
                </a>
                <a routerLink="/my-orders" class="p-4 rounded-2xl bg-slate-900/80 hover:bg-slate-800 border border-slate-800 transition-all space-y-1 block">
                  <div class="font-bold text-sm text-white">📦 Personal Orders</div>
                  <p class="text-xs text-slate-400">View your own marketplace purchases and delivery timelines.</p>
                </a>
              } @else {
                <a routerLink="/my-orders" class="p-4 rounded-2xl bg-emerald-500/10 hover:bg-emerald-500/20 border border-emerald-500/30 transition-all space-y-1 block">
                  <div class="font-bold text-sm text-emerald-300">📦 My Order History</div>
                  <p class="text-xs text-slate-400">Track shipments from independent merchants and multi-seller bags.</p>
                </a>
                <a routerLink="/cart" class="p-4 rounded-2xl bg-slate-900/80 hover:bg-slate-800 border border-slate-800 transition-all space-y-1 block">
                  <div class="font-bold text-sm text-white">🛍️ Shopping Cart</div>
                  <p class="text-xs text-slate-400">Check items in your current shopping basket and proceed to checkout.</p>
                </a>
              }
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class ProfileComponent implements OnInit {
  authService = inject(AuthService);
  userService = inject(UserService);

  activeTab = signal<ProfileTab>('personal');
  profileUser = signal<User | null>(null);

  uploadingAvatar = signal<boolean>(false);
  private avatarFailedUrl = signal<string | null>(null);

  avatarLoadFailed(): boolean {
    const url = this.profileUser()?.avatarUrl;
    if (!url) return false;
    return this.avatarFailedUrl() === url;
  }

  onAvatarError(): void {
    const url = this.profileUser()?.avatarUrl;
    if (url) {
      this.avatarFailedUrl.set(url);
    }
  }

  savingPersonal = signal<boolean>(false);
  savingStore = signal<boolean>(false);
  savingPassword = signal<boolean>(false);

  alertMessage = signal<string | null>(null);
  alertType = signal<'success' | 'error'>('success');

  personalForm: UpdateProfileRequest = {
    firstName: '',
    lastName: '',
    phoneNumber: '',
    address: '',
    city: '',
    postalCode: ''
  };

  storeForm: UpdateSellerProfileRequest = {
    storeName: '',
    storeDescription: '',
    logoUrl: '',
    bannerUrl: '',
    businessRegistrationNumber: '',
    taxIdentificationNumber: ''
  };

  passwordForm: ChangePasswordRequest = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  };

  isAdmin = computed(() => this.profileUser()?.role === 'ROLE_ADMIN');
  isSeller = computed(() => this.profileUser()?.role === 'ROLE_SELLER' || !!this.profileUser()?.sellerProfile);

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    this.userService.getProfile().subscribe({
      next: res => {
        if (res.success && res.data) {
          this.avatarFailedUrl.set(null);
          this.profileUser.set(res.data);
          this.populateForms(res.data);
        }
      },
      error: () => {
        // Fallback to authService user if offline or error
        const localUser = this.authService.currentUser();
        if (localUser) {
          this.profileUser.set(localUser);
          this.populateForms(localUser);
        }
      }
    });
  }

  private populateForms(user: User): void {
    this.personalForm = {
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      phoneNumber: user.phoneNumber || '',
      address: user.address || '',
      city: user.city || '',
      postalCode: user.postalCode || ''
    };

    if (user.sellerProfile) {
      this.storeForm = {
        storeName: user.sellerProfile.storeName || '',
        storeDescription: user.sellerProfile.storeDescription || '',
        logoUrl: user.sellerProfile.logoUrl || '',
        bannerUrl: user.sellerProfile.bannerUrl || '',
        businessRegistrationNumber: user.sellerProfile.businessRegistrationNumber || '',
        taxIdentificationNumber: user.sellerProfile.taxIdentificationNumber || ''
      };
    }
  }

  savePersonalInfo(): void {
    this.savingPersonal.set(true);
    this.alertMessage.set(null);

    this.userService.updateProfile(this.personalForm).subscribe({
      next: res => {
        this.savingPersonal.set(false);
        if (res.success && res.data) {
          this.profileUser.set(res.data);
          // Update local session
          this.authService.updateStoredUser(res.data);
          this.showAlert('Personal information updated successfully!', 'success');
        }
      },
      error: err => {
        this.savingPersonal.set(false);
        const msg = err.error?.message || 'Failed to update personal information';
        this.showAlert(msg, 'error');
      }
    });
  }

  onAvatarFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];

    // File validation: Size <= 10MB
    if (file.size > 10 * 1024 * 1024) {
      this.showAlert('Image size exceeds maximum limit of 10MB', 'error');
      input.value = '';
      return;
    }

    // File validation: Allowed image formats
    if (!file.type.match(/^image\/(jpeg|png|webp|gif|svg\+xml)$/)) {
      this.showAlert('Please upload a valid image file (JPG, PNG, WEBP, GIF, SVG)', 'error');
      input.value = '';
      return;
    }

    this.uploadingAvatar.set(true);
    this.alertMessage.set(null);

    this.userService.uploadAvatar(file).subscribe({
      next: res => {
        this.uploadingAvatar.set(false);
        input.value = '';
        if (res.success && res.data?.avatarUrl) {
          const newAvatarUrl = res.data.avatarUrl;
          this.avatarFailedUrl.set(null);
          const current = this.profileUser();
          if (current) {
            const updated = { ...current, avatarUrl: newAvatarUrl };
            this.profileUser.set(updated);
            this.authService.updateStoredUser(updated);
          }
          this.showAlert('Profile picture uploaded and saved successfully!', 'success');
        }
      },
      error: err => {
        this.uploadingAvatar.set(false);
        input.value = '';
        const msg = err.error?.message || 'Failed to upload profile picture. Please try again.';
        this.showAlert(msg, 'error');
      }
    });
  }

  removeAvatar(): void {
    const current = this.profileUser();
    if (!current || !current.avatarUrl) return;

    const updateReq: UpdateProfileRequest = {
      firstName: current.firstName,
      lastName: current.lastName,
      phoneNumber: current.phoneNumber,
      address: current.address,
      city: current.city,
      postalCode: current.postalCode,
      avatarUrl: ''
    };

    this.userService.updateProfile(updateReq).subscribe({
      next: res => {
        if (res.success && res.data) {
          this.profileUser.set(res.data);
          this.authService.updateStoredUser(res.data);
          this.showAlert('Profile picture removed successfully.', 'success');
        }
      },
      error: err => {
        const msg = err.error?.message || 'Failed to remove profile picture.';
        this.showAlert(msg, 'error');
      }
    });
  }

  saveStoreProfile(): void {
    this.savingStore.set(true);
    this.alertMessage.set(null);

    this.userService.updateSellerProfile(this.storeForm).subscribe({
      next: res => {
        this.savingStore.set(false);
        if (res.success && res.data) {
          const u = this.profileUser();
          if (u) {
            this.profileUser.set({ ...u, sellerProfile: res.data });
          }
          this.showAlert('Seller store profile updated successfully!', 'success');
        }
      },
      error: err => {
        this.savingStore.set(false);
        const msg = err.error?.message || 'Failed to update store profile';
        this.showAlert(msg, 'error');
      }
    });
  }

  changePassword(): void {
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.showAlert('New password and confirmation do not match', 'error');
      return;
    }

    this.savingPassword.set(true);
    this.alertMessage.set(null);

    this.userService.changePassword(this.passwordForm).subscribe({
      next: res => {
        this.savingPassword.set(false);
        this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
        this.showAlert('Password changed successfully!', 'success');
      },
      error: err => {
        this.savingPassword.set(false);
        const msg = err.error?.message || 'Failed to change password. Please check your current password.';
        this.showAlert(msg, 'error');
      }
    });
  }

  showAlert(message: string, type: 'success' | 'error'): void {
    this.alertMessage.set(message);
    this.alertType.set(type);
    setTimeout(() => {
      if (this.alertMessage() === message) {
        this.alertMessage.set(null);
      }
    }, 5000);
  }

  userInitials(): string {
    const u = this.profileUser();
    if (!u) return 'AP';
    const first = u.firstName ? u.firstName.charAt(0).toUpperCase() : '';
    const last = u.lastName ? u.lastName.charAt(0).toUpperCase() : '';
    return `${first}${last}` || 'U';
  }

  roleLabel(): string {
    const r = this.profileUser()?.role;
    if (r === 'ROLE_ADMIN') return 'Platform Administrator';
    if (r === 'ROLE_SELLER') return 'Verified Merchant';
    return 'Customer Account';
  }

  roleIcon(): string {
    const r = this.profileUser()?.role;
    if (r === 'ROLE_ADMIN') return '🛡️';
    if (r === 'ROLE_SELLER') return '🏪';
    return '👤';
  }

  roleBadgeClass(): string {
    const r = this.profileUser()?.role;
    if (r === 'ROLE_ADMIN') return 'bg-purple-500/15 text-purple-300 border border-purple-500/40';
    if (r === 'ROLE_SELLER') return 'bg-cyan-500/15 text-cyan-300 border border-cyan-500/40';
    return 'bg-emerald-500/15 text-emerald-300 border border-emerald-500/40';
  }

  avatarGradientClass(): string {
    const r = this.profileUser()?.role;
    if (r === 'ROLE_ADMIN') return 'bg-gradient-to-tr from-purple-600 via-purple-500 to-indigo-400 text-white';
    if (r === 'ROLE_SELLER') return 'bg-gradient-to-tr from-cyan-600 via-cyan-500 to-teal-400 text-slate-950';
    return 'bg-gradient-to-tr from-emerald-600 via-emerald-500 to-teal-400 text-slate-950';
  }

  rolePrivileges(): string {
    const r = this.profileUser()?.role;
    if (r === 'ROLE_ADMIN') return 'Full Catalog & Merchant Moderation, Audit Log Inspection';
    if (r === 'ROLE_SELLER') return 'Product Listing & Inventory Management, Sub-Order Dispatch';
    return 'Marketplace Shopping, Checkout, Order Tracking';
  }

  sellerVerificationBadgeClass(): string {
    const status = this.profileUser()?.sellerProfile?.verificationStatus;
    if (status === 'APPROVED') return 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/40';
    if (status === 'PENDING') return 'bg-amber-500/20 text-amber-400 border border-amber-500/40';
    return 'bg-rose-500/20 text-rose-400 border border-rose-500/40';
  }

  memberSinceDate(): string {
    const created = this.profileUser()?.createdAt;
    if (!created) return 'September 2026';
    try {
      return new Date(created).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
    } catch {
      return 'September 2026';
    }
  }

  onImageError(event: any): void {
    event.target.style.display = 'none';
  }
}
