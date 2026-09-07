import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { SellerOnboardingRequest, SellerProfile, User, UserStatus } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class SellerService {
  private http = inject(HttpClient);
  private readonly GATEWAY_URL = 'http://localhost:8080/api/v1';

  // --- Seller Operations ---
  submitOnboarding(request: SellerOnboardingRequest): Observable<ApiResponse<SellerProfile>> {
    return this.http.post<ApiResponse<SellerProfile>>(`${this.GATEWAY_URL}/sellers/onboarding`, request);
  }

  getMySellerProfile(): Observable<ApiResponse<SellerProfile>> {
    return this.http.get<ApiResponse<SellerProfile>>(`${this.GATEWAY_URL}/sellers/me`);
  }

  getSellerById(id: number): Observable<ApiResponse<SellerProfile>> {
    return this.http.get<ApiResponse<SellerProfile>>(`${this.GATEWAY_URL}/sellers/${id}`);
  }

  // --- Admin Moderation Operations ---
  getAllSellers(): Observable<ApiResponse<SellerProfile[]>> {
    return this.http.get<ApiResponse<SellerProfile[]>>(`${this.GATEWAY_URL}/admin/sellers`);
  }

  getPendingSellers(): Observable<ApiResponse<SellerProfile[]>> {
    return this.http.get<ApiResponse<SellerProfile[]>>(`${this.GATEWAY_URL}/admin/sellers/pending`);
  }

  approveSeller(id: number): Observable<ApiResponse<SellerProfile>> {
    return this.http.post<ApiResponse<SellerProfile>>(`${this.GATEWAY_URL}/admin/sellers/${id}/approve`, {});
  }

  rejectSeller(id: number, reason: string): Observable<ApiResponse<SellerProfile>> {
    return this.http.post<ApiResponse<SellerProfile>>(`${this.GATEWAY_URL}/admin/sellers/${id}/reject`, { reason });
  }

  suspendSeller(id: number, reason: string): Observable<ApiResponse<SellerProfile>> {
    return this.http.post<ApiResponse<SellerProfile>>(`${this.GATEWAY_URL}/admin/sellers/${id}/suspend`, { reason });
  }

  getCustomers(): Observable<ApiResponse<User[]>> {
    return this.http.get<ApiResponse<User[]>>(`${this.GATEWAY_URL}/admin/customers`);
  }

  getCustomerById(id: number): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.GATEWAY_URL}/admin/customers/${id}`);
  }

  createCustomer(request: any): Observable<ApiResponse<User>> {
    return this.http.post<ApiResponse<User>>(`${this.GATEWAY_URL}/admin/customers`, request);
  }

  updateCustomer(id: number, request: any): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.GATEWAY_URL}/admin/customers/${id}`, request);
  }

  updateUserStatus(userId: number, status: UserStatus, reason?: string): Observable<ApiResponse<User>> {
    return this.http.patch<ApiResponse<User>>(`${this.GATEWAY_URL}/admin/users/${userId}/status`, { status, reason });
  }

  resetUserPassword(userId: number, newPassword: string): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.GATEWAY_URL}/admin/users/${userId}/reset-password`, { newPassword });
  }

  deleteCustomer(userId: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.GATEWAY_URL}/admin/customers/${userId}`);
  }
}
