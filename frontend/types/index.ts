// Import types from individual files
import { Author } from './author';
import { Book } from './book';
import { Member } from './member';
import { BorrowedBook } from './borrowed-book';

// Re-export types
export type { Author } from './author';
export type { Book } from './book';
export type { Member } from './member';
export type { BorrowedBook } from './borrowed-book';

// Define additional types used across the application

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