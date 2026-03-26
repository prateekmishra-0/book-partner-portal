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
@AutoConfigureMockMvc // virtual tomcat or stimulate Tomcat
@Transactional // it will rollback after running tests
public class PublisherApiTest {

    @Autowired
    private MockMvc mockMvc;

    private Publisher testPublisher;

    @Autowired
    private PublisherRepository publisherRepository;

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
        mockMvc.perform(get("/api/publishers/search/pubname") // Added /search/
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
        mockMvc.perform(get("/api/publishers/search/pubname").param("pubName", "random"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }



    // searching by city
    @Test
    void shouldReturnPublisherListByCity() throws Exception {
        mockMvc.perform(get("/api/publishers/search/city").param("city", testPublisher.getCity()))
                .andDo(print())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect((jsonPath("$._embedded.publishers").exists()))
                .andExpect(jsonPath("$._embedded.publishers[0].city").value(testPublisher.getCity()));
    }

    // city does not exists
    @Test
    void shouldReturnPublisherListByCityEmpty() throws Exception {
        mockMvc.perform(get("/api/publishers/search/city?").param("city", "pune"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.publishers").isEmpty());
    }

    // searching by city
    @Test
    void shouldReturnPublisherListByState() throws Exception {
        mockMvc.perform(get("/api/publishers/search/state").param("state", testPublisher.getState()))
                .andDo(print())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect((jsonPath("$._embedded.publishers").exists()))
                .andExpect(jsonPath("$._embedded.publishers[0].state").value(testPublisher.getState()));
    }

    // city does not exists
    @Test
    void shouldReturnPublisherListByStateEmpty() throws Exception {
        mockMvc.perform(get("/api/publishers/search/state").param("state", "VJ"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.publishers").isEmpty());
    }


}
