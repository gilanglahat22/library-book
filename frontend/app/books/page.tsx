'use client'

import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Search, Plus, Edit, Trash2, Eye, Book as BookIcon } from 'lucide-react'
import Link from 'next/link'
import { booksApi } from '@/lib/api'
import type { Book, BookFilters } from '@/types'

export default function BooksPage() {
  const [filters, setFilters] = useState<BookFilters>({
    page: 0,
    size: 10,
    sortBy: 'title',
    sortDir: 'asc',
  })
  const [searchTerm, setSearchTerm] = useState('')

  const { data: booksData, isLoading, error } = useQuery({
    queryKey: ['books', filters],
    queryFn: () => booksApi.getAll(filters),
  })

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setFilters({ ...filters, search: searchTerm, page: 0 })
  }

  const handlePageChange = (newPage: number) => {
    setFilters({ ...filters, page: newPage })
  }

  const getStatusBadge = (book: Book) => {
    if (book.availableCopies === 0) {
      return <span className="badge-red">Unavailable</span>
    } else if (book.availableCopies <= 2) {
      return <span className="badge-yellow">Low Stock</span>
    } else {
      return <span className="badge-green">Available</span>
    }
  }

  if (error) {
    return (
      <div className="text-center py-12">
        <div className="text-red-600">
          Error loading books. Please try again later.
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Books</h1>
          <p className="mt-2 text-gray-600">
            Manage your library's book collection
          </p>
        </div>
        <Link
          href="/books/new"
          className="btn-primary flex items-center space-x-2"
        >
          <Plus className="h-4 w-4" />
          <span>Add Book</span>
        </Link>
      </div>

      {/* Search and Filters */}
      <div className="card">
        <form onSubmit={handleSearch} className="flex gap-4">
          <div className="flex-1">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
              <input
                type="text"
                placeholder="Search books by title, author, category, or ISBN..."
                className="input pl-10"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
          <button type="submit" className="btn-primary">
            Search
          </button>
          {filters.search && (
            <button
              type="button"
              className="btn-outline"
              onClick={() => {
                setSearchTerm('')
                setFilters({ ...filters, search: undefined, page: 0 })
              }}
            >
              Clear
            </button>
          )}
        </form>
      </div>

      {/* Books List */}
      <div className="card">
        {isLoading ? (
          <div className="text-center py-12">
            <div className="text-gray-600">Loading books...</div>
          </div>
        ) : booksData?.data.content.length === 0 ? (
          <div className="text-center py-12">
            <BookIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">No books found</h3>
            <p className="mt-1 text-sm text-gray-500">
              {filters.search 
                ? 'Try adjusting your search criteria.'
                : 'Get started by adding your first book.'
              }
            </p>
            {!filters.search && (
              <div className="mt-6">
                <Link href="/books/new" className="btn-primary">
                  <Plus className="h-4 w-4 mr-2" />
                  Add Book
                </Link>
              </div>
            )}
          </div>
        ) : (
          <>
            {/* Results Summary */}
            <div className="mb-4 text-sm text-gray-600">
              Showing {booksData?.data.content.length} of {booksData?.data.totalElements} books
              {filters.search && (
                <span> for "{filters.search}"</span>
              )}
            </div>

            {/* Books Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {booksData?.data.content.map((book) => (
                <div
                  key={book.id}
                  className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow"
                >
                  <div className="flex justify-between items-start mb-3">
                    <div className="flex-1">
                      <h3 className="font-semibold text-gray-900 line-clamp-2">
                        {book.title}
                      </h3>
                      <p className="text-sm text-gray-600 mt-1">
                        by {book.author.name}
                      </p>
                    </div>
                    {getStatusBadge(book)}
                  </div>

                  <div className="space-y-2 text-sm text-gray-600">
                    <div>
                      <span className="font-medium">Category:</span> {book.category}
                    </div>
                    <div>
                      <span className="font-medium">Published:</span> {book.publishingYear}
                    </div>
                    {book.isbn && (
                      <div>
                        <span className="font-medium">ISBN:</span> {book.isbn}
                      </div>
                    )}
                    <div>
                      <span className="font-medium">Copies:</span>{' '}
                      {book.availableCopies} of {book.totalCopies} available
                    </div>
                  </div>

                  {book.description && (
                    <p className="mt-3 text-sm text-gray-600 line-clamp-3">
                      {book.description}
                    </p>
                  )}

                  {/* Actions */}
                  <div className="flex justify-end space-x-2 mt-4">
                    <Link
                      href={`/books/${book.id}`}
                      className="p-2 text-gray-400 hover:text-gray-600"
                      title="View Details"
                    >
                      <Eye className="h-4 w-4" />
                    </Link>
                    <Link
                      href={`/books/${book.id}/edit`}
                      className="p-2 text-gray-400 hover:text-primary-600"
                      title="Edit Book"
                    >
                      <Edit className="h-4 w-4" />
                    </Link>
                    <button
                      className="p-2 text-gray-400 hover:text-red-600"
                      title="Delete Book"
                      onClick={() => {
                        // TODO: Implement delete functionality
                        alert('Delete functionality will be implemented')
                      }}
                    >
                      <Trash2 className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              ))}
            </div>

            {/* Pagination */}
            {booksData?.data.totalPages && booksData.data.totalPages > 1 && (
              <div className="flex justify-center space-x-2 mt-6">
                <button
                  className="btn-outline"
                  disabled={booksData.data.first}
                  onClick={() => handlePageChange(filters.page! - 1)}
                >
                  Previous
                </button>
                
                <span className="flex items-center px-4 text-sm text-gray-600">
                  Page {(filters.page || 0) + 1} of {booksData.data.totalPages}
                </span>
                
                <button
                  className="btn-outline"
                  disabled={booksData.data.last}
                  onClick={() => handlePageChange(filters.page! + 1)}
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
} 