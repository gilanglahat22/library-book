package com.library.config;

import com.library.model.Author;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.BorrowedBook;
import com.library.service.AuthorService;
import com.library.service.BookService;
import com.library.service.MemberService;
import com.library.service.BorrowedBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthorService authorService;
    private final BookService bookService;
    private final MemberService memberService;
    private final BorrowedBookService borrowedBookService;

    @Autowired
    public DataInitializer(AuthorService authorService, BookService bookService, 
                          MemberService memberService, BorrowedBookService borrowedBookService) {
        this.authorService = authorService;
        this.bookService = bookService;
        this.memberService = memberService;
        this.borrowedBookService = borrowedBookService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (authorService.findAll().isEmpty()) {
            initializeData();
        }
    }

    private void initializeData() {
        // Create Authors
        Author scott = new Author("F. Scott Fitzgerald", 
                "American novelist and short story writer", "American", 1896);
        Author orwell = new Author("George Orwell", 
                "English novelist, essayist, journalist and critic", "British", 1903);
        Author harper = new Author("Harper Lee", 
                "American novelist", "American", 1926);
        Author tolkien = new Author("J.R.R. Tolkien", 
                "English writer and philologist", "British", 1892);
        Author jane = new Author("Jane Austen", 
                "English novelist", "British", 1775);

        scott = authorService.save(scott);
        orwell = authorService.save(orwell);
        harper = authorService.save(harper);
        tolkien = authorService.save(tolkien);
        jane = authorService.save(jane);

        // Create Books
        Book gatsby = new Book("The Great Gatsby", "978-0-7432-7356-5", "Fiction", 2020, 
                "A classic American novel about the Jazz Age", scott);
        gatsby.setTotalCopies(3);
        gatsby.setAvailableCopies(2);

        Book nineteen = new Book("1984", "978-0-452-28423-4", "Fiction", 1949, 
                "A dystopian social science fiction novel", orwell);
        nineteen.setTotalCopies(2);
        nineteen.setAvailableCopies(1);

        Book mockingbird = new Book("To Kill a Mockingbird", "978-0-06-112008-4", "Fiction", 1960, 
                "A novel about racial injustice and childhood", harper);
        mockingbird.setTotalCopies(2);
        mockingbird.setAvailableCopies(2);

        Book hobbit = new Book("The Hobbit", "978-0-547-92822-7", "Fantasy", 1937, 
                "A fantasy adventure novel", tolkien);
        hobbit.setTotalCopies(1);
        hobbit.setAvailableCopies(0);

        Book pride = new Book("Pride and Prejudice", "978-0-14-143951-8", "Romance", 1813, 
                "A romantic novel of manners", jane);
        pride.setTotalCopies(2);
        pride.setAvailableCopies(2);

        Book animal = new Book("Animal Farm", "978-0-452-28424-1", "Fiction", 1945, 
                "An allegorical novella", orwell);
        animal.setTotalCopies(1);
        animal.setAvailableCopies(1);

        gatsby = bookService.save(gatsby);
        nineteen = bookService.save(nineteen);
        mockingbird = bookService.save(mockingbird);
        hobbit = bookService.save(hobbit);
        pride = bookService.save(pride);
        animal = bookService.save(animal);

        // Create Members
        Member jack = new Member("Jack Smith", "jack@email.com", "+1-555-0123", "123 Main St, City, State");
        Member emily = new Member("Emily Johnson", "emily@email.com", "+1-555-0124", "456 Oak Ave, City, State");
        Member michael = new Member("Michael Brown", "michael@email.com", "+1-555-0125", "789 Pine Rd, City, State");
        Member sarah = new Member("Sarah Davis", "sarah@email.com", "+1-555-0126", "321 Elm St, City, State");
        Member david = new Member("David Wilson", "david@email.com", "+1-555-0127", "654 Maple Dr, City, State");

        jack = memberService.save(jack);
        emily = memberService.save(emily);
        michael = memberService.save(michael);
        sarah = memberService.save(sarah);
        david = memberService.save(david);

        // Create Borrowed Books
        // Jack borrowed The Great Gatsby
        BorrowedBook borrowedGatsby = new BorrowedBook();
        borrowedGatsby.setMember(jack);
        borrowedGatsby.setBook(gatsby);
        borrowedGatsby.setBorrowDate(LocalDate.now().minusDays(10));
        borrowedGatsby.setDueDate(LocalDate.now().plusDays(4));
        borrowedGatsby.setStatus(BorrowedBook.BorrowStatus.BORROWED);
        borrowedGatsby.setNotes("First borrow");

        // Emily borrowed 1984
        BorrowedBook borrowed1984 = new BorrowedBook();
        borrowed1984.setMember(emily);
        borrowed1984.setBook(nineteen);
        borrowed1984.setBorrowDate(LocalDate.now().minusDays(5));
        borrowed1984.setDueDate(LocalDate.now().plusDays(9));
        borrowed1984.setStatus(BorrowedBook.BorrowStatus.BORROWED);

        // David borrowed The Hobbit
        BorrowedBook borrowedHobbit = new BorrowedBook();
        borrowedHobbit.setMember(david);
        borrowedHobbit.setBook(hobbit);
        borrowedHobbit.setBorrowDate(LocalDate.now().minusDays(20));
        borrowedHobbit.setDueDate(LocalDate.now().minusDays(6));
        borrowedHobbit.setStatus(BorrowedBook.BorrowStatus.OVERDUE);
        borrowedHobbit.setNotes("Overdue - need to follow up");

        // Sarah returned Pride and Prejudice
        BorrowedBook returnedPride = new BorrowedBook();
        returnedPride.setMember(sarah);
        returnedPride.setBook(pride);
        returnedPride.setBorrowDate(LocalDate.now().minusDays(25));
        returnedPride.setDueDate(LocalDate.now().minusDays(11));
        returnedPride.setReturnDate(LocalDate.now().minusDays(12));
        returnedPride.setStatus(BorrowedBook.BorrowStatus.RETURNED);

        borrowedBookService.borrowBook(borrowedGatsby);
        borrowedBookService.borrowBook(borrowed1984);
        borrowedBookService.borrowBook(borrowedHobbit);
        borrowedBookService.borrowBook(returnedPride);

        System.out.println("Sample data initialized successfully!");
        System.out.println("Created " + authorService.findAll().size() + " authors");
        System.out.println("Created " + bookService.findAll().size() + " books");
        System.out.println("Created " + memberService.findAll().size() + " members");
        System.out.println("Created " + borrowedBookService.findAll(Pageable.unpaged()).getContent().size() + " borrowed book records");
    }
} 