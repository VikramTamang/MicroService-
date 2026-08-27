import { Injectable, signal, computed, effect } from '@angular/core';
import { CartItem } from '../models/order.model';
import { Product } from '../models/product.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly CART_KEY = 'apex_cart_items';

  items = signal<CartItem[]>(this.loadStoredCart());

  itemCount = computed(() => {
    return this.items().reduce((total, item) => total + item.quantity, 0);
  });

  subtotal = computed(() => {
    return this.items().reduce((total, item) => total + (item.product.price * item.quantity), 0);
  });

  constructor() {
    effect(() => {
      localStorage.setItem(this.CART_KEY, JSON.stringify(this.items()));
    });
  }

  addToCart(product: Product, quantity = 1): void {
    this.items.update(current => {
      const existingIndex = current.findIndex(i => i.product.id === product.id);
      if (existingIndex > -1) {
        const updated = [...current];
        const newQty = updated[existingIndex].quantity + quantity;
        updated[existingIndex] = {
          ...updated[existingIndex],
          quantity: Math.min(newQty, product.stockQuantity)
        };
        return updated;
      } else {
        return [...current, {
          product: {
            id: product.id,
            sellerId: product.sellerId || 1,
            sellerStoreName: (product.sellerId === 2) ? 'Nordic Home & Living' : 'Apex Electronics Store',
            name: product.name,
            price: product.price,
            imageUrl: product.imageUrl,
            stockQuantity: product.stockQuantity
          },
          quantity: Math.min(quantity, product.stockQuantity)
        }];
      }
    });
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }
    this.items.update(current => {
      return current.map(item => {
        if (item.product.id === productId) {
          return { ...item, quantity: Math.min(quantity, item.product.stockQuantity) };
        }
        return item;
      });
    });
  }

  removeFromCart(productId: number): void {
    this.items.update(current => current.filter(i => i.product.id !== productId));
  }

  clearCart(): void {
    this.items.set([]);
  }

  private loadStoredCart(): CartItem[] {
    const data = localStorage.getItem(this.CART_KEY);
    if (!data) return [];
    try {
      return JSON.parse(data) as CartItem[];
    } catch {
      return [];
    }
  }
}
