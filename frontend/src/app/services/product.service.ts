import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { Category, CreateProductRequest, Product, ProductAuditLog, UpdateProductRequest } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private http = inject(HttpClient);
  private readonly GATEWAY_URL = 'http://localhost:8080/api/v1';

  // --- Public Methods ---
  getProducts(categoryId?: number, search?: string, page = 0, size = 12): Observable<ApiResponse<PageResponse<Product>>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (categoryId) {
      params = params.set('categoryId', categoryId.toString());
    }
    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }

    return this.http.get<ApiResponse<PageResponse<Product>>>(`${this.GATEWAY_URL}/products`, { params });
  }

  getProductById(id: number): Observable<ApiResponse<Product>> {
    return this.http.get<ApiResponse<Product>>(`${this.GATEWAY_URL}/products/${id}`);
  }

  getCategories(): Observable<ApiResponse<Category[]>> {
    return this.http.get<ApiResponse<Category[]>>(`${this.GATEWAY_URL}/categories`);
  }

  uploadImage(file: File): Observable<ApiResponse<{ imageUrl: string }>> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ApiResponse<{ imageUrl: string }>>(`${this.GATEWAY_URL}/products/upload-image`, formData);
  }

  // --- Seller Methods ---
  getSellerProducts(page = 0, size = 20): Observable<ApiResponse<PageResponse<Product>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PageResponse<Product>>>(`${this.GATEWAY_URL}/seller/products`, { params });
  }

  createSellerProduct(request: CreateProductRequest): Observable<ApiResponse<Product>> {
    return this.http.post<ApiResponse<Product>>(`${this.GATEWAY_URL}/seller/products`, request);
  }

  updateSellerProduct(id: number, request: UpdateProductRequest): Observable<ApiResponse<Product>> {
    return this.http.put<ApiResponse<Product>>(`${this.GATEWAY_URL}/seller/products/${id}`, request);
  }

  updateSellerStock(id: number, stockQuantity: number): Observable<ApiResponse<Product>> {
    return this.http.patch<ApiResponse<Product>>(`${this.GATEWAY_URL}/seller/products/${id}/stock`, { stockQuantity });
  }

  deleteSellerProduct(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.GATEWAY_URL}/seller/products/${id}`);
  }

  // --- Admin Moderation Methods ---
  getPendingProducts(page = 0, size = 20): Observable<ApiResponse<PageResponse<Product>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PageResponse<Product>>>(`${this.GATEWAY_URL}/admin/products/pending`, { params });
  }

  approveProduct(id: number): Observable<ApiResponse<Product>> {
    return this.http.post<ApiResponse<Product>>(`${this.GATEWAY_URL}/admin/products/${id}/approve`, {});
  }

  rejectProduct(id: number, reason: string): Observable<ApiResponse<Product>> {
    return this.http.post<ApiResponse<Product>>(`${this.GATEWAY_URL}/admin/products/${id}/reject`, { reason });
  }

  suspendProduct(id: number, reason: string): Observable<ApiResponse<Product>> {
    return this.http.post<ApiResponse<Product>>(`${this.GATEWAY_URL}/admin/products/${id}/suspend`, { reason });
  }

  getAllAdminProducts(page = 0, size = 50): Observable<ApiResponse<PageResponse<Product>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PageResponse<Product>>>(`${this.GATEWAY_URL}/admin/products`, { params });
  }

  deleteAdminProduct(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.GATEWAY_URL}/admin/products/${id}`);
  }

  getProductAuditLogs(id: number): Observable<ApiResponse<ProductAuditLog[]>> {
    return this.http.get<ApiResponse<ProductAuditLog[]>>(`${this.GATEWAY_URL}/admin/products/${id}/audit-logs`);
  }

  // Legacy / fallback
  createProduct(request: CreateProductRequest): Observable<ApiResponse<Product>> {
    return this.createSellerProduct(request);
  }

  deleteProduct(id: number): Observable<ApiResponse<void>> {
    return this.deleteSellerProduct(id);
  }
}
