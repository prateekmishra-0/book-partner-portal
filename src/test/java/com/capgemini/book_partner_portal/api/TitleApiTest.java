package com.capgemini.book_partner_portal.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.capgemini.book_partner_portal.entity.Title;
import com.capgemini.book_partner_portal.repository.TitleRepository;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.assertj.core.api.Assertions.assertThat;
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

    // will work as prerequisite for all test cases
    @BeforeEach
    void initData() {
       Title book = new Title();
        book.setTitleId("BU1032");
        book.setTitle("The Busy Executive's Database Guide");
        book.setType("business");
        book.setPubId("1389");
        book.setPrice(19.99);
        book.setRoyalty(10);
        book.setPubdate(LocalDateTime.now());
        titleRepository.save(book);
    }

    @Test
    void testFetchMasterBookList() throws Exception {
        
        mockMvc.perform(get("/api/titles"))
        // will display list in console
        .andDo(print())
        // check is status is ok
        .andExpect(status().isOk())
        ;       
    }

    @Test
    void testFetchMasterBookEmptyList() throws Exception {
        titleRepository.deleteAll();
        mockMvc.perform(get("/api/titles")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                // Returns an empty HAL collection: { "_embedded": { "titles": [] } }
                .andExpect(jsonPath("$._embedded.titles", hasSize(0)));
    }

    @Test
    void testFindByTitle() throws Exception{
        mockMvc.perform(get("/api/titles/search/findByTitle")
                .param("title", "The Busy Executive's Database Guide") 
                .accept("application/hal+json")) 
                .andDo(print()) 
                // checking status first
                .andExpect(status().isOk()) 
                
                // here if status is ok it will return hal+json
                .andExpect(content().contentType("application/hal+json"));
    }

     @Test
    void testFindByTitle_Empty() throws Exception {
        mockMvc.perform(get("/api/titles/search/findByTitle")
                .param("title", "Harry Potter")
                .accept("application/hal+json"))
                .andExpect(status().isNotFound());
                // Returns an empty HAL collection: { "_embedded": { "titles": [] } }
    }

    @Test
    void testFindByType() throws Exception{ 
        mockMvc.perform(get("/api/titles/search/findByType")
                .param("type", "business") 
                .accept("application/hal+json")) 
                .andDo(print()) 
                // checking status first
                .andExpect(status().isOk()) 
                
                // here if status is ok it will return hal+json
                .andExpect(content().contentType("application/hal+json"));
    }

    @Test
    void testFindByType_Empty() throws Exception {
        mockMvc.perform(get("/api/titles/search/findByType")
                .param("type", "dancing")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                // Returns an empty HAL collection: { "_embedded": { "titles": [] } }
                .andExpect(jsonPath("$._embedded.titles", hasSize(0)));
    }

    @Test
    void testFindByPrice() throws Exception {
        mockMvc.perform(get("/api/titles/search/price")
                .param("value", "19.99") 
                .accept("application/hal+json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.titles").exists());
    }
    
    @Test
    void testFindByPriceLessThan() throws Exception {
        mockMvc.perform(get("/api/titles/search/priceLessThan")
                .param("value", "100.00")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                // verify that results exists
                .andExpect(jsonPath("$._embedded.titles").exists());
    }
    
    @Test
    void testFindByPriceBetween() throws Exception {
        mockMvc.perform(get("/api/titles/search/priceBetween")
                .param("min", "10.0")
                .param("max", "50.0")
                .accept("application/hal+json"))
                .andExpect(status().isOk())
                // verify that results exists
                .andExpect(jsonPath("$._embedded.titles").exists());
    }

    @Test
    void testFindByPriceGreaterThan() throws Exception {
       String searchValue = "10.00";

       mockMvc.perform(get("/api/titles/search/priceGreaterThan")
               .param("value", searchValue)
               .accept("application/hal+json"))
               .andDo(print()) // This helps us see the JSON in the console
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/hal+json"))
               
               // Verify that the results exist 
               .andExpect(jsonPath("$._embedded.titles").exists());
    }

    @Test
    void testAddNewBookOrUpdateExisting() throws Exception{
        // write the json content in string
        String id = "PC9999";
        String newBookJson = "{" +
                    "\"title\": \"Programming in Java\"," +
                    "\"type\": \"popular_comp\"," +
                    "\"pub_id\": \"1389\"," +
                    "\"price\": 29.99," +
                    "\"advance\": 5000.00," +
                    "\"royalty\": 10," +
                    "\"ytd_sales\": 100," +
                    "\"notes\": \"A complete guide for beginners.\"," +
                    "\"pubdate\": \"2026-03-26T12:00:00\"" +
                    "}";
        // perform the same way just put the json string in content
        mockMvc.perform(put("/api/titles/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newBookJson)
                .accept("application/hal+json"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Programming in Java")));
        
        assertThat(titleRepository.existsById("PC9999")).isTrue();
    
        }  

}