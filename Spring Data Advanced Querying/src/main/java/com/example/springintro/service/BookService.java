package com.example.springintro.service;

import com.example.springintro.model.entity.AgeRestriction;
import com.example.springintro.model.entity.Book;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface BookService {
    void seedBooks() throws IOException;

    List<Book> findAllBooksAfterYear(int year);

    List<String> findAllAuthorsWithBooksWithReleaseDateBeforeYear(int year);

    List<String> findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate(String firstName, String lastName);

    List<String> findAllBookTitlesWithAgeRestriction(AgeRestriction ageRestriction);

    List<String> findAllGoldBookTitlesWithCopiesLessThen5000();

    List<String> findAllBooksTitlesWithPriceLessThan5OrMoreThen40();

    List<String> findAllNotReleasedBookTitlesInYear(int year);

    List<String> findAllBooksReleasedBeforeDate(LocalDate localDate);

    List<String> findAllBookTitlesWhereTitleContainsString(String string);

    List<String> findAllTitleWithAuthorWithLastNameStartingWith(String startString);

    int findCountOfBooksWithTitleLengthLongerThan(int titleLength);

    List<String> findAllByTitle(String title);

}
