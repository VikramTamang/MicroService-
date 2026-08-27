export type ProductStatus = 'PENDING_REVIEW' | 'APPROVED' | 'ACTIVE' | 'REJECTED' | 'SUSPENDED';

export interface Category {
  id: number;
  name: string;
  slug: string;
  description?: string;
  productCount?: number;
}

export interface Product {
  id: number;
  sellerId: number;
  name: string;
  slug: string;
  sku?: string;
  description: string;
  price: number;
  stockQuantity: number;
  categoryId?: number;
  categoryName?: string;
  imageUrl?: string;
  status: ProductStatus;
  rejectionReason?: string;
  suspensionReason?: string;
  active: boolean;
  createdAt?: string;
}

export interface CreateProductRequest {
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  categoryId?: number;
  imageUrl?: string;
}

export interface UpdateProductRequest extends CreateProductRequest {
  active?: boolean;
}

export interface ProductAuditLog {
  id: number;
  productId: number;
  actorId: number;
  actorRole: string;
  action: string;
  previousStatus?: ProductStatus;
  newStatus?: ProductStatus;
  reason?: string;
  createdAt: string;
}
