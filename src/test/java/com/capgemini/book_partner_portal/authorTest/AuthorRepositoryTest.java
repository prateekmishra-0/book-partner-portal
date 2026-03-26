package com.capgemini.book_partner_portal.authorTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.capgemini.book_partner_portal.entity.Author;
import com.capgemini.book_partner_portal.repository.AuthorRepository;

import jakarta.transaction.Transactional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class AuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @Test
    void testGetAllAuthors(){
        List<Author> authors = authorRepository.findAll();
        assertThat(authors).isNotEmpty(); 
    }

    @Test
    void testGetAuthorByValidId() {
        Author testAuthor = new Author(
            "123-45-6789",
            "Doe",
            "John",
            "1234567890",
            "Street 1",
            "NYC",
            "NY",
            "10001",
            1
        );

        authorRepository.save(testAuthor);

        Author author = authorRepository.findById("123-45-6789").orElse(null);

        assertThat(author).isNotNull();
    }
    
    @Test
    void testGetAuthorByInvalidId(){
        Author author = authorRepository.findById("999-99-9999").orElse(null);
        assertThat(author).isNull();
    }
    
}
