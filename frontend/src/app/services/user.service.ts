import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { ChangePasswordRequest, SellerProfile, UpdateProfileRequest, UpdateSellerProfileRequest, User } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private readonly USERS_URL = 'http://localhost:8080/api/v1/users';
  private readonly SELLERS_URL = 'http://localhost:8080/api/v1/sellers';

  getProfile(): Observable<ApiResponse<User>> {
    return this.http.get<ApiResponse<User>>(`${this.USERS_URL}/me`);
  }

  updateProfile(request: UpdateProfileRequest): Observable<ApiResponse<User>> {
    return this.http.put<ApiResponse<User>>(`${this.USERS_URL}/me`, request);
  }

  changePassword(request: ChangePasswordRequest): Observable<ApiResponse<void>> {
    return this.http.put<ApiResponse<void>>(`${this.USERS_URL}/me/password`, request);
  }

  getMySellerProfile(): Observable<ApiResponse<SellerProfile>> {
    return this.http.get<ApiResponse<SellerProfile>>(`${this.SELLERS_URL}/me`);
  }

  updateSellerProfile(request: UpdateSellerProfileRequest): Observable<ApiResponse<SellerProfile>> {
    return this.http.put<ApiResponse<SellerProfile>>(`${this.SELLERS_URL}/me`, request);
  }

  uploadAvatar(file: File): Observable<ApiResponse<{ avatarUrl: string }>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ApiResponse<{ avatarUrl: string }>>(`${this.USERS_URL}/upload-avatar`, formData);
  }
}
