'use client';

import { ColumnDef } from '@tanstack/react-table';
import { Author } from '@/types/author';
import { Button } from '@/components/ui/button';
import { MoreHorizontal } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

export const columns: ColumnDef<Author>[] = [
  {
    accessorKey: 'name',
    header: 'Name',
  },
  {
    accessorKey: 'nationality',
    header: 'Nationality',
  },
  {
    accessorKey: 'birthYear',
    header: 'Birth Year',
  },
  {
    accessorKey: 'biography',
    header: 'Biography',
    cell: ({ row }) => {
      const biography = row.getValue('  biography') as string;
      return biography ? `${biography.substring(0, 100)}...` : '-';
    },
  },
  {
    id: 'actions',
    cell: ({ row }) => {
      const author = row.original;

      return (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-8 w-8 p-0">
              <span className="sr-only">Open menu</span>
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuLabel>Actions</DropdownMenuLabel>
            <DropdownMenuItem
              onClick={() => navigator.clipboard.writeText(author.id.toString())}
            >
              Copy ID
            </DropdownMenuItem>
            <DropdownMenuItem>View Details</DropdownMenuItem>
            <DropdownMenuItem>Edit</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      );
    },
  },
]; 