'use client';

import { useState } from 'react';
import { useToast } from '@/components/ui/use-toast';
import { DataTable } from '@/components/ui/data-table';
import { columns } from './columns';
import { Author } from '@/types';
import { authorsApi } from '@/lib/api';
import { useQuery, useQueryClient } from '@tanstack/react-query';

export default function AuthorsPage() {
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState({
    page: 0,
    size: 10,
    sortBy: 'name',
    sortDir: 'asc',
    search: ''
  });
  const { toast } = useToast();
  const queryClient = useQueryClient();

  const { data: authorsData, isLoading, error } = useQuery({
    queryKey: ['authors', filters],
    queryFn: () => authorsApi.getAll(filters),
    retry: 1,
    staleTime: 30000, // 30 seconds
  });

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setFilters({ ...filters, search: searchTerm, page: 0 });
  };

  const handleRetry = () => {
    queryClient.invalidateQueries({ queryKey: ['authors'] });
  };

  if (error) {
    return (
      <div className="container mx-auto py-10">
        <div className="text-center py-12">
          <div className="text-red-600">
            Error loading authors. Please try again later.
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
        <form onSubmit={handleSearch} className="flex gap-4">
          <div className="flex-1">
            <input
              type="text"
              placeholder="Search authors..."
              className="input"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <button type="submit" className="btn-primary">
            Search
          </button>
          {filters.search && (
            <button
              type="button"
              className="btn-outline"
              onClick={() => {
                setSearchTerm('');
                setFilters({ ...filters, search: '', page: 0 });
              }}
            >
              Clear
            </button>
          )}
        </form>
      </div>

      <DataTable
        columns={columns}
        data={authorsData?.data.content || []}
        loading={isLoading}
      />

      {/* Pagination */}
      {authorsData?.data.totalPages && authorsData.data.totalPages > 1 && (
        <div className="flex justify-center space-x-2 mt-6">
          <button
            className="btn-outline"
            disabled={authorsData.data.first}
            onClick={() => setFilters({ ...filters, page: (filters.page || 0) - 1 })}
          >
            Previous
          </button>
          
          <span className="flex items-center px-4 text-sm text-gray-600">
            Page {(filters.page || 0) + 1} of {authorsData.data.totalPages}
          </span>
          
          <button
            className="btn-outline"
            disabled={authorsData.data.last}
            onClick={() => setFilters({ ...filters, page: (filters.page || 0) + 1 })}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
} 