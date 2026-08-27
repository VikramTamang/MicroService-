export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export type SubOrderStatus = 
  | 'PLACED'
  | 'CONFIRMED'
  | 'PACKED'
  | 'SHIPPED'
  | 'OUT_FOR_DELIVERY'
  | 'DELIVERED'
  | 'CANCELLED'
  | 'RETURN_REQUESTED'
  | 'RETURNED'
  | 'REFUNDED';

export type DerivedOrderStatus = 
  | 'PLACED'
  | 'PROCESSING'
  | 'PARTIALLY_SHIPPED'
  | 'SHIPPED'
  | 'PARTIALLY_DELIVERED'
  | 'DELIVERED'
  | 'COMPLETED'
  | 'CANCELLED';

export interface SubOrderItem {
  id: number;
  productId: number;
  productName: string;
  sku?: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface SubOrder {
  id: number;
  parentOrderId: number;
  orderNumber?: string;
  subOrderNumber: string;
  sellerId: number;
  sellerStoreName?: string;
  subtotal: number;
  shippingFee: number;
  status: SubOrderStatus;
  cancellationReason?: string;
  returnReason?: string;
  carrier?: string;
  trackingCode?: string;
  deliveredAt?: string;
  items: SubOrderItem[];
  createdAt: string;
}

export interface ParentOrder {
  id: number;
  orderNumber: string;
  customerId: number;
  customerEmail: string;
  totalAmount: number;
  shippingFee: number;
  taxAmount: number;
  paymentMethod: string;
  paymentStatus: string;
  derivedStatus: DerivedOrderStatus;
  shippingAddress: string;
  shippingCity: string;
  shippingPostalCode: string;
  subOrders: SubOrder[];
  createdAt: string;
}

// Backward compatible Order interface
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
  items: any[];
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
    sellerId?: number;
    sellerStoreName?: string;
    name: string;
    price: number;
    imageUrl?: string;
    stockQuantity: number;
  };
  quantity: number;
}

export interface FulfillSubOrderRequest {
  carrier: string;
  trackingCode: string;
}
