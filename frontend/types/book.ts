import { Author } from './author';

export interface Book {
  id: number;
  title: string;
  isbn: string;
  author: Author;
  category: string;
  publicationYear: number;
  publisher: string;
  description?: string;
  available: boolean;
  createdAt: string;
  updatedAt: string;
} 