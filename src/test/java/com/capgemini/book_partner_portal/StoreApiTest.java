package com.capgemini.book_partner_portal;

import com.capgemini.book_partner_portal.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StoreApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    @WithMockUser
    public void testGetAllStores_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/stores"))
                .andDo(print()) // This prints the JSON rows/body to your console
                .andExpect(status().isOk());

        assert(storeRepository.findAll().size() == 6);
    }
}
