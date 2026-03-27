package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.BookPartnerPortalApplication;
import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.entity.Title;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

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

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private TestEntityManager entityManager; // Required for clearing cache

    @BeforeEach
    void setUp() {
        // Cleanup to avoid "Duplicate Entry" errors in real DB
        if (titleRepository.existsById("BU1332")) {
            titleRepository.deleteById("BU1332");
            entityManager.flush();
        }

        // 1. Create and Save Publisher
        Publisher pub;
        if (!publisherRepository.existsById("1389")) {
            pub = new Publisher("1389", "Algodata Infosystems", "Berkeley", "CA", "USA",true);
            publisherRepository.save(pub);
        } else {
            pub = publisherRepository.findById("1389").get();
        }

        // 2. Create Title and link the Publisher object
        Title book = new Title();
        book.setTitleId("BU1332");
        book.setTitle("The Good Book");
        book.setPublisher(pub);
        book.setType("philosophy");
        book.setPrice(19.99);
        book.setRoyalty(10);
        book.setPubdate(LocalDateTime.now());
        book.setActive(true); // Ensure it's active for searches
        
        titleRepository.save(book);
        
        // Sync with DB so searches find the new data
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void testFindAll() {
        List<Title> titles = titleRepository.findAll();
        assertThat(titles).isNotEmpty();
    }

    @Test
    @DisplayName("Find By ID: BU1332")
    void testFindById() {
        Optional<Title> title = titleRepository.findById("BU1332");
        assertThat(title).isPresent();
        assertThat(title.get().getTitle()).isEqualTo("The Good Book");
    }

    @Test
    void testFindbyIDNotFound() {
        Optional<Title> title = titleRepository.findById("AB2132");
        assertThat(title).isEmpty();
    }

    @Test
    @DisplayName("Search Exact: The Good Book")
    void testFindByExactTitle() {
        Optional<Title> title = titleRepository.findByTitle("The Good Book");
        assertThat(title).isPresent();
    }

    @Test
    @DisplayName("Search Similar: 'Good'")
    void testFindBySimilarTitle() {
        List<Title> list = titleRepository.findByTitleContainingIgnoreCase("Good");
        assertThat(list).isNotEmpty();
        assertThat(list.get(0).getTitle()).contains("Good");
    }

    @Test
    void testFindByType() {
        List<Title> list = titleRepository.findByTypeIgnoreCase("philosophy");
        assertThat(list).isNotEmpty();
    }

    @Test
    void testFindByPrice() {
        List<Title> list = titleRepository.findByPrice(19.99);
        assertThat(list).isNotEmpty();
    }

    @Test
    void testFindByPriceGreaterThan() {
        List<Title> list = titleRepository.findByPriceGreaterThan(15.00);
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("Repo Test: Update Price and Notes")
    void testUpdateTitlePartial() {
        String id = "BU1332";
        Title book = titleRepository.findById(id).get();
        
        book.setPrice(25.00);
        book.setNotes("Wisdom is expensive.");
        titleRepository.save(book);

        Title updated = titleRepository.findById(id).get();
        assertThat(updated.getPrice()).isEqualTo(25.00);
        assertThat(updated.getNotes()).isEqualTo("Wisdom is expensive.");
    }

    @Test
    @DisplayName("Soft Delete Check: Verify BU1332 is hidden")
    void testSoftDelete_Logic() {
        Title book = titleRepository.findById("BU1332").get();
        book.setActive(false);
        titleRepository.save(book);

        // Force Hibernate to check the @Where clause in DB
        entityManager.flush();
        entityManager.clear();

        Optional<Title> deletedBook = titleRepository.findById("BU1332");
        assertThat(deletedBook).isEmpty();
    }
}