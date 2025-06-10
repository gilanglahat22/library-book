import { Book } from './book';
import { Member } from './member';

export interface BorrowedBook {
  id: number;
  book: Book;
  member: Member;
  borrowDate: string;
  dueDate: string;
  returnDate: string | null;
  status: 'BORROWED' | 'RETURNED' | 'OVERDUE';
  notes?: string;
  createdAt: string;
  updatedAt: string;
} 