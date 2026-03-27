package com.capgemini.book_partner_portal.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.capgemini.book_partner_portal.entity.Author;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    private Author testAuthor;

    @BeforeEach
    void setup() {
        testAuthor = new Author(
            "123-45-6789", 
            "Doe", 
            "John", 
            "415 658-9932", 
            "6223 Bateman St.", 
            "Berkeley", 
            "CA", 
            "94705", 
            1
        );

        authorRepository.save(testAuthor);
    }


    // find all authors
    @Test
    void findAll_WhenAuthorsExist_ShouldReturnNonEmptyList(){
        List<Author> authors = authorRepository.findAll();
        assertThat(authors).isNotEmpty(); 
    }

    // find author with valid id
    @Test
    void findById_WithValidId_ShouldReturnAuthor() {
        Author author = authorRepository.findById("123-45-6789").orElse(null);
        assertThat(author).isNotNull();
    }
    
    // find author with invalid id 
    @Test
    void findById_WithInvalidId_ShouldReturnEmpty(){
        Author author = authorRepository.findById("999-99-9999").orElse(null);
        assertThat(author).isNull();
    }

    // find author when first name exists
    @Test
    void findByName_WhenFirstNameExists_ShouldReturnNonEmptyList(){
        List<Author> authors = authorRepository
        .findByFirstNameContainingIgnoreCase("John");
        assertThat(authors).isNotEmpty();
    }

    // find author when first name not exists
    @Test
    void findByFirstName_WhenFirstNameNotExists_ShouldReturnEmptyList(){
        List<Author> authors = authorRepository
        .findByFirstNameContainingIgnoreCase("Ramesh");
        assertThat(authors).isEmpty();
    }

    // find author when last name exists
    @Test
    void findByName_WhenLastNameExists_ShouldReturnNonEmptyList(){
        List<Author> authors = authorRepository
        .findByLastNameContainingIgnoreCase("Doe");
        assertThat(authors).isNotEmpty();
    }

    // find author when last name not exists
    @Test
    void findByLastName_WhenLastNameNotExists_ShouldReturnEmptyList(){
        List<Author> authors = authorRepository
        .findByLastNameContainingIgnoreCase("Sharma");
        assertThat(authors).isEmpty();
    }


    // find author when city exists
    @Test 
    void findByCity_WhenCityExists_ShouldReturnNonEmptyList(){
        List<Author> authors = authorRepository.findByCityIgnoreCase("Berkeley");
        assertThat(authors).isNotEmpty();
    }

    // find author when city not exists
    @Test 
    void findByCity_WhenCityNotExists_ShouldReturnEmptyList(){
        List<Author> authors = authorRepository.findByCityIgnoreCase("Gondia");
        assertThat(authors).isEmpty();
    }


    // find author when state exists
    @Test 
    void findByState_WhenStateExists_ShouldReturnNonEmptyList(){
        List<Author> authors = authorRepository.findByStateIgnoreCase("CA");
        assertThat(authors).isNotEmpty();
    }

    //find author when state not exists
    @Test 
    void findByState_WhenStateNotExists_ShouldReturnEmptyList(){
        List<Author> authors = authorRepository.findByStateIgnoreCase("Maharashtra");
        assertThat(authors).isEmpty();
    }

    //find author when phone exists
    @Test 
    void findByPhone_WhenPhoneExists_ShouldReturnNonEmptyList(){
        List<Author> authors = authorRepository.findByPhone("415 658-9932");
        assertThat(authors).isNotEmpty();
    }

    // find author when phone not exists
    @Test 
    void findByPhone_WhenPhoneNotExists_ShouldReturnEmptyList(){
        List<Author> authors = authorRepository.findByPhone("999 999-9999");
        assertThat(authors).isEmpty();
    }


    // create author with valid data
    @Test
    void createAuthor_WithValidData_ShouldCreateAuthor() {
        
        Author newAuthor = new Author(
            "987-65-4321",
            "Smith",
            "Alice",
            "415 123-8585",
            "Street 1",
            "Oakland",
            "CA",
            "94618",
            1
        );

        Author savedAuthor = authorRepository.save(newAuthor);

        // Assertions
        assertNotNull(savedAuthor);
        assertEquals("987-65-4321", savedAuthor.getAuId());
        assertEquals("Alice", savedAuthor.getFirstName());
        assertEquals("Smith", savedAuthor.getLastName());

        // verify from DB
        Optional<Author> fetched = authorRepository.findById("987-65-4321");
        assertTrue(fetched.isPresent());
    }


    // create author with invalid id
    @Test
    void createAuthor_WithInvalidId_ShouldThrowConstraintViolationException() {

        Author invalidAuthor = new Author(
            "12311",  // invalid ID
            "Smith",
            "Alice",
            "415 123-8585",
            "Street 1",
            "Oakland",
            "CA",
            "94618",
            1
        );

        assertThrows(ConstraintViolationException.class, () -> {
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }


    // create author with null contract
    @Test
    void createAuthor_WithNullContract_ShouldThrowJpaSystemException() {

        Author invalidAuthor = new Author(
            "111-11-1111", 
            "Smith",
            "Alice",
            "415 123-8585",
            "Street 1",
            "Oakland",
            "CA",
            "94618",
            null
        );

        assertThrows(ConstraintViolationException.class, () -> {
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }

    // create author with invalid zip
    @Test
    void createAuthor_WithInvalidZip_ShouldThrowJpaSystemException() {

        Author invalidAuthor = new Author(
            "111-11-1111", 
            "Smith",
            "Alice",
            "415 123-8585",
            "Street 1",
            "Oakland",
            "CA",
            "11",
            1
        );

        assertThrows(ConstraintViolationException.class, () -> {
            authorRepository.saveAndFlush(invalidAuthor);
        });
    }




    // create author with valid zipcode 
    // @Test
    // void createAuthor_WithDuplicateId_ShouldThrowDataIntegrityViolationException() {

    //     // This ID already exists from @BeforeEach
    //     Author duplicateAuthor = new Author(
    //         "123-45-6789", 
    //         "Smith",
    //         "Alice",
    //         "415 123-8585",
    //         "Street 1",
    //         "Oakland",
    //         "CA",
    //         "94618",
    //         1
    //     );

    //     DataIntegrityViolationException exception =
    //         assertThrows(DataIntegrityViolationException.class, () -> {
    //             authorRepository.saveAndFlush(duplicateAuthor);
    //         });

    //     assertNotNull(exception);
    // }

}