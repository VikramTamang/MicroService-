export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  role: 'ROLE_CUSTOMER' | 'ROLE_ADMIN';
  address?: string;
  city?: string;
  postalCode?: string;
  enabled: boolean;
  createdAt?: string;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface LoginRequest {
  email: string;
  password?: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password?: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  postalCode?: string;
}
