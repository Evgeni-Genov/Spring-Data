package com.example.springintro.service.impl;

import com.example.springintro.model.entity.*;
import com.example.springintro.repository.BookRepository;
import com.example.springintro.service.AuthorService;
import com.example.springintro.service.BookService;
import com.example.springintro.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private static final String BOOKS_FILE_PATH = "src/main/resources/files/books.txt";

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final CategoryService categoryService;

    public BookServiceImpl(BookRepository bookRepository, AuthorService authorService, CategoryService categoryService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.categoryService = categoryService;
    }

    @Override
    public void seedBooks() throws IOException {
        if (bookRepository.count() > 0) {
            return;
        }

        Files
                .readAllLines(Path.of(BOOKS_FILE_PATH))
                .forEach(row -> {
                    String[] bookInfo = row.split("\\s+");

                    Book book = createBookFromInfo(bookInfo);

                    bookRepository.save(book);
                });
    }

    @Override
    public List<Book> findAllBooksAfterYear(int year) {
        return bookRepository
                .findAllByReleaseDateAfter(LocalDate.of(year, 12, 31));
    }

    @Override
    public List<String> findAllAuthorsWithBooksWithReleaseDateBeforeYear(int year) {
        return bookRepository
                .findAllByReleaseDateBefore(LocalDate.of(year, 1, 1))
                .stream()
                .map(book -> String.format("%s %s", book.getAuthor().getFirstName(),
                        book.getAuthor().getLastName()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate(String firstName, String lastName) {
        return bookRepository
                .findAllByAuthor_FirstNameAndAuthor_LastNameOrderByReleaseDateDescTitle(firstName, lastName)
                .stream()
                .map(book -> String.format("%s %s %d",
                        book.getTitle(),
                        book.getReleaseDate(),
                        book.getCopies()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllBookTitlesWithAgeRestriction(AgeRestriction ageRestriction) {

        return bookRepository
                .findAllByAgeRestriction(ageRestriction)
                .stream()
                .map(Book::getTitle)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllGoldBookTitlesWithCopiesLessThen5000() {

        return bookRepository
                .findAllByEditionTypeAndCopiesLessThan(EditionType.GOLD, 5000)
                .stream()
                .map(Book::getTitle)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllBooksTitlesWithPriceLessThan5OrMoreThen40() {

        return bookRepository
                .findAllByPriceLessThanOrPriceGreaterThan(BigDecimal.valueOf(5L), BigDecimal.valueOf(40L))
                .stream()
                .map(book -> String.format("%s - $ %.2f",
                        book.getTitle(), book.getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllNotReleasedBookTitlesInYear(int year) {
        LocalDate lower = LocalDate.of(year, 1, 1);
        LocalDate upper = LocalDate.of(year, 12, 31);

        return bookRepository
                .findAllByReleaseDateBeforeOrReleaseDateAfter(lower, upper)
                .stream()
                .map(Book::getTitle)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllBooksReleasedBeforeDate(LocalDate localDate) {
        return bookRepository
                .findAllByReleaseDateBefore(localDate)
                .stream()
                .map(book -> String.format("%s %s %.2f",
                        book.getTitle(), book.getEditionType().name(), book.getPrice()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllBookTitlesWhereTitleContainsString(String string) {
        return bookRepository
                .findAllByTitleContaining(string)
                .stream()
                .map(Book::getTitle)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllTitleWithAuthorWithLastNameStartingWith(String startString) {
        return bookRepository
                .findAllByAuthor_LastNameStartsWith(startString)
                .stream()
                .map(book -> String.format("%s (%s %s)",
                        book.getTitle(),
                        book.getAuthor().getFirstName(),
                        book.getAuthor().getLastName()))
                .collect(Collectors.toList());
    }

    @Override
    public int findCountOfBooksWithTitleLengthLongerThan(int titleLength) {
        return bookRepository
                .countOfBooksWithTitleLengthMoreThan(titleLength);
    }

    private Book createBookFromInfo(String[] bookInfo) {
        EditionType editionType = EditionType.values()[Integer.parseInt(bookInfo[0])];

        LocalDate releaseDate = LocalDate
                .parse(bookInfo[1], DateTimeFormatter.ofPattern("d/M/yyyy"));

        Integer copies = Integer.parseInt(bookInfo[2]);
        BigDecimal price = new BigDecimal(bookInfo[3]);

        AgeRestriction ageRestriction = AgeRestriction
                .values()[Integer.parseInt(bookInfo[4])];

        String title = Arrays.stream(bookInfo)
                .skip(5)
                .collect(Collectors.joining(" "));

        Author author = authorService.getRandomAuthor();
        Set<Category> categories = categoryService
                .getRandomCategories();

        return new Book(editionType, releaseDate, copies, price, ageRestriction, title, author, categories);

    }

    @Override
    public List<String> findAllByTitle(String title) {
        return this.bookRepository.findAllByTitle(title)
                .stream()
                .map(book -> String.format("%s %s %s %.2f", book.getTitle(),
                        book.getEditionType().name(),
                        book.getAgeRestriction().name(),
                        book.getPrice()))
                .collect(Collectors.toList());
    }
}