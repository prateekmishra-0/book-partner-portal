package com.capgemini.book_partner_portal.api;

import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.repository.PublisherRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
// For get(), post(), put(), delete()
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

// For status(), content(), jsonPath(), header()
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// For print() - optional but very helpful for debugging
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc // virtual tomcat or stimulate Tomcat
@Transactional // it will rollback after running tests
public class PublisherApiTest {

    @Autowired
    private MockMvc mockMvc;

    private Publisher testPublisher;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUpTestPublisher() {
        testPublisher = Publisher.builder()
                .pubId("9994")
                .pubName("sujal nimje")
                .city("nagpur")
                .state("MH")
                .country("India").build();

        publisherRepository.save(testPublisher);
    }


    @Test
    void shouldReturnPublisherList() throws Exception {
        mockMvc.perform(get("/api/publishers"))
                .andDo(print())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.publishers").exists());
    }

    // success when name of publisher is there
    @Test
    void shouldReturnPublisherByName() throws Exception {
        mockMvc.perform(get("/api/publishers/search/exact-pubname") // Added /search/
                        .param("pubName", testPublisher.getPubName()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                // If Optional<Publisher> returns the object directly:
                .andExpect(jsonPath("$.pubName").value(testPublisher.getPubName()));
    }

    // not found because the name not present in publisher
    @Test
    void shouldReturnNotFoundByName() throws Exception {
        mockMvc.perform(get("/api/publishers/search/exact-pubname").param("pubName", "random"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    // searching by pubName
    @Test
    void shouldReturnPublisherListByPubName() throws Exception {
        mockMvc.perform(get("/api/publishers/search/pubname").param("pubName", testPublisher.getPubName()))
                .andDo(print())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect((jsonPath("$._embedded.publishers").exists()))
                .andExpect(jsonPath("$._embedded.publishers[*].pubName").value(testPublisher.getPubName()));
    }

    // city does not exists
    @Test
    void shouldReturnPublisherListByPubNameEmpty() throws Exception {
        mockMvc.perform(get("/api/publishers/search/pubname").param("pubName", "pune"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.publishers").isEmpty());
    }

    // searching by city
    @Test
    void shouldReturnPublisherListByCity() throws Exception {
        mockMvc.perform(get("/api/publishers/search/city").param("city", testPublisher.getCity()))
                .andDo(print())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect((jsonPath("$._embedded.publishers").exists()))
                .andExpect(jsonPath("$._embedded.publishers[*].city").value(testPublisher.getCity()));
    }

    // city does not exists
    @Test
    void shouldReturnPublisherListByCityEmpty() throws Exception {
        mockMvc.perform(get("/api/publishers/search/city").param("city", "pune"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.publishers").isEmpty());
    }

    // searching by State
    @Test
    void shouldReturnPublisherListByState() throws Exception {
        mockMvc.perform(get("/api/publishers/search/state").param("state", testPublisher.getState()))
                .andDo(print())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect((jsonPath("$._embedded.publishers").exists()))
                .andExpect(jsonPath("$._embedded.publishers[*].state").value(testPublisher.getState()));
    }

    // state does not exists
    @Test
    void shouldReturnPublisherListByStateEmpty() throws Exception {
        mockMvc.perform(get("/api/publishers/search/state").param("state", "VJ"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.publishers").isEmpty());
    }

    // searching by country
    @Test
    void shouldReturnPublisherListByCountry() throws Exception {
        mockMvc.perform(get("/api/publishers/search/country").param("country", testPublisher.getCountry()))
                .andDo(print())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect((jsonPath("$._embedded.publishers").exists()))
                .andExpect(jsonPath("$._embedded.publishers[*].country").value(testPublisher.getCountry()));
    }

    // state does not exists
    @Test
    void shouldReturnEmptyPublisherListByCountry() throws Exception {
        mockMvc.perform(get("/api/publishers/search/country").param("country", "random"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.publishers").isEmpty());
    }

    @Test
    void shouldReturnConflictWhenIdAlreadyExists() throws Exception {
        // 1. Setup: Ensure the publisher already exists (or mock it)
        String publisherJson = objectMapper.writeValueAsString(testPublisher);

        // 2. Action: POST the same entity again
        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publisherJson))
                .andExpect(status().isConflict()); // Or .isBadRequest() depending on your logic
    }

    @Test
    void shouldUpdateExistingPublisher() throws Exception {
        // 1. Modify the object
        testPublisher.setPubName("Updated Name");
        String updatedJson = objectMapper.writeValueAsString(testPublisher);

        // 2. Action: PUT request to update
        mockMvc.perform(put("/api/publishers/" + testPublisher.getPubId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pubName").value("Updated Name"));
    }

    @Test
    void shouldUpdateOnlyPubNameUsingPatch() throws Exception {
        String id = testPublisher.getPubId();

        // We only create a map or a partial object with the name
        Map<String, Object> updates = new HashMap<>();
        updates.put("pubName", "New Shiny Name");

        String json = objectMapper.writeValueAsString(updates);

        mockMvc.perform(patch("/api/publishers/" + id) // Use patch()
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pubName").value("New Shiny Name"))
                // Ensure other fields (like city) remained the same
                .andExpect(jsonPath("$.city").exists());
    }

    /**
     * TEST: Validation - Invalid pub_id format
     * Constraints: ^(1389|0736|0877|1622|1756|99\d{2})$
     * Verifies that the API rejects IDs that don't match the specific business pattern.
     */
    @Test
    void shouldReturnBadRequestWhenIdDoesNotMatchRegex() throws Exception {
        Publisher invalidIdPublisher = Publisher.builder()
                .pubId("1234") // Invalid ID per @Pattern
                .pubName("Test Publisher")
                .state("NY")
                .build();

        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidIdPublisher)))
                .andExpect(status().isBadRequest());
    }

    /**
     * TEST: Validation - State code length
     * Constraint: @Size(min = 2, max = 2)
     * Verifies that the API rejects state codes longer than 2 characters.
     */
    @Test
    void shouldReturnBadRequestWhenStateCodeIsInvalid() throws Exception {
        Publisher invalidStatePublisher = Publisher.builder()
                .pubId("9955")
                .pubName("Test Publisher")
                .state("NYK") // 3 chars - Invalid
                .build();

        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidStatePublisher)))
                .andExpect(status().isBadRequest());
    }

    /**
     * TEST: Validation - Required field missing
     * Constraint: @NotBlank(message = "publisher name cannot be empty")
     * Verifies that the API rejects requests where the publisher name is null or empty.
     */
    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        Publisher blankNamePublisher = Publisher.builder()
                .pubId("9966")
                .pubName("") // Blank - Invalid
                .state("CA")
                .build();

        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(blankNamePublisher)))
                .andExpect(status().isBadRequest());
    }

    /**
     * TEST: Update - Resource Not Found
     * Verifies that attempting to update a non-existent ID returns 404.
     */

    // TODO : configuration should be done so that if the record is not exist put returns error
    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentId() throws Exception {
        String nonExistentId = "9900"; // Ensure this isn't in your setup

        Publisher updateData = Publisher.builder()
                .pubId(nonExistentId)
                .pubName("New Name")
                .build();

        mockMvc.perform(put("/api/publishers/" + nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());
    }

    /**
     * TEST: Logic - Default Country assignment
     * Verifies that the entity's default value "USA" is preserved when not provided in JSON.
     */
    @Test
    void shouldSaveWithDefaultCountryWhenNotProvided() throws Exception {
        Publisher publisher = Publisher.builder()
                .pubId("9977")
                .pubName("International Books")
                .state("WA")
                // country is not set here
                .build();

        mockMvc.perform(post("/api/publishers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(publisher)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.country").value("USA"));
    }


    /**
     * API TEST: Find All Pagination (HAL-JSON)
     * Verifies that the REST endpoint returns the correct HATEOAS structure and page info.
     */
    @Test
    void shouldReturnHalPagingMetadataForFindAll() throws Exception {
        // 1. Action: Perform GET request with page parameters
        mockMvc.perform(get("/api/publishers")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "pubId,asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                // 2. Verify Data: Check if the list is inside the '_embedded' field
                .andExpect(jsonPath("$._embedded.publishers").isArray())
                .andExpect(jsonPath("$._embedded.publishers.length()").value(2))

                // 3. Verify Metadata: Check the 'page' object
                .andExpect(jsonPath("$.page.size").value(2))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").exists())

                // 4. Verify HATEOAS: Check for navigation links
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.next").exists()) // Only if total records > 2
                .andExpect(jsonPath("$._links.last").exists());
    }

    /**
     * TEST: Delete Publisher by ID
     * Verifies that a DELETE request removes the resource and returns 204 No Content.
     */
    @Test
    void shouldDeletePublisherById() throws Exception {
        // 1. Get the ID of the publisher created in @BeforeEach
        String id = testPublisher.getPubId();

        // 2. Action: Perform the DELETE request
        mockMvc.perform(delete("/api/publishers/" + id))
                .andExpect(status().isNoContent()); // Spring Data REST returns 204

        // 3. Verification: Ensure the resource is actually gone
        mockMvc.perform(get("/api/publishers/" + id))
                .andExpect(status().isNotFound()); // Should return 404 now
    }

    @Test
    void shouldHideActiveFieldWhenUsingProjection() throws Exception {
        mockMvc.perform(get("/api/publishers/" + testPublisher.getPubId())
                        .param("projection", "publisherSummary")) // Force the projection
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pubName").exists())
                .andExpect(jsonPath("$.active").doesNotExist()) // Verify it's hidden
                .andExpect(jsonPath("$.isActive").doesNotExist());
    }
}
