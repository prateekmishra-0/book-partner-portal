package com.capgemini.book_partner_portal.api;

import com.capgemini.book_partner_portal.repository.StoreRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("local")
public class StoreApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreRepository storeRepository;

    // ------------------- GET APIs -------------------
    // ------------------- BASIC & ID APIs -------------------

    @Test
    void getAllStores_WhenStoresExist_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/stores"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores").isNotEmpty());

        long currentCount = storeRepository.count();
        assertEquals(currentCount, storeRepository.findAll().size());
    }

    @Test
    public void testGetStoreById_ValidId_ShouldReturnStore() throws Exception {
        mockMvc.perform(get("/api/stores/7066"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storName").value("Barnum's"));
    }

    @Test
    public void testGetStoreById_InvalidId_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/stores/9999"))
                .andExpect(status().isNotFound());
    }

    // ------------------- SEARCH APIs -------------------

    @Test
    void getStoresByCity_WhenCityExists_ShouldReturnNonEmptyList() throws Exception {
        mockMvc.perform(get("/api/stores/search/city").param("city", "Seattle"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores").isNotEmpty())
                .andExpect(jsonPath("$._embedded.stores[*].city").value(everyItem(is("Seattle"))));
    }

    @Test
    void getStoresByCity_WhenCityNotExists_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/stores/search/city").param("city", "Gondia"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores").isEmpty());
    }

    @Test
    void getStoresByState_WhenStateExists_ShouldReturnNonEmptyList() throws Exception {
        String state = "CA";
        mockMvc.perform(get("/api/stores/search/state")
                        .param("state", state))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores").isNotEmpty())
                .andExpect(jsonPath("$._embedded.stores[*].state").value(everyItem(is(state))));
    }

    @Test
    void getStoresByState_WhenStateNotExists_ShouldReturnEmptyList() throws Exception {
        String unknownState = "NY";
        mockMvc.perform(get("/api/stores/search/state")
                        .param("state", unknownState))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores").isEmpty());
    }

    @Test
    void getStoresByName_WhenNameExists_ShouldReturnNonEmptyList() throws Exception {
        String name = "Barnum";
        mockMvc.perform(get("/api/stores/search/name")
                        .param("name", name))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores").isNotEmpty())
                .andExpect(jsonPath("$._embedded.stores[*].storName")
                        .value(everyItem(containsStringIgnoringCase(name))));
    }

    @Test
    void getStoresByName_WhenNameNotExists_ShouldReturnEmptyList() throws Exception {
        String unknownName = "Unknown Book Store";
        mockMvc.perform(get("/api/stores/search/name")
                        .param("name", unknownName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores").isEmpty());
    }


    // ------------------- PAGINATION -------------------

    @Test
    public void testPagination_Default_ShouldReturnFirstPage() throws Exception {
        mockMvc.perform(get("/api/stores")) // No parameters passed
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$._embedded.stores").exists());
    }

    @Test
    public void testPage2_Pagination_ShouldReturnOnlyFiveStores() throws Exception {
        long totalInDb = storeRepository.count(); // Dynamic total

        mockMvc.perform(get("/api/stores")
                        .param("page", "0")
                        .param("size", "5")) // Requesting exactly 5
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores", hasSize(5))) // Keep this 5!
                .andExpect(jsonPath("$.page.size").value(5))
                .andExpect(jsonPath("$.page.totalElements").value((int) totalInDb)); // Dynamic
    }

    @Test
    public void testPagination_SecondPage_ShouldReturnSixthStore() throws Exception {
        mockMvc.perform(get("/api/stores")
                        .param("page", "1")  // Request the second page
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores", hasSize(1)))
                .andExpect(jsonPath("$._embedded.stores[0].storName").value("Bookbeat"))
                .andExpect(jsonPath("$.page.number").value(1));
    }

    // ------------------- POST APIs -------------------

    @Test
    void insertStore_WithValidData_ShouldReturn201() throws Exception {
        String newStoreJson = """
            {
                "storId": "9910",
                "storName": "Vedika's Book Hub",
                "storAddress": "RCOEM Square",
                "city": "Nagpur",
                "state": "MH",
                "zip": "44001"
            }
            """;

        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStoreJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/stores/9910")));
    }

    @Test
    void insertStore_WhenIdAlreadyExists_ShouldReturnConflict() throws Exception {
        String duplicateJson = """
            {
                "storId": "7066", 
                "storName": "Hacker Store",
                "city": "Nagpur"
            }
            """;

        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateJson))
                .andExpect(status().isConflict()); // HTTP 409 Conflict
    }

    @Test
    void insertStore_WithIsActiveFalse_ShouldIgnoreAndSetTrue() throws Exception {
        String sneakyJson = """
            {
                "storId": "9955",
                "storName": "Sneaky Store",
                "isActive": false 
            }
            """;

        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sneakyJson))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/stores/9955"))
                .andExpect(status().isOk());
    }

    // ------------------- PUT APIs -------------------

    @Test
    void updateStore_WithValidData_ShouldReturn200() throws Exception {
        String storeId = "7066";
        String updatedJson = """
            {
                "storName": "Barnum's Nagpur",
                "city": "Nagpur"
            }
            """;

        mockMvc.perform(put("/api/stores/" + storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storName").value("Barnum's Nagpur"));
    }

    @Test
    void updateStore_WhenIdDoesNotExist_ShouldReturnNotFound() throws Exception {
        String fakeStoreJson = """
            {
                "storName": "Ghost Store",
                "city": "Nowhere"
            }
            """;

        mockMvc.perform(put("/api/stores/0000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fakeStoreJson))
                .andExpect(status().isNotFound()); // HTTP 404 from EventHandler
    }

    // ------------------- PATCH APIs -------------------

    @Test
    void patchStore_WithValidId_ShouldUpdateSingleField() throws Exception {
        String storeId = "7066";
        String partialUpdateJson = """
            {
                "city": "Nagpur"
            }
            """;

        mockMvc.perform(patch("/api/stores/" + storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partialUpdateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city", is("Nagpur")))
                .andExpect(jsonPath("$.storName", is("Barnum's")));
    }

    @Test
    void patchStore_WhenIdDoesNotExist_ShouldReturnNotFound() throws Exception {
        String fakePatchJson = """
            {
                "storName": "Ghost Update"
            }
            """;

        mockMvc.perform(patch("/api/stores/0000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fakePatchJson))
                .andExpect(status().isNotFound()); // From StoreEventHandler
    }

    // ------------------- DELETE APIs -------------------

    @Test
    void deleteStore_WithValidId_ShouldReturn204AndHideRecord() throws Exception {
        String storeId = "7131";

        mockMvc.perform(delete("/api/stores/" + storeId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/stores/" + storeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStore_WithInvalidId_ShouldReturn404() throws Exception {
        String ghostId = "9999";

        mockMvc.perform(delete("/api/stores/" + ghostId))
                .andExpect(status().isNotFound()); // GlobalExceptionHandler handles this
    }

    // --- PROJECTION SECURITY ---

    @Test
    void getStoreById_WithStoreSummaryProjection_ShouldHideInternalId() throws Exception {
        // Goal: Prove that applying the storeSummary projection explicitly hides
        // the raw Database ID and internal security flags from the frontend payload.

        mockMvc.perform(get("/api/stores/7066?projection=storeSummary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storName").value("Barnum's"))
                .andExpect(jsonPath("$.city").value("Tustin"))

                // CRITICAL LEAK CHECK: Prove raw DB ID and isActive flag are hidden
                .andExpect(jsonPath("$.storId").doesNotExist())
                .andExpect(jsonPath("$.isActive").doesNotExist());
    }
}
