'use client'

import { useQuery } from '@tanstack/react-query'
import { BookOpen, Users, User, TrendingUp } from 'lucide-react'
import Link from 'next/link'
import { booksApi, membersApi, authorsApi, borrowedBooksApi } from '@/lib/api'

interface StatCard {
  title: string
  value: number | string
  icon: React.ComponentType<any>
  href: string
  color: string
}

export default function Dashboard() {
  const { data: books } = useQuery({
    queryKey: ['books', 'stats'],
    queryFn: () => booksApi.getAll({ size: 1 }),
  })

  const { data: members } = useQuery({
    queryKey: ['members', 'stats'],
    queryFn: () => membersApi.getAll({ size: 1 }),
  })

  const { data: authors } = useQuery({
    queryKey: ['authors', 'stats'],
    queryFn: () => authorsApi.getAll({ size: 1 }),
  })

  const { data: currentBorrows } = useQuery({
    queryKey: ['borrowed-books', 'current-count'],
    queryFn: () => borrowedBooksApi.getCurrentBorrowsCount(),
  })

  const { data: overdueCount } = useQuery({
    queryKey: ['borrowed-books', 'overdue-count'],
    queryFn: () => borrowedBooksApi.getOverdueCount(),
  })

  const stats: StatCard[] = [
    {
      title: 'Total Books',
      value: books?.data.totalElements || 0,
      icon: BookOpen,
      href: '/books',
      color: 'bg-blue-500',
    },
    {
      title: 'Total Authors',
      value: authors?.data.totalElements || 0,
      icon: User,
      href: '/authors',
      color: 'bg-green-500',
    },
    {
      title: 'Total Members',
      value: members?.data.totalElements || 0,
      icon: Users,
      href: '/members',
      color: 'bg-purple-500',
    },
    {
      title: 'Current Borrows',
      value: currentBorrows?.data || 0,
      icon: TrendingUp,
      href: '/borrowed-books',
      color: 'bg-orange-500',
    },
  ]

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="text-center">
        <h1 className="text-3xl font-bold text-gray-900">
          Library Management Dashboard
        </h1>
        <p className="mt-2 text-gray-600">
          Manage your library's books, authors, and members efficiently
        </p>
      </div>

      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, index) => {
          const Icon = stat.icon
          return (
            <Link
              key={index}
              href={stat.href}
              className="block card hover:shadow-lg transition-shadow"
            >
              <div className="flex items-center">
                <div className={`flex-shrink-0 p-3 rounded-lg ${stat.color}`}>
                  <Icon className="h-6 w-6 text-white" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">
                    {stat.title}
                  </p>
                  <p className="text-2xl font-bold text-gray-900">
                    {stat.value}
                  </p>
                </div>
              </div>
            </Link>
          )
        })}
      </div>

      {/* Overdue Books Alert */}
      {overdueCount?.data && overdueCount.data > 0 && (
        <div className="bg-red-50 border border-red-200 rounded-md p-4">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <TrendingUp className="h-5 w-5 text-red-400" />
            </div>
            <div className="ml-3">
              <h3 className="text-sm font-medium text-red-800">
                Overdue Books Alert
              </h3>
              <div className="mt-1 text-sm text-red-700">
                There are {overdueCount.data} overdue books that need attention.
                <Link 
                  href="/borrowed-books?status=OVERDUE" 
                  className="ml-1 font-medium underline"
                >
                  View overdue books →
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Quick Actions
          </h3>
          <div className="space-y-3">
            <Link
              href="/books/new"
              className="block w-full btn-primary text-center"
            >
              Add New Book
            </Link>
            <Link
              href="/members/new"
              className="block w-full btn-outline text-center"
            >
              Add New Member
            </Link>
            <Link
              href="/borrowed-books/new"
              className="block w-full btn-outline text-center"
            >
              Borrow Book
            </Link>
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Recent Activity
          </h3>
          <div className="text-sm text-gray-600">
            <p>Recent borrowing and return activities will be displayed here.</p>
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Popular Books
          </h3>
          <div className="text-sm text-gray-600">
            <p>Most frequently borrowed books will be shown here.</p>
          </div>
        </div>
      </div>
    </div>
  )
} 