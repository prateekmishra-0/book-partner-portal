package com.capgemini.book_partner_portal.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.capgemini.book_partner_portal.entity.Author;
import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.entity.Title;
import com.capgemini.book_partner_portal.entity.TitleAuthor;
import com.capgemini.book_partner_portal.entity.TitleAuthorId;
import com.capgemini.book_partner_portal.repository.AuthorRepository;
import com.capgemini.book_partner_portal.repository.PublisherRepository;
import com.capgemini.book_partner_portal.repository.TitleAuthorRepository;
import com.capgemini.book_partner_portal.repository.TitleRepository;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
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
    private AuthorRepository authorRepository;

    @Autowired
    private TitleAuthorRepository titleAuthorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @BeforeEach
    void setupRelationships() {
        // 1. Create Publisher
        Publisher pub = new Publisher("1389", "Algodata", "Berkeley", "CA", "USA", true);
        publisherRepository.save(pub);

        // 2. Create Title
        Title title = new Title();
        title.setTitleId("BU1332");
        title.setTitle("The Good Book");
        title.setType("philosophy");
        title.setPrice(25.00);
        title.setPubId("1389");
        title.setPubdate(LocalDateTime.now());
        title.setIsActive(true);
        title.setPublisher(pub);
        titleRepository.save(title);

        // 3. Create Authors
        Author a1 = Author.builder().auId("111-11-1111").firstName("Alice").lastName("Alpha").contract(1).isActive(true).build();
        Author a2 = Author.builder().auId("222-22-2222").firstName("Bob").lastName("Beta").contract(1).isActive(true).build();
        authorRepository.saveAll(List.of(a1, a2));

        // 4. Link Authors to Title (The "Selection" result)
        titleAuthorRepository.save(new TitleAuthor(new TitleAuthorId("111-11-1111", "BU1332"), a1, title, (byte) 1, 50));
        titleAuthorRepository.save(new TitleAuthor(new TitleAuthorId("222-22-2222", "BU1332"), a2, title, (byte) 2, 50));
    }

    @Test
    @DisplayName("API: Fetch All Titles (Paginated)")
    void testFetchMasterBookList() throws Exception {
        mockMvc.perform(get("/api/titles")
                .param("size", "5")
                .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.titles", hasSize(greaterThanOrEqualTo(1))))
                // Verify pagination metadata exists
                .andExpect(jsonPath("$.page.size", is(5)))
                .andExpect(jsonPath("$.page.totalElements").exists());
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
    @DisplayName("API: GET Authors by Title (HATEOAS Search)")
    void testApiGetAuthorsByTitle() throws Exception {
        // This hits the specific search path defined in your TitleAuthorRepository
        mockMvc.perform(get("/api/titleAuthors/search/byTitle")
                .param("titleId", "BU1332")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.titleAuthors", hasSize(2)))
                // Verify the author details are visible in the response
                .andExpect(jsonPath("$._embedded.titleAuthors[0].royaltyPer").exists());
    }

    @Test
    @DisplayName("API: Search by Exact Title")
    void testFindByTitle() throws Exception {
        mockMvc.perform(get("/api/titles/search/exact")
                .param("title", "The Good Book") 
                .accept("application/hal+json")) 
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.title", is("The Good Book")));
    }

    @Test
    @DisplayName("API: Search by Type (philosophy) - Paginated")
    void testFindByType() throws Exception { 
        mockMvc.perform(get("/api/titles/search/type")
                .param("type", "philosophy") 
                .accept("application/hal+json")) 
                .andExpect(status().isOk()) 
                // Results are inside _embedded when paginated
                .andExpect(jsonPath("$._embedded.titles[0].type", is("philosophy")))
                .andExpect(jsonPath("$.page").exists());
    }

   @Test
    @DisplayName("API: Search Price Greater Than - Paginated")
    void testFindByPriceGreaterThan() throws Exception {
       mockMvc.perform(get("/api/titles/search/price-gt")
               .param("price", "15.00")
               .accept("application/hal+json"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$._embedded.titles").exists())
               .andExpect(jsonPath("$.page.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("API: Search Price Between")
    void testFindByPriceBetween() throws Exception {
        mockMvc.perform(get("/api/titles/search/price-range")
                .param("min", "10.0")
                .param("max", "30.0")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.titles", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("API: POST New Book")
    void testAddNewBook() throws Exception {
        String id = "PC9999";
        String newBookJson = """
            {
                "titleId": "PC9999",
                "title": "Post Method Guide",
                "type": "business",
                "publisher": "/api/publishers/1389",
                "price": 50.00,
                "pubdate": "2026-03-27T10:00:00",
                "isActive": true
            }
            """;

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

    @Test
    @DisplayName("Security: POST with isActive=false should be ignored")
    void insertTitle_WithIsActiveFalse_ShouldIgnoreAndSetTrue() throws Exception {
        String id = "PC8088";
        // Hacker tries to create a book that is already "deleted" (soft-delete hack)
        String maliciousJson = "{" +
                "\"titleId\": \"" + id + "\"," +
                "\"title\": \"Hacker's Manual\"," +
                "\"publisher\": \"/api/publishers/1389\"," +
                "\"isActive\": false" + 
                "}";
    
        mockMvc.perform(post("/api/titles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(maliciousJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Hacker's Manual")));
    
        // Verify in DB: The record must be active despite the hacker's JSON
        Optional<Title> savedBook = titleRepository.findById(id);
        assertThat(savedBook).isPresent();
        assertThat(savedBook.get().getIsActive()).isTrue(); // The Shield Worked!
    }

    @Test
    @DisplayName("API: Reject negative values for financial fields")
    void testRejectNegativeFinancials() throws Exception {
        String invalidPayload = """
            {
                "titleId": "FAIL01",
                "title": "Invalid Book",
                "type": "business",
                "price": -10.00,
                "advance": -500.0,
                "royalty": -5,
                "ytdSales": -100,
                "pubId": "1389"
            }
            """;

        mockMvc.perform(post("/api/titles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("API: POST with existing titleId returns 409 Conflict")
    void testPostExistingIdConflict() throws Exception {
        // "BU1332" is created in @BeforeEach
        String duplicatePayload = """
            {
                "titleId": "BU1332",
                "title": "Duplicate Title",
                "type": "philosophy",
                "pubId": "1389"
            }
            """;

        mockMvc.perform(post("/api/titles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicatePayload))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("API: PUT to non-existent ID returns 404 Not Found")
    void testPutGhostIdNotFound() throws Exception {
        String payload = """
            {
                "title": "Ghost Update",
                "type": "tech",
                "pubId": "1389"
            }
            """;

        mockMvc.perform(put("/api/titles/GHOST99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isNotFound());
    }

   @Test
    @DisplayName("Security: POST with isActive=false should be forced to true")
    void testIsActiveShield() throws Exception {
        String id = "SHLD01";
        String payload = """
            {
                "titleId": "SHLD01",
                "title": "Protected Book",
                "type": "tech",
                "isActive": false,
                "publisher": "/api/publishers/1389"
            }
            """;

        mockMvc.perform(post("/api/titles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated());

        // Check if EventHandler forced it to true
        Optional<Title> saved = titleRepository.findById(id);
        assertThat(saved.get().getIsActive()).isTrue();
    }

    @Test
    @DisplayName("API: Projection hides internal fields and flattens publisher")
    void testProjectionSecurity() throws Exception {
        mockMvc.perform(get("/api/titles/BU1332")
                .param("projection", "titleSummary")
                .accept("application/hal+json")) // Force HAL+JSON
                .andExpect(status().isOk())
                .andDo(print())
                // 1. These should now be HIDDEN (they are in Entity but NOT in Projection)
                .andExpect(jsonPath("$.pubId").doesNotExist())
                .andExpect(jsonPath("$.isActive").doesNotExist())
                
                // 2. This should be FLATTENED (from our @Value annotation)
                .andExpect(jsonPath("$.publisherName").value("Algodata"))
                .andExpect(jsonPath("$.publisherCity").exists())
                
                // 3. Original fields that ARE in the projection
                .andExpect(jsonPath("$.title").value("The Good Book"));
    }

    @Test
    @DisplayName("API: Search Authors by Title via Bridge Entity")
    void testBridgeRoutingByTitle() throws Exception {
        // Assumes setup code has linked an author to "BU1332"
        mockMvc.perform(get("/api/titleAuthors/search/byTitle")
                .param("titleId", "BU1332")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.titleAuthors").isArray())
                // Verify link to Title exists in response
                .andExpect(jsonPath("$._embedded.titleAuthors[0]._links.title").exists());
    }

    @Test
    @DisplayName("API: Composite Key correctly addresses resource")
    void testCompositeKeyAddressing() throws Exception {
        // Default Spring Data REST format is auId,titleId (COMMA)
        // If your TitleAuthorIdConverter uses underscore, keep it. 
        // Otherwise, change to ","
        String compositeId = "111-11-1111_BU1332"; 
        
        mockMvc.perform(get("/api/titleAuthors/" + compositeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.royaltyPer").exists())
                .andExpect(jsonPath("$._links.author").exists());
    }


}