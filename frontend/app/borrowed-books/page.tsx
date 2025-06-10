'use client';

import { useEffect, useState } from 'react';
import { useToast } from '@/components/ui/use-toast';
import { DataTable } from '@/components/ui/data-table';
import { columns } from './columns';
import { BorrowedBook } from '@/types/borrowed-book';
import { api } from '@/lib/api';

export default function BorrowedBooksPage() {
  const [borrowedBooks, setBorrowedBooks] = useState<BorrowedBook[]>([]);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  const fetchBorrowedBooks = async () => {
    try {
      setLoading(true);
      const { data } = await api.get('/api/borrowed-books');
      setBorrowedBooks(data);
    } catch (error) {
      toast.error('Failed to load borrowed books');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBorrowedBooks();
  }, []);

  return (
    <div className="container mx-auto py-10">
      <DataTable
        columns={columns}
        data={borrowedBooks}
        loading={loading}
      />
    </div>
  );
} 