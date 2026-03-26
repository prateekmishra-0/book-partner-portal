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

@SpringBootTest
@AutoConfigureMockMvc
// virtual tomcat or stimulate Tomcat
@Transactional // it will rollback after running tests
public class PublisherApiTest {

    @Autowired
    private MockMvc mockMvc;

    private Publisher testPublisher;

    @Autowired
    private PublisherRepository publisherRepository;

    @Transactional
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

    @Test
    void shouldReturnPublisherListByCity() throws Exception {
        mockMvc.perform(get("/api/publishers/search/city?city=nagpur"))
                .andDo(print())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect((jsonPath("$._embedded.publishers").exists()))
                .andExpect(jsonPath("$._embedded.publishers[0].city").value("nagpur"))
                .andExpect(jsonPath("$._embedded.publishers[0].state").value("MH"))
                .andExpect(jsonPath("$._embedded.publishers[0].pubName").value("sujal nimje"))
                .andExpect(jsonPath("$._embedded.publishers[0].country").value("India"));
    }
}
