package com.capgemini.book_partner_portal.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.ContextConfiguration;

import com.capgemini.book_partner_portal.BookPartnerPortalApplication;
import com.capgemini.book_partner_portal.entity.Title;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = BookPartnerPortalApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TitleRepositoryTest {

    @Autowired
    private TitleRepository titleRepository;

    @BeforeEach
    void setUp(){
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
    void testFindAll() {
        List<Title> titles = titleRepository.findAll();
        assertThat(titles).isNotEmpty(); 
    }

    @Test
    void testFindBookEmptyList(){
        titleRepository.deleteAll();
        List<Title> list = titleRepository.findAll();
        assertThat(list).isEmpty();
    }

    @Test
    void testFindByExactTitle(){
        Optional<Title> list = titleRepository.findByTitle("The Busy Executive's Database Guide");
        assertThat(list).isNotEmpty();
    }

    @Test
    void testFindBySimilarTitle(){
        List<Title> list = titleRepository.findByTitleContainingIgnoreCase("The Busy");
        assertThat(list).isNotEmpty();
    }

    @Test
    void testFindByTitle_Empty(){
        Optional<Title> list = titleRepository.findByTitle("Not an actual book");
        assertThat(list).isEmpty(); 
    }

    @Test
    void testFindByType(){
        List<Title> list = titleRepository.findByType("business");
        assertThat(list).isNotEmpty();
    }

    @Test
    void testFindByType_Empty(){
        List<Title> list = titleRepository.findByType("not an actual type");
        assertThat(list).isEmpty();
    }

    
       

    
    



    
}