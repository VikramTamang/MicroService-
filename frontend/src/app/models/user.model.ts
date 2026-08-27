export type UserRole = 'ROLE_CUSTOMER' | 'ROLE_SELLER' | 'ROLE_ADMIN';
export type UserStatus = 'ACTIVE' | 'PENDING_VERIFICATION' | 'SUSPENDED' | 'DEACTIVATED';
export type SellerVerificationStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED';

export interface SellerProfile {
  id: number;
  userId: number;
  storeName: string;
  storeSlug: string;
  businessRegistrationNumber?: string;
  taxIdentificationNumber?: string;
  storeDescription?: string;
  logoUrl?: string;
  bannerUrl?: string;
  verificationStatus: SellerVerificationStatus;
  rejectionReason?: string;
  suspensionReason?: string;
  commissionRate?: number;
  ratingAvg?: number;
  ratingCount?: number;
  createdAt?: string;
}

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  role: UserRole;
  status?: UserStatus;
  address?: string;
  city?: string;
  postalCode?: string;
  enabled: boolean;
  sellerProfile?: SellerProfile;
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
  role?: string;
  storeName?: string;
}

export interface SellerOnboardingRequest {
  storeName: string;
  businessRegistrationNumber?: string;
  taxIdentificationNumber?: string;
  storeDescription?: string;
  logoUrl?: string;
  bannerUrl?: string;
}
