package com.capgemini.book_partner_portal.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    void testFindByPrice(){
        List<Title> list = titleRepository.findByPrice(19.99);
        assertThat(list).isNotEmpty();
    }
    
    @Test
    void testFindByPriceGreaterThan(){
        List<Title> list = titleRepository.findByPriceGreaterThan(18.00);
        assertThat(list).isNotEmpty();
    }

    @Test
    void testFindByPriceLessThan(){
        List<Title> list = titleRepository.findByPriceLessThan(20.00);
        assertThat(list).isNotEmpty();
    }

    @Test 
    void testFindByPriceBetween(){
        List<Title> list = titleRepository.findByPriceBetween(18.00, 20.00);
        assertThat(list).isNotEmpty();
    }

    @Test
    void testSaveNewTitle() {
        // Create a new Title entity
        Title newBook = new Title();
        newBook.setTitleId("BT7777");
        newBook.setTitle("Spring Boot Pro Guide");
        newBook.setType("popular_comp");
        newBook.setPrice(45.99);
        newBook.setPubdate(LocalDateTime.now());
        // Ensure you set any other @NotNull fields required by your schema (e.g., pub_id)
        newBook.setPubId("1389"); 

        // Save using the repository
        Title savedBook = titleRepository.save(newBook);

        // Verify the data was stored correctly
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitleId()).isEqualTo("BT7777");
        
        // Double check by fetching it back from the DB
        Optional<Title> fetchedBook = titleRepository.findById("BT7777");
        assertThat(fetchedBook).isPresent();
        assertThat(fetchedBook.get().getTitle()).isEqualTo("Spring Boot Pro Guide");
    }

    
 
}