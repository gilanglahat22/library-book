export interface Author {
  id: number;
  name: string;
  biography?: string;
  nationality?: string;
  birthYear?: number;
  createdAt: string;
  updatedAt: string;
}

export interface Book {
  id: number;
  title: string;
  isbn?: string;
  category: string;
  publishingYear: number;
  description?: string;
  totalCopies: number;
  availableCopies: number;
  author: Author;
  createdAt: string;
  updatedAt: string;
}

export interface Member {
  id: number;
  name: string;
  email: string;
  phone?: string;
  address?: string;
  membershipDate: string;
  status: 'ACTIVE' | 'SUSPENDED' | 'EXPIRED';
  createdAt: string;
  updatedAt: string;
}

export interface BorrowedBook {
  id: number;
  member: Member;
  book: Book;
  borrowDate: string;
  dueDate?: string;
  returnDate?: string;
  status: 'BORROWED' | 'RETURNED' | 'OVERDUE' | 'LOST';
  notes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
}

export interface ApiError {
  message: string;
  status?: number;
}

export interface SearchFilters {
  search?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface BookFilters extends SearchFilters {
  category?: string;
  authorId?: number;
  startYear?: number;
  endYear?: number;
  available?: boolean;
}

export interface MemberFilters extends SearchFilters {
  status?: Member['status'];
}

export interface BorrowedBookFilters extends SearchFilters {
  status?: BorrowedBook['status'];
  borrowDate?: string;
  startDate?: string;
  endDate?: string;
} 