'use client';

import { ColumnDef } from '@tanstack/react-table';
import { BorrowedBook } from '@/types/borrowed-book';
import { Button } from '@/components/ui/button';
import { MoreHorizontal } from 'lucide-react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Badge } from '@/components/ui/badge';
import { format } from 'date-fns';

export const columns: ColumnDef<BorrowedBook>[] = [
  {
    accessorKey: 'id',
    header: 'ID',
  },
  {
    accessorKey: 'book.title',
    header: 'Book Title',
  },
  {
    accessorKey: 'member.name',
    header: 'Member Name',
  },
  {
    accessorKey: 'borrowDate',
    header: 'Borrow Date',
    cell: ({ row }) => {
      const date = row.getValue('borrowDate') as string;
      return date ? format(new Date(date), 'PP') : '-';
    },
  },
  {
    accessorKey: 'dueDate',
    header: 'Due Date',
    cell: ({ row }) => {
      const date = row.getValue('dueDate') as string;
      return date ? format(new Date(date), 'PP') : '-';
    },
  },
  {
    accessorKey: 'returnDate',
    header: 'Return Date',
    cell: ({ row }) => {
      const date = row.getValue('returnDate') as string | null;
      return date ? format(new Date(date), 'PP') : '-';
    },
  },
  {
    accessorKey: 'status',
    header: 'Status',
    cell: ({ row }) => {
      const status = row.getValue('status') as string;
      return (
        <Badge
          variant={
            status === 'BORROWED'
              ? 'default'
              : status === 'RETURNED'
              ? 'secondary'
              : status === 'OVERDUE'
              ? 'destructive'
              : 'outline'
          }
        >
          {status}
        </Badge>
      );
    },
  },
  {
    id: 'actions',
    cell: ({ row }) => {
      const borrowedBook = row.original;

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
              onClick={() => navigator.clipboard.writeText(borrowedBook.id.toString())}
            >
              Copy ID
            </DropdownMenuItem>
            <DropdownMenuItem>View Details</DropdownMenuItem>
            {borrowedBook.status === 'BORROWED' && (
              <DropdownMenuItem>Mark as Returned</DropdownMenuItem>
            )}
            {borrowedBook.status === 'BORROWED' && (
              <DropdownMenuItem>Mark as Lost</DropdownMenuItem>
            )}
          </DropdownMenuContent>
        </DropdownMenu>
      );
    },
  },
]; 