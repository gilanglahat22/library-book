'use client';

import { useEffect, useState } from 'react';
import { useToast } from '@/components/ui/use-toast';
import { DataTable } from '@/components/ui/data-table';
import { columns } from './columns';
import { Member } from '@/types/member';
import { api } from '@/lib/api';

export default function MembersPage() {
  const [members, setMembers] = useState<Member[]>([]);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  const fetchMembers = async () => {
    try {
      setLoading(true);
      const { data } = await api.get('/api/members');
      setMembers(data);
    } catch (error) {
      toast.error('Failed to load members');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMembers();
  }, []);

  return (
    <div className="container mx-auto py-10">
      <DataTable
        columns={columns}
        data={members}
        loading={loading}
      />
    </div>
  );
} 