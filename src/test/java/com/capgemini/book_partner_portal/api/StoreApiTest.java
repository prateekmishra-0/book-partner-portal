package com.capgemini.book_partner_portal.api;

import com.capgemini.book_partner_portal.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    public void testGetAllStores_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/stores"))
                .andDo(print())
                .andExpect(status().isOk());

//        assert(storeRepository.findAll().size() == 6);
        assertEquals(6, storeRepository.findAll().size());
    }

    @Test
    public void testSearchByCityApi_ShouldReturnSpecificJsonResponse() throws Exception {
        String city = "Seattle";

        mockMvc.perform(get("/stores/search/findByCity")
                        .param("city", city)) // This sends ?city=Seattle
                .andDo(print())       // Prints the JSON in your console
                .andExpect(status().isOk())
                // Verify the specific store details from the script
                .andExpect(jsonPath("$._embedded.stores[0].storName").value("Eric the Read Books"))
                .andExpect(jsonPath("$._embedded.stores[0].storAddress").value("788 Catamaugus Ave."))
                .andExpect(jsonPath("$._embedded.stores[0].city").value("Seattle"));
    }
}
