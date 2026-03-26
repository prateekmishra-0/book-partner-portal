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

    
}
