export interface Member {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  membershipDate: string;
  status: 'ACTIVE' | 'SUSPENDED';
  createdAt: string;
  updatedAt: string;
} 