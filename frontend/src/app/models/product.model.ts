export interface Category {
  id: number;
  name: string;
  slug: string;
  description?: string;
  productCount?: number;
}

export interface Product {
  id: number;
  name: string;
  slug: string;
  description: string;
  price: number;
  stockQuantity: number;
  categoryId?: number;
  categoryName?: string;
  imageUrl?: string;
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
