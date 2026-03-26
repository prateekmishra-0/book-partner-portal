package com.capgemini.book_partner_portal.api;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.everyItem;
import org.junit.jupiter.api.BeforeEach;
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



    // ---------------------------------- GET APIs ----------------------------------------------

    @Test
    void getAllAuthors_WhenAuthorsExist_ShouldReturnNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").isNotEmpty());
    }

    @Test
    void getAuthorById_WithValidId_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/authors/123-45-6789"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getAuthorById_WithInvalidId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/authors/999-99-9999"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }



    @Test
    void getAuthorsByFirstName_WhenFirstNameExists_ShouldReturnNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors/search/firstname")
            .param("firstName", "John"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").exists())
            .andExpect(jsonPath("$._embedded.authors").isNotEmpty())
            .andExpect(jsonPath("$._embedded.authors[*].firstName")
            .value(everyItem(containsStringIgnoringCase("John"))));
    }

    @Test
    void getAuthorsByFirstName_WhenFirstNameNotExists_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors/search/firstname")
            .param("firstName", "Ramu"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").exists())
            .andExpect(jsonPath("$._embedded.authors").isEmpty());
    }

    @Test
    void getAuthorsByLastName_WhenLastNameExists_ShouldReturnNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors/search/lastname")
            .param("lastName", "Doe"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").exists())
            .andExpect(jsonPath("$._embedded.authors").isNotEmpty())
            .andExpect(jsonPath("$._embedded.authors[*].lastName")
            .value(everyItem(containsStringIgnoringCase("Doe"))));
    }

    @Test
    void getAuthorsByLastName_WhenLastNameNotExists_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors/search/lastname")
            .param("lastName", "Sharma"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").exists())
            .andExpect(jsonPath("$._embedded.authors").isEmpty());
    }

    @Test
    void getAuthorsByCity_WhenCityExists_ShouldReturnNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors/search/city")
            .param("city", "Berkeley"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").exists())
            .andExpect(jsonPath("$._embedded.authors").isNotEmpty())
            .andExpect(jsonPath("$._embedded.authors[*].city")
            .value(everyItem(containsStringIgnoringCase("Berkeley"))));
    }

    @Test
    void getAuthorsByCity_WhenCityNotExists_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors/search/city")
            .param("city", "Gondia"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").exists())
            .andExpect(jsonPath("$._embedded.authors").isEmpty());
    }




}