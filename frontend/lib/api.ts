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

// Use the environment variable or default to the Main API URL
const baseURL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8090/api';

export const api = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json',
  },
  // Set to false to avoid CORS preflight issues
  withCredentials: false,
  // Add timeout
  timeout: 10000,
});

// Request interceptor for logging and adding API keys
api.interceptors.request.use(
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
api.interceptors.response.use(
  (response) => {
    console.log(`API Response: ${response.status} for ${response.config.url}`);
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
    api.get<PageResponse<Author>>('/authors', { params: filters }),
  
  getAllWithoutPagination: () => 
    api.get<Author[]>('/authors/all'),
  
  getById: (id: number) => 
    api.get<Author>(`/authors/${id}`),
  
  create: (author: Omit<Author, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.post<Author>('/authors', author),
  
  update: (id: number, author: Omit<Author, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.put<Author>(`/authors/${id}`, author),
  
  delete: (id: number) => 
    api.delete(`/authors/${id}`),
  
  search: (query: string, page?: number, size?: number) => 
    api.get<PageResponse<Author>>('/authors/search', { 
      params: { query, page, size } 
    }),
};

// Books API
export const booksApi = {
  getAll: (filters?: BookFilters) => 
    api.get<PageResponse<Book>>('/books', { params: filters }),
  
  getAllWithoutPagination: () => 
    api.get<Book[]>('/books/all'),
  
  getById: (id: number) => 
    api.get<Book>(`/books/${id}`),
  
  create: (book: Omit<Book, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.post<Book>('/books', book),
  
  update: (id: number, book: Omit<Book, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.put<Book>(`/books/${id}`, book),
  
  delete: (id: number) => 
    api.delete(`/books/${id}`),
  
  search: (query: string, page?: number, size?: number) => 
    api.get<PageResponse<Book>>('/books/search', { 
      params: { query, page, size } 
    }),
  
  getCategories: () => 
    api.get<string[]>('/books/categories'),
  
  getByCategory: (category: string, page?: number, size?: number) => 
    api.get<PageResponse<Book>>(`/books/by-category/${category}`, { 
      params: { page, size } 
    }),
  
  getByAuthor: (authorId: number, page?: number, size?: number) => 
    api.get<PageResponse<Book>>(`/books/by-author/${authorId}`, { 
      params: { page, size } 
    }),
  
  checkAvailability: (id: number) => 
    api.get<boolean>(`/books/${id}/availability`),
};

// Members API
export const membersApi = {
  getAll: (filters?: MemberFilters) => 
    api.get<PageResponse<Member>>('/members', { params: filters }),
  
  getAllWithoutPagination: () => 
    api.get<Member[]>('/members/all'),
  
  getById: (id: number) => 
    api.get<Member>(`/members/${id}`),
  
  getWithBorrowedBooks: (id: number) => 
    api.get<Member>(`/members/${id}/with-borrowed-books`),
  
  create: (member: Omit<Member, 'id' | 'createdAt' | 'updatedAt' | 'membershipDate'>) => 
    api.post<Member>('/members', member),
  
  update: (id: number, member: Omit<Member, 'id' | 'createdAt' | 'updatedAt' | 'membershipDate'>) => 
    api.put<Member>(`/members/${id}`, member),
  
  delete: (id: number) => 
    api.delete(`/members/${id}`),
  
  suspend: (id: number) => 
    api.patch<Member>(`/members/${id}/suspend`),
  
  activate: (id: number) => 
    api.patch<Member>(`/members/${id}/activate`),
  
  search: (query: string, page?: number, size?: number) => 
    api.get<PageResponse<Member>>('/members/search', { 
      params: { query, page, size } 
    }),
  
  canBorrow: (id: number) => 
    api.get<boolean>(`/members/${id}/can-borrow`),
  
  getCurrentBorrowsCount: (id: number) => 
    api.get<number>(`/members/${id}/current-borrows-count`),
};

// Borrowed Books API
export const borrowedBooksApi = {
  getAll: (filters?: BorrowedBookFilters) => 
    api.get<PageResponse<BorrowedBook>>('/borrowed-books', { params: filters }),
  
  getAllWithoutPagination: () => 
    api.get<BorrowedBook[]>('/borrowed-books/all'),
  
  getById: (id: number) => 
    api.get<BorrowedBook>(`/borrowed-books/${id}`),
  
  getByMember: (memberId: number) => 
    api.get<BorrowedBook[]>(`/borrowed-books/member/${memberId}`),
  
  getActiveBorrowsByMember: (memberId: number) => 
    api.get<BorrowedBook[]>(`/borrowed-books/member/${memberId}/active`),
  
  getByBook: (bookId: number) => 
    api.get<BorrowedBook[]>(`/borrowed-books/book/${bookId}`),
  
  getOverdue: () => 
    api.get<BorrowedBook[]>('/borrowed-books/overdue'),
  
  borrowBook: (memberId: number, bookId: number, dueDate?: string, notes?: string) => 
    api.post<BorrowedBook>('/borrowed-books/borrow', null, {
      params: { memberId, bookId, dueDate, notes }
    }),
  
  returnBook: (id: number) => 
    api.patch<BorrowedBook>(`/borrowed-books/${id}/return`),
  
  markAsLost: (id: number) => 
    api.patch<BorrowedBook>(`/borrowed-books/${id}/mark-lost`),
  
  update: (id: number, borrowedBook: Partial<BorrowedBook>) => 
    api.put<BorrowedBook>(`/borrowed-books/${id}`, borrowedBook),
  
  delete: (id: number) => 
    api.delete(`/borrowed-books/${id}`),
  
  search: (query: string, startDate?: string, endDate?: string, page?: number, size?: number) => 
    api.get<PageResponse<BorrowedBook>>('/borrowed-books/search', { 
      params: { query, startDate, endDate, page, size } 
    }),
  
  getCurrentBorrowsCount: () => 
    api.get<number>('/borrowed-books/statistics/current-borrows'),
  
  getOverdueCount: () => 
    api.get<number>('/borrowed-books/statistics/overdue-count'),
};

export default api; 