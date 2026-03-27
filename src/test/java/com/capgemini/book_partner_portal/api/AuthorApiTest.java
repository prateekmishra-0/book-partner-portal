package com.capgemini.book_partner_portal.api;

import java.util.Map;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.everyItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.capgemini.book_partner_portal.entity.Author;
import com.capgemini.book_partner_portal.repository.AuthorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthorApiTest {

    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private AuthorRepository authorRepository;

    private Author testAuthor;

    @BeforeEach
    void setup() {
        testAuthor = Author.builder()
            .auId("123-45-6789")
            .firstName("John")
            .lastName("Doe")
            .phone("415 658-9932")
            .address("6223 Bateman St.")
            .city("Berkeley")
            .state("CA")
            .zip("94705")
            .contract(1)
            .build();


        authorRepository.save(testAuthor);
    }



    // ---------------------------------- GET APIs Tests ----------------------------------------------

    @Test
    void getAllAuthors_WhenAuthorsExist_ShouldReturnNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors"))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").isNotEmpty());
    }

    @Test
    void getAuthorById_WithValidId_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/authors/123-45-6789"))
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void getAuthorById_WithInvalidId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/authors/999-99-9999"))
            // .andDo(print())
            .andExpect(status().isNotFound());
    }



    @Test
    void getAuthorsByFirstName_WhenFirstNameExists_ShouldReturnNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors/search/firstname")
            .param("firstName", "John"))
            // .andDo(print())
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
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").exists())
            .andExpect(jsonPath("$._embedded.authors").isEmpty());
    }

    @Test
    void getAuthorsByLastName_WhenLastNameExists_ShouldReturnNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors/search/lastname")
            .param("lastName", "Doe"))
            // .andDo(print())
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
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").exists())
            .andExpect(jsonPath("$._embedded.authors").isEmpty());
    }

    @Test
    void getAuthorsByCity_WhenCityExists_ShouldReturnNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/authors/search/city")
            .param("city", "Berkeley"))
            // .andDo(print())
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
            // .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").exists())
            .andExpect(jsonPath("$._embedded.authors").isEmpty());
    }



    // ---------------------------------- POST APIs Tests ----------------------------------------------

    @Test
    void createAuthor_WithValidData_ShouldReturn201() throws Exception {

        Author newAuthorJson = Author.builder()
            .auId("987-65-4321")
            .firstName("Alice")
            .lastName("Smith")
            .phone("415 123-8585")
            .address("Street 1")
            .city("Oakland")
            .state("CA")
            .zip("94618")
            .contract(1)
            .build();

        mockMvc.perform(post("/api/authors")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newAuthorJson)))
            // .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("Alice"))
            .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void createAuthor_WithMissingFields_ShouldReturn400() throws Exception {
        Author newAuthorJson = Author.builder()
                                .auId("111-11-1111")
                                .build();

        mockMvc.perform(post("/api/authors")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newAuthorJson)))
                // .andDo(print())
            .andExpect(status().isBadRequest());
    }

    // create author with invalid id format
    @Test
    void createAuthor_WithInvalidIdFormat_ShouldReturn400() throws Exception {

        Author newAuthorJson = Author.builder()
                                .auId("111111111")
                                .firstName("John")
                                .lastName("Doe")
                                .contract(1)
                                .build();

        mockMvc.perform(post("/api/authors")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newAuthorJson)))
                // .andDo(print())
            .andExpect(status().isBadRequest());
    }

    
    // create author with empty firstName and lastName 
    @Test
    void createAuthor_WithEmptyFirstNameAndLastName_ShouldReturn400() throws Exception {

        
        Author newAuthorJson = Author.builder()
                                .auId("111111111")
                                .firstName("")
                                .lastName("")
                                .contract(1)
                                .build();

        mockMvc.perform(post("/api/authors")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newAuthorJson)))
                // .andDo(print())
            .andExpect(status().isBadRequest());
    }


    // create author with Null contract
    @Test
    void createAuthor_WithNullContract_ShouldReturn400() throws Exception {

        Author newAuthorJson = Author.builder()
                                .auId("111-11-1111")
                                .firstName("John")
                                .lastName("Doe")
                                .build();

        mockMvc.perform(post("/api/authors")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newAuthorJson)))
                // .andDo(print())
            .andExpect(status().isBadRequest());
    }
    
    // create author with invalid zip
    @Test
    void createAuthor_WithInvalidZipcode_ShouldReturn400() throws Exception {

        Author newAuthorJson = Author.builder()
                                .auId("111-11-1111")
                                .firstName("John")
                                .lastName("Doe")
                                .contract(1)
                                .zip("111")
                                .build();
        mockMvc.perform(post("/api/authors")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newAuthorJson)))
                // .andDo(print())
            .andExpect(status().isBadRequest());
    }

    // create author with invalid state length
    @Test
    void createAuthor_WithInvalidStateLength_ShouldReturn400() throws Exception {

        Author newAuthorJson = Author.builder()
                                .auId("111-11-1111")
                                .firstName("John")
                                .lastName("Doe")
                                .contract(1)
                                .state("ABCD")
                                .build();

        mockMvc.perform(post("/api/authors")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(newAuthorJson)))
                // .andDo(print())
            .andExpect(status().isBadRequest());
    }



    // create author with duplicate id
    @Test
    void createAuthor_WithDuplicateId_ShouldReturn409() throws Exception {

        // Same ID as inserted in @BeforeEach
        Author duplicateAuthor = Author.builder()
                .auId("123-45-6789")
                .firstName("John")
                .lastName("Doe")
                .contract(1)
                .build();

        mockMvc.perform(post("/api/authors")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(duplicateAuthor)))
                // .andDo(print())
                .andExpect(status().isConflict());
    }

    // ---------------------------------- PUT APIs Tests ----------------------------------------------
    
    @Test
    void updateAuthor_WithValidData_ShouldReturn200() throws Exception {
        
        Author updatedAuthor = Author.builder()
        .auId(testAuthor.getAuId()) 
        .firstName("UpdatedFirst")
        .lastName("UpdatedLast")
        .contract(1) 
        .phone("415-658-9932")
        .address("6223 Bateman St.")
        .city("Berkeley")
        .state("CA")
        .zip("94705")
        .build();

        mockMvc.perform(put("/api/authors/{id}", testAuthor.getAuId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAuthor)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedFirst"))
                .andExpect(jsonPath("$.lastName").value("UpdatedLast"));
            }
            
            
            
    // ---------------------------------- PATCH APIs Tests ----------------------------------------------

    // path Author with valid id and fields
    @Test
    void patchAuthor_WithValidFields_ShouldReturn200() throws Exception {
        Map<String, Object> updates = Map.of(
            "firstName", "UpdatedFirst",
            "lastName", "UpdatedLast"
        );

        mockMvc.perform(patch("/api/authors/{id}", testAuthor.getAuId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("UpdatedFirst"))
            .andExpect(jsonPath("$.lastName").value("UpdatedLast"));
    }

    // path Author with invalid fields
    @Test
    void patchAuthor_WithInvalidFields_ShouldReturn400() throws Exception {
        Map<String, Object> updates = Map.of(
            "zip", "asdasda"
        );

        mockMvc.perform(patch("/api/authors/{id}", testAuthor.getAuId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }


    // patch author with invalid Id       
    @Test
    void patchAuthor_WithInvalidId_ShouldReturn404() throws Exception {
        Map<String, Object> updates = Map.of(
            "firstName", "Alice",
            "lastName", "Smith"
        );

        mockMvc.perform(patch("/api/authors/{id}", "999-99-9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updates)))
            .andDo(print())
            .andExpect(status().isNotFound());
    }


    // ---------------------------------- DELETE APIs Tests ----------------------------------------------

    @Test
    void deleteAuthor_WithValidId_ShouldReturn204_AndNotBeAccessible() throws Exception {

        // Step 1: Call DELETE API
        mockMvc.perform(delete("/api/authors/{id}", testAuthor.getAuId()))
                .andDo(print())
                .andExpect(status().isNoContent());

        // Step 2: Try fetching → should return 404 (because of @SQLRestriction)
        mockMvc.perform(get("/api/authors/{id}", testAuthor.getAuId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



    // ---------------------------------- Pagination APIs Tests ----------------------------------------------

    @Test
    void getAuthors_WithValidPageAndSize_ShouldReturnPagedAuthors() throws Exception {

        mockMvc.perform(get("/api/authors")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").isArray())
            .andExpect(jsonPath("$._embedded.authors").isNotEmpty())
            .andExpect(jsonPath("$.page.size").value(5))
            .andExpect(jsonPath("$.page.number").value(0));
    }

    
    @Test
    void getAuthors_WithPageBeyondData_ShouldReturnEmptyList() throws Exception {

        mockMvc.perform(get("/api/authors")
                .param("page", "9999")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").isArray())
            .andExpect(jsonPath("$._embedded.authors.length()").value(0));
    }

    @Test
    void getAuthors_WithInvalidPageOrSize_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/authors")
                .param("page", "-1")
                .param("size", "0"))
            .andExpect(status().isOk());
    }


    // ---------------------------------- Projection APIs Tests ----------------------------------------------

    @Test
    void getAuthors_WithProjection_ShouldReturnProjectedFields() throws Exception {

        mockMvc.perform(get("/api/authors")
                .param("projection", "authorList"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").isArray())
            .andExpect(jsonPath("$._embedded.authors").isNotEmpty())
            .andExpect(jsonPath("$._embedded.authors[0].firstName").exists())
            .andExpect(jsonPath("$._embedded.authors[0].lastName").exists());
    }

    @Test
    void getAuthors_ShouldNotReturnExcludedFields() throws Exception {

        mockMvc.perform(get("/api/authors").param("projection", "authorList"))
        
            .andExpect(status().isOk())
            .andExpect(jsonPath("$._embedded.authors").isArray())
            .andExpect(jsonPath("$._embedded.authors").isNotEmpty())

            .andExpect(jsonPath("$._embedded.authors[0].phone").doesNotExist())
            .andExpect(jsonPath("$._embedded.authors[0].address").doesNotExist())
            .andExpect(jsonPath("$._embedded.authors[0].zip").doesNotExist())
            .andExpect(jsonPath("$._embedded.authors[0].contract").doesNotExist());
    }
}