package com.capgemini.book_partner_portal.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.entity.Title;
import com.capgemini.book_partner_portal.repository.PublisherRepository;
import com.capgemini.book_partner_portal.repository.TitleRepository;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TitleApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TitleRepository titleRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @BeforeEach
    void initData() {
        // 1. Cleanup old data to avoid Primary Key conflicts
        titleRepository.deleteAll();

        // 2. Setup Publisher 
        Publisher pub = new Publisher("1389", "Algodata Infosystems", "Berkeley", "CA", "USA",true);
        publisherRepository.save(pub);

        // 3. Setup Base Title (Matching your new Repository Test data)
        Title book = new Title();
        book.setTitleId("BU1332");
        book.setTitle("The Good Book");
        book.setPublisher(pub); 
        book.setType("philosophy");
        book.setPrice(19.99);
        book.setRoyalty(10);
        book.setPubdate(LocalDateTime.now());
        book.setIsActive(true);
        titleRepository.save(book);
    }

    @Test
    @DisplayName("API: Fetch All Titles")
    void testFetchMasterBookList() throws Exception {
        mockMvc.perform(get("/api/titles"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.titles", hasSize(1)));
    }

    @Test
    @DisplayName("API: Fetch Title by ID BU1332")
    void testFetchTitleById() throws Exception {
        String id = "BU1332";
        mockMvc.perform(get("/api/titles/" + id)
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("The Good Book")))
                .andExpect(jsonPath("$.type", is("philosophy")));
    }

    @Test
    @DisplayName("API: Search by Exact Title")
    void testFindByTitle() throws Exception {
        mockMvc.perform(get("/api/titles/search/findByTitle")
                .param("title", "The Good Book") 
                .accept("application/hal+json")) 
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.title", is("The Good Book")));
    }

    @Test
    @DisplayName("API: Search by Type (philosophy)")
    void testFindByType() throws Exception { 
        mockMvc.perform(get("/api/titles/search/findByTypeIgnoreCase")
                .param("type", "PHILOSOPHY") 
                .accept("application/hal+json")) 
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$._embedded.titles[0].type", is("philosophy")));
    }

    @Test
    @DisplayName("API: Search Price Greater Than")
    void testFindByPriceGreaterThan() throws Exception {
       mockMvc.perform(get("/api/titles/search/findByPriceGreaterThan")
               .param("price", "15.00") // Matches @Param("price") in Repo
               .accept("application/hal+json"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$._embedded.titles").exists());
    }

    @Test
    @DisplayName("API: Search Price Between")
    void testFindByPriceBetween() throws Exception {
        mockMvc.perform(get("/api/titles/search/findByPriceBetween")
                .param("min", "10.0")
                .param("max", "30.0")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.titles", hasSize(1)));
    }

    @Test
    @DisplayName("API: POST New Book")
    void testAddNewBook() throws Exception {
        String id = "PC9999";
        String newBookJson = "{" +
                "\"titleId\": \"" + id + "\"," +
                "\"title\": \"Post Method Guide\"," + 
                "\"type\": \"business\"," +
                "\"publisher\": \"/api/publishers/1389\"," +
                "\"price\": 50.00," +
                "\"pubdate\": \"2026-03-27T10:00:00\"," +
                "\"active\": true" +
                "}";

        mockMvc.perform(post("/api/titles") 
                .contentType(MediaType.APPLICATION_JSON)
                .content(newBookJson))
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.title", is("Post Method Guide")));

        assertThat(titleRepository.existsById(id)).isTrue();
    }  

    @Test
    @DisplayName("API: PATCH Update Price")
    void testUpdateTitlePartial() throws Exception {
        String id = "BU1332";
        String patchJson = "{\"price\": 88.88}";

        mockMvc.perform(patch("/api/titles/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price", is(88.88)));
    }

    @Test
    @DisplayName("API: Soft Delete (Verify 404 after Delete)")
    void testSoftDeleteApi() throws Exception {
        String id = "BU1332";

        // 1. Perform Delete
        mockMvc.perform(delete("/api/titles/" + id))
                .andExpect(status().isNoContent());

        // 2. Try to Get - Should be 404 because @Where(is_active=true) filters it out
        mockMvc.perform(get("/api/titles/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Security: Prevent Duplicate ID (409 Conflict)")
    void testCreateDuplicateTitle_ShouldFail() throws Exception {
        // "BU1332" already exists from @BeforeEach
        String duplicateJson = "{" +
                "\"titleId\": \"BU1332\"," +
                "\"title\": \"Hacker Book\"," +
                "\"publisher\": \"/api/publishers/1389\"," +
                "\"active\": true" +
                "}";

        mockMvc.perform(post("/api/titles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateJson))
                .andExpect(status().isConflict()); // Validates TitleEventHandler bouncer
    }

    @Test
    @DisplayName("Security: Prevent Phantom Insert via PUT (404 Not Found)")
    void testPutNewTitle_ShouldFail() throws Exception {
        String nonExistentId = "NEW999";
        String phantomJson = "{" +
                "\"titleId\": \"" + nonExistentId + "\"," +
                "\"title\": \"Phantom Book\"," +
                "\"publisher\": \"/api/publishers/1389\"" +
                "}";

        mockMvc.perform(put("/api/titles/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(phantomJson))
                .andExpect(status().isNotFound()); 
    }
}