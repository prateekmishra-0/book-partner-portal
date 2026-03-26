package com.capgemini.book_partner_portal.api;

import com.capgemini.book_partner_portal.repository.StoreRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StoreApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    public void testGetAllStores_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/stores"))
                .andDo(print())
                .andExpect(status().isOk());

//        assert(storeRepository.findAll().size() == 6);
        assertEquals(6, storeRepository.findAll().size());
    }

    @Test
    public void testSearchByCityApi_ShouldReturnSpecificJsonResponse() throws Exception {
        String city = "Seattle";

        mockMvc.perform(get("/api/stores/search/findByCity")
                        .param("city", city)) // This sends ?city=Seattle
                .andDo(print())       // Prints the JSON in your console
                .andExpect(status().isOk())
                // Verify the specific store details from the script
                .andExpect(jsonPath("$._embedded.stores[0].storName").value("Eric the Read Books"))
                .andExpect(jsonPath("$._embedded.stores[0].storAddress").value("788 Catamaugus Ave."))
                .andExpect(jsonPath("$._embedded.stores[0].city").value("Seattle"));
    }

    @Test
    public void testSearchByStateApi_ShouldWorkForMultipleStores() throws Exception {
        String state = "CA";

        mockMvc.perform(get("/api/stores/search/findByState")
                        .param("state", state))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores[*].state", everyItem(is(state))));
    }

    @Test
    public void testSearchByNameApi_ShouldWorkForPartialMatch() throws Exception {
        String name = "Barnum";

        mockMvc.perform(get("/api/stores/search/findByStorNameContaining")
                        .param("name", name))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores[*].storName", everyItem(containsString(name))));
    }

    @Test
    public void testPagination_Default_ShouldReturnFirstPage() throws Exception {
        mockMvc.perform(get("/api/stores")) // No parameters passed
                .andDo(print())
                .andExpect(status().isOk())
                // Spring default page number is always 0
                .andExpect(jsonPath("$.page.number").value(0))
                // It should contain our data
                .andExpect(jsonPath("$._embedded.stores").exists());
    }

    @Test
    public void testPage2_Pagination_ShouldReturnOnlyFiveStores() throws Exception {
        mockMvc.perform(get("/api/stores")
                        .param("page", "0")
                        .param("size", "5")) // Requesting only 5 out of 6
                .andDo(print())
                .andExpect(status().isOk())
                // Check that only 5 stores are in the list
                .andExpect(jsonPath("$._embedded.stores", hasSize(5)))
                // Check that the metadata reflects the size of 5
                .andExpect(jsonPath("$.page.size").value(5))
                // Total elements should still be 6
                .andExpect(jsonPath("$.page.totalElements").value(6))
                // Total pages should now be 2 (because the 6th store needs a new page)
                .andExpect(jsonPath("$.page.totalPages").value(2));
    }

    @Test
    public void testPagination_SecondPage_ShouldReturnSixthStore() throws Exception {
        mockMvc.perform(get("/api/stores")
                        .param("page", "1")  // Request the second page
                        .param("size", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                // There is only 1 store left for this page
                .andExpect(jsonPath("$._embedded.stores", hasSize(1)))
                // Verify it is the "Bookbeat" store from your script
                .andExpect(jsonPath("$._embedded.stores[0].storName").value("Bookbeat"))
                .andExpect(jsonPath("$.page.number").value(1));
    }

    @Test
    public void testSearchByName_NotFound_ShouldReturnEmpty() throws Exception {
        String unknownName = "Unknown Book Store";

        mockMvc.perform(get("/api/stores/search/findByStorNameContaining")
                        .param("name", unknownName))
                .andDo(print())
                .andExpect(status().isOk())
                // CHANGE THIS LINE:
                .andExpect(jsonPath("$._embedded.stores", hasSize(0)));
    }

    @Test
    public void testSearchByCity_NotFound_ShouldReturnEmpty() throws Exception {
        String unknownCity = "Nagpur";

        mockMvc.perform(get("/api/stores/search/findByCity")
                        .param("city", unknownCity))
                .andDo(print())
                .andExpect(status().isOk())
                // AND THIS ONE:
                .andExpect(jsonPath("$._embedded.stores", hasSize(0)));
    }

    @Test
    public void testSearchByState_NotFound_ShouldReturnEmpty() throws Exception {
        String unknownState = "NY";

        mockMvc.perform(get("/api/stores/search/findByState")
                        .param("state", unknownState))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.stores", hasSize(0)));
    }


}
