'use client';

import { useEffect, useState } from 'react';
import { useToast } from '@/components/ui/use-toast';
import { DataTable } from '@/components/ui/data-table';
import { columns } from './columns';
import { Author } from '@/types/author';
import { api } from '@/lib/api';

export default function AuthorsPage() {
  const [authors, setAuthors] = useState<Author[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const { toast } = useToast();

  const fetchAuthors = async () => {
    try {
      setLoading(true);
      const { data } = await api.get('/api/authors');
      setAuthors(data);
    } catch (error) {
      toast.error('Failed to load authors');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAuthors();
  }, []);

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchAuthors();
      return;
    }

    try {
      const response = await fetch(`/api/authors/search?query=${encodeURIComponent(searchTerm)}`);
      if (!response.ok) throw new Error('Search failed');
      const data = await response.json();
      setAuthors(data);
    } catch (error) {
      toast.error('Search failed');
    }
  };

  return (
    <div className="container mx-auto py-10">
      <DataTable
        columns={columns}
        data={authors}
        loading={loading}
      />
    </div>
  );
} 