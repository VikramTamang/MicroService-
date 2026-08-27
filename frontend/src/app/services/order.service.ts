import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiResponse, PageResponse } from '../models/api-response.model';
import { CreateOrderRequest, FulfillSubOrderRequest, ParentOrder, SubOrder } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private http = inject(HttpClient);
  private readonly GATEWAY_URL = 'http://localhost:8080/api/v1';

  // --- Customer Methods ---
  checkout(request: CreateOrderRequest): Observable<ApiResponse<ParentOrder>> {
    return this.http.post<ApiResponse<ParentOrder>>(`${this.GATEWAY_URL}/orders`, request);
  }

  getMyParentOrders(page = 0, size = 20): Observable<ApiResponse<PageResponse<ParentOrder>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PageResponse<ParentOrder>>>(`${this.GATEWAY_URL}/orders/my-orders`, { params });
  }

  getParentOrderByNumber(orderNumber: string): Observable<ApiResponse<ParentOrder>> {
    return this.http.get<ApiResponse<ParentOrder>>(`${this.GATEWAY_URL}/orders/${orderNumber}`);
  }

  cancelSubOrderAsCustomer(subOrderNumber: string, reason?: string): Observable<ApiResponse<SubOrder>> {
    return this.http.post<ApiResponse<SubOrder>>(`${this.GATEWAY_URL}/orders/sub-orders/${subOrderNumber}/cancel`, { reason });
  }

  // --- Seller Fulfillment Methods ---
  getSellerSubOrders(page = 0, size = 20): Observable<ApiResponse<PageResponse<SubOrder>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PageResponse<SubOrder>>>(`${this.GATEWAY_URL}/seller/sub-orders`, { params });
  }

  confirmSubOrder(subOrderNumber: string): Observable<ApiResponse<SubOrder>> {
    return this.http.post<ApiResponse<SubOrder>>(`${this.GATEWAY_URL}/seller/sub-orders/${subOrderNumber}/confirm`, {});
  }

  packSubOrder(subOrderNumber: string): Observable<ApiResponse<SubOrder>> {
    return this.http.post<ApiResponse<SubOrder>>(`${this.GATEWAY_URL}/seller/sub-orders/${subOrderNumber}/pack`, {});
  }

  shipSubOrder(subOrderNumber: string, request: FulfillSubOrderRequest): Observable<ApiResponse<SubOrder>> {
    return this.http.post<ApiResponse<SubOrder>>(`${this.GATEWAY_URL}/seller/sub-orders/${subOrderNumber}/ship`, request);
  }

  cancelSubOrderAsSeller(subOrderNumber: string, reason: string): Observable<ApiResponse<SubOrder>> {
    return this.http.post<ApiResponse<SubOrder>>(`${this.GATEWAY_URL}/seller/sub-orders/${subOrderNumber}/cancel`, { reason });
  }

  // --- Admin Methods (View-Only) ---
  getAllParentOrders(page = 0, size = 20): Observable<ApiResponse<PageResponse<ParentOrder>>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<ApiResponse<PageResponse<ParentOrder>>>(`${this.GATEWAY_URL}/admin/orders`, { params });
  }

  // Backward compatibility
  createOrder(request: CreateOrderRequest): Observable<ApiResponse<any>> {
    return this.checkout(request);
  }

  getMyOrders(): Observable<ApiResponse<any>> {
    return this.getMyParentOrders();
  }

  getAllOrders(page = 0, size = 20): Observable<ApiResponse<PageResponse<any>>> {
    return this.getAllParentOrders(page, size);
  }

  updateOrderStatus(orderId: number, status: any): Observable<ApiResponse<any>> {
    return this.http.patch<ApiResponse<any>>(`${this.GATEWAY_URL}/orders/${orderId}/status`, { status });
  }
}
