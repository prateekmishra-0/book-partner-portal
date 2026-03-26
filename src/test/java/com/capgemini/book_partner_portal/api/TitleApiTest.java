package com.capgemini.book_partner_portal.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.capgemini.book_partner_portal.entity.Title;
import com.capgemini.book_partner_portal.repository.TitleRepository;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;



@SpringBootTest
@AutoConfigureMockMvc

@Transactional
class TitleApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TitleRepository titleRepository;

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
        .andDo(print())
        
        .andExpect(status().isOk())
        ;       
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

}