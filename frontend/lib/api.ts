import axios from 'axios';
import type { 
  Author, 
  Book, 
  Member, 
  BorrowedBook, 
  PageResponse, 
  BookFilters, 
  MemberFilters, 
  BorrowedBookFilters 
} from '@/types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for logging
apiClient.interceptors.request.use(
  (config) => {
    console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Response Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Authors API
export const authorsApi = {
  getAll: (filters?: { search?: string; page?: number; size?: number; sortBy?: string; sortDir?: string }) => 
    apiClient.get<PageResponse<Author>>('/authors', { params: filters }),
  
  getAllWithoutPagination: () => 
    apiClient.get<Author[]>('/authors/all'),
  
  getById: (id: number) => 
    apiClient.get<Author>(`/authors/${id}`),
  
  create: (author: Omit<Author, 'id' | 'createdAt' | 'updatedAt'>) => 
    apiClient.post<Author>('/authors', author),
  
  update: (id: number, author: Omit<Author, 'id' | 'createdAt' | 'updatedAt'>) => 
    apiClient.put<Author>(`/authors/${id}`, author),
  
  delete: (id: number) => 
    apiClient.delete(`/authors/${id}`),
  
  search: (query: string, page?: number, size?: number) => 
    apiClient.get<PageResponse<Author>>('/authors/search', { 
      params: { query, page, size } 
    }),
};

// Books API
export const booksApi = {
  getAll: (filters?: BookFilters) => 
    apiClient.get<PageResponse<Book>>('/books', { params: filters }),
  
  getAllWithoutPagination: () => 
    apiClient.get<Book[]>('/books/all'),
  
  getById: (id: number) => 
    apiClient.get<Book>(`/books/${id}`),
  
  create: (book: Omit<Book, 'id' | 'createdAt' | 'updatedAt'>) => 
    apiClient.post<Book>('/books', book),
  
  update: (id: number, book: Omit<Book, 'id' | 'createdAt' | 'updatedAt'>) => 
    apiClient.put<Book>(`/books/${id}`, book),
  
  delete: (id: number) => 
    apiClient.delete(`/books/${id}`),
  
  search: (query: string, page?: number, size?: number) => 
    apiClient.get<PageResponse<Book>>('/books/search', { 
      params: { query, page, size } 
    }),
  
  getCategories: () => 
    apiClient.get<string[]>('/books/categories'),
  
  getByCategory: (category: string, page?: number, size?: number) => 
    apiClient.get<PageResponse<Book>>(`/books/by-category/${category}`, { 
      params: { page, size } 
    }),
  
  getByAuthor: (authorId: number, page?: number, size?: number) => 
    apiClient.get<PageResponse<Book>>(`/books/by-author/${authorId}`, { 
      params: { page, size } 
    }),
  
  checkAvailability: (id: number) => 
    apiClient.get<boolean>(`/books/${id}/availability`),
};

// Members API
export const membersApi = {
  getAll: (filters?: MemberFilters) => 
    apiClient.get<PageResponse<Member>>('/members', { params: filters }),
  
  getAllWithoutPagination: () => 
    apiClient.get<Member[]>('/members/all'),
  
  getById: (id: number) => 
    apiClient.get<Member>(`/members/${id}`),
  
  getWithBorrowedBooks: (id: number) => 
    apiClient.get<Member>(`/members/${id}/with-borrowed-books`),
  
  create: (member: Omit<Member, 'id' | 'createdAt' | 'updatedAt' | 'membershipDate'>) => 
    apiClient.post<Member>('/members', member),
  
  update: (id: number, member: Omit<Member, 'id' | 'createdAt' | 'updatedAt' | 'membershipDate'>) => 
    apiClient.put<Member>(`/members/${id}`, member),
  
  delete: (id: number) => 
    apiClient.delete(`/members/${id}`),
  
  suspend: (id: number) => 
    apiClient.patch<Member>(`/members/${id}/suspend`),
  
  activate: (id: number) => 
    apiClient.patch<Member>(`/members/${id}/activate`),
  
  search: (query: string, page?: number, size?: number) => 
    apiClient.get<PageResponse<Member>>('/members/search', { 
      params: { query, page, size } 
    }),
  
  canBorrow: (id: number) => 
    apiClient.get<boolean>(`/members/${id}/can-borrow`),
  
  getCurrentBorrowsCount: (id: number) => 
    apiClient.get<number>(`/members/${id}/current-borrows-count`),
};

// Borrowed Books API
export const borrowedBooksApi = {
  getAll: (filters?: BorrowedBookFilters) => 
    apiClient.get<PageResponse<BorrowedBook>>('/borrowed-books', { params: filters }),
  
  getAllWithoutPagination: () => 
    apiClient.get<BorrowedBook[]>('/borrowed-books/all'),
  
  getById: (id: number) => 
    apiClient.get<BorrowedBook>(`/borrowed-books/${id}`),
  
  getByMember: (memberId: number) => 
    apiClient.get<BorrowedBook[]>(`/borrowed-books/member/${memberId}`),
  
  getActiveBorrowsByMember: (memberId: number) => 
    apiClient.get<BorrowedBook[]>(`/borrowed-books/member/${memberId}/active`),
  
  getByBook: (bookId: number) => 
    apiClient.get<BorrowedBook[]>(`/borrowed-books/book/${bookId}`),
  
  getOverdue: () => 
    apiClient.get<BorrowedBook[]>('/borrowed-books/overdue'),
  
  borrowBook: (memberId: number, bookId: number, dueDate?: string, notes?: string) => 
    apiClient.post<BorrowedBook>('/borrowed-books/borrow', null, {
      params: { memberId, bookId, dueDate, notes }
    }),
  
  returnBook: (id: number) => 
    apiClient.patch<BorrowedBook>(`/borrowed-books/${id}/return`),
  
  markAsLost: (id: number) => 
    apiClient.patch<BorrowedBook>(`/borrowed-books/${id}/mark-lost`),
  
  update: (id: number, borrowedBook: Partial<BorrowedBook>) => 
    apiClient.put<BorrowedBook>(`/borrowed-books/${id}`, borrowedBook),
  
  delete: (id: number) => 
    apiClient.delete(`/borrowed-books/${id}`),
  
  search: (query: string, startDate?: string, endDate?: string, page?: number, size?: number) => 
    apiClient.get<PageResponse<BorrowedBook>>('/borrowed-books/search', { 
      params: { query, startDate, endDate, page, size } 
    }),
  
  getCurrentBorrowsCount: () => 
    apiClient.get<number>('/borrowed-books/statistics/current-borrows'),
  
  getOverdueCount: () => 
    apiClient.get<number>('/borrowed-books/statistics/overdue-count'),
};

export default apiClient; 