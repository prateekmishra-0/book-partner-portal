package com.capgemini.book_partner_portal.publisherTest;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
// For get(), post(), put(), delete()
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

// For status(), content(), jsonPath(), header()
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// For print() - optional but very helpful for debugging
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc // virtual tomcat or stimulate Tomcat
@Transactional // it will rollback after running tests
public class PublisherApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPublisherList() throws Exception {
        mockMvc.perform(get("/publishers"))
                .andDo(print())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.publishers").exists());

    }

    @Test
    void shouldReturnPublisherListByCity() throws Exception {
        mockMvc.perform(get("/publishers/search/city?city=Boston"))
                .andDo(print())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect((jsonPath("$._embedded.publishers").exists()))
                .andExpect(jsonPath("$._embedded.publishers[0].city").value("Boston"));
    }
}
