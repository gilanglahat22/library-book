import { Author } from './author';

export interface Book {
  id: number;
  title: string;
  isbn?: string;
  author: Author;
  category: string;
  publishingYear: number;
  description?: string;
  totalCopies: number;
  availableCopies: number;
  createdAt: string;
  updatedAt: string;
} 