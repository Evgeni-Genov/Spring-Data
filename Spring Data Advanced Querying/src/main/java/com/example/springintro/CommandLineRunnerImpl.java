package com.example.springintro;

import com.example.springintro.model.entity.AgeRestriction;
import com.example.springintro.model.entity.Book;
import com.example.springintro.service.AuthorService;
import com.example.springintro.service.BookService;
import com.example.springintro.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookService bookService;
    private final BufferedReader bufferedReader;

    public CommandLineRunnerImpl(CategoryService categoryService, AuthorService authorService, BookService bookService, BufferedReader bufferedReader) {
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.bookService = bookService;
        this.bufferedReader = bufferedReader;
    }

    @Override
    public void run(String... args) throws Exception {
        seedData();

        //printAllBooksAfterYear(2000);
        //printAllAuthorsNamesWithBooksWithReleaseDateBeforeYear(1990);
        //printAllAuthorsAndNumberOfTheirBooks();
        //printALlBooksByAuthorNameOrderByReleaseDate("George", "Powell");

        System.out.println("Please select the exercise number: ");
        int exerciseNumber = Integer.parseInt(bufferedReader.readLine());

        switch (exerciseNumber) {
            case 1 -> booksTitlesByAge();
            case 2 -> goldenBook();
            case 3 -> booksByPrice();
            case 4 -> notReleasedBooks();
            case 5 -> booksReleasedBefore();
            case 6 -> authorsSearch();
            case 7 -> bookSearch();
            case 8 -> booksTitleSearch();
            case 9 -> countBooks();
            case 10 -> totalBooksCopies();
            case 11 -> reducedBook();
        }
    }

    private void reducedBook() throws IOException {
        System.out.println("Please enter the title");
        String title = bufferedReader.readLine();

        this.bookService
                .findAllByTitle(title)
                .forEach(System.out::println);
    }

    private void totalBooksCopies() {
        authorService
                .findAllAuthorsAndTheirTotalCopies()
                .forEach(System.out::println);
    }

    private void countBooks() throws IOException {
        System.out.println("Please enter the desired title length: ");

        int titleLength = Integer.parseInt(bufferedReader.readLine());

        System.out.println(bookService
                .findCountOfBooksWithTitleLengthLongerThan(titleLength));
    }

    private void booksTitleSearch() throws IOException {
        System.out.println("Please enter the starting String of the Author's last name: ");

        String startString = bufferedReader.readLine();

        bookService
                .findAllTitleWithAuthorWithLastNameStartingWith(startString)
                .forEach(System.out::println);
    }

    private void bookSearch() throws IOException {
        System.out.println("Please enter the containing String: ");

        String string = bufferedReader.readLine();

        bookService
                .findAllBookTitlesWhereTitleContainsString(string)
                .forEach(System.out::println);
    }

    private void authorsSearch() throws IOException {
        System.out.println("Please Enter the first name ends with str: ");
        String endString = bufferedReader.readLine();

        authorService
                .findAuthorFirstNameEndsWithStr(endString)
                .forEach(System.out::println);
    }

    private void booksReleasedBefore() throws IOException {
        System.out.println("Please enter date in format: dd-MM-yyyy");

        LocalDate localDate = LocalDate.parse(bufferedReader.readLine(),
                DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        bookService
                .findAllBooksReleasedBeforeDate(localDate)
                .forEach(System.out::println);
    }

    private void notReleasedBooks() throws IOException {
        System.out.println("Please enter the year: ");

        int year = Integer.parseInt(bufferedReader.readLine());

        bookService
                .findAllNotReleasedBookTitlesInYear(year)
                .forEach(System.out::println);

    }

    private void booksByPrice() {
        bookService
                .findAllBooksTitlesWithPriceLessThan5OrMoreThen40()
                .forEach(System.out::println);
    }

    private void goldenBook() {
        bookService
                .findAllGoldBookTitlesWithCopiesLessThen5000()
                .forEach(System.out::println);

    }

    private void booksTitlesByAge() throws IOException {
        System.out.println("Enter Age Restriction: ");
        AgeRestriction ageRestriction = AgeRestriction.valueOf(bufferedReader.readLine().toUpperCase());

        bookService
                .findAllBookTitlesWithAgeRestriction(ageRestriction)
                .forEach(System.out::println);

    }

    private void printALlBooksByAuthorNameOrderByReleaseDate(String firstName, String lastName) {
        bookService
                .findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate(firstName, lastName)
                .forEach(System.out::println);
    }

    private void printAllAuthorsAndNumberOfTheirBooks() {
        authorService
                .getAllAuthorsOrderByCountOfTheirBooks()
                .forEach(System.out::println);
    }

    private void printAllAuthorsNamesWithBooksWithReleaseDateBeforeYear(int year) {
        bookService
                .findAllAuthorsWithBooksWithReleaseDateBeforeYear(year)
                .forEach(System.out::println);
    }

    private void printAllBooksAfterYear(int year) {
        bookService
                .findAllBooksAfterYear(year)
                .stream()
                .map(Book::getTitle)
                .forEach(System.out::println);
    }

    private void seedData() throws IOException {
        categoryService.seedCategories();
        authorService.seedAuthors();
        bookService.seedBooks();
    }
}
