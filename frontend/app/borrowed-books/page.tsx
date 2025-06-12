'use client';

import { useState } from 'react';
import { useToast } from '@/components/ui/use-toast';
import { DataTable } from '@/components/ui/data-table';
import { columns } from './columns';
import { BorrowedBookFilters, BorrowedBook } from '@/types';
import { borrowedBooksApi } from '@/lib/api';
import { useQuery, useQueryClient } from '@tanstack/react-query';

export default function BorrowedBooksPage() {
  const { toast } = useToast();
  const queryClient = useQueryClient();
  const [filters, setFilters] = useState<BorrowedBookFilters>({
    page: 0,
    size: 10,
    sortBy: 'borrowDate',
    sortDir: 'desc',
    status: undefined
  });

  const { data: borrowedBooksData, isLoading, error } = useQuery({
    queryKey: ['borrowed-books', filters],
    queryFn: () => borrowedBooksApi.getAll(filters),
    retry: 1,
    staleTime: 30000, // 30 seconds
  });

  const handleRetry = () => {
    queryClient.invalidateQueries({ queryKey: ['borrowed-books'] });
  };

  const handleStatusFilter = (status: string | undefined) => {
    setFilters({ 
      ...filters, 
      status: status as 'BORROWED' | 'RETURNED' | 'OVERDUE' | 'LOST' | undefined, 
      page: 0 
    });
  };

  const handlePageChange = (newPage: number) => {
    setFilters({ ...filters, page: newPage });
  };

  if (error) {
    return (
      <div className="container mx-auto py-10">
        <div className="text-center py-12">
          <div className="text-red-600">
            Error loading borrowed books. Please try again later.
            <p className="mt-2 text-sm">
              {error instanceof Error ? error.message : 'Unknown error occurred'}
            </p>
            <button 
              className="btn-primary mt-4"
              onClick={handleRetry}
            >
              Retry
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-10">
      <div className="mb-6">
        <div className="flex gap-2">
          <button 
            className={`btn ${filters.status === undefined ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => handleStatusFilter(undefined)}
          >
            All
          </button>
          <button 
            className={`btn ${filters.status === 'BORROWED' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => handleStatusFilter('BORROWED')}
          >
            Borrowed
          </button>
          <button 
            className={`btn ${filters.status === 'RETURNED' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => handleStatusFilter('RETURNED')}
          >
            Returned
          </button>
          <button 
            className={`btn ${filters.status === 'OVERDUE' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => handleStatusFilter('OVERDUE')}
          >
            Overdue
          </button>
          <button 
            className={`btn ${filters.status === 'LOST' ? 'btn-primary' : 'btn-outline'}`}
            onClick={() => handleStatusFilter('LOST')}
          >
            Lost
          </button>
        </div>
      </div>

      <DataTable
        columns={columns}
        data={borrowedBooksData?.data.content || []}
        loading={isLoading}
      />

      {/* Pagination */}
      {borrowedBooksData?.data.totalPages && borrowedBooksData.data.totalPages > 1 && (
        <div className="flex justify-center space-x-2 mt-6">
          <button
            className="btn-outline"
            disabled={borrowedBooksData.data.first}
            onClick={() => handlePageChange((filters.page || 0) - 1)}
          >
            Previous
          </button>
          
          <span className="flex items-center px-4 text-sm text-gray-600">
            Page {(filters.page || 0) + 1} of {borrowedBooksData.data.totalPages}
          </span>
          
          <button
            className="btn-outline"
            disabled={borrowedBooksData.data.last}
            onClick={() => handlePageChange((filters.page || 0) + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
} 