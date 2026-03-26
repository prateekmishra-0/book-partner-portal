package com.capgemini.book_partner_portal.authorTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.capgemini.book_partner_portal.entity.Author;
import com.capgemini.book_partner_portal.repository.AuthorRepository;

import jakarta.transaction.Transactional;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthorApiTest {
    
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private AuthorRepository authorRepository;
    
    // Get all authors
    @Test
    void testGetAllAuthorsAPI() throws Exception {

    mockMvc.perform(get("/api/authors"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$._embedded.authors").isNotEmpty());
    }
    

    // Get author with valid id
    @Test
    void testGetAuthorByValidIdAPI() throws Exception {

        // Arrange: create data in DB
        Author author = new Author(
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

        authorRepository.save(author);

        mockMvc.perform(get("/api/authors/123-45-6789"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    // Get author with invalid id
    @Test
    void testGetAuthorByInvalidIdAPI() throws Exception {

        // Attempt to get an ID that does NOT exist
        mockMvc.perform(get("/api/authors/999-99-9999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

}
