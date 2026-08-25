export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface OrderItem {
  id?: number;
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface Order {
  id: number;
  trackingNumber: string;
  userId: number;
  userEmail: string;
  totalAmount: number;
  status: OrderStatus;
  shippingAddress: string;
  shippingCity: string;
  shippingPostalCode: string;
  customerPhone?: string;
  paymentMethod?: string;
  notes?: string;
  items: OrderItem[];
  createdAt: string;
  updatedAt?: string;
}

export interface CreateOrderItemRequest {
  productId: number;
  quantity: number;
}

export interface CreateOrderRequest {
  userId?: number;
  userEmail?: string;
  items: CreateOrderItemRequest[];
  shippingAddress: string;
  shippingCity: string;
  shippingPostalCode: string;
  customerPhone?: string;
  paymentMethod?: string;
  notes?: string;
}

export interface CartItem {
  product: {
    id: number;
    name: string;
    price: number;
    imageUrl?: string;
    stockQuantity: number;
  };
  quantity: number;
}
