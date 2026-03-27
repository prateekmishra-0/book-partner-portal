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
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // Cleanup to avoid "Duplicate Entry" errors in real MySQL DB
        if (titleRepository.existsById("BU1332")) {
            titleRepository.deleteById("BU1332");
            entityManager.flush();
        }

        // 1. Setup Publisher (Check first to avoid duplicate)
        Publisher pub;
        if (!publisherRepository.existsById("1389")) {
            pub = new Publisher("1389", "Algodata Infosystems", "Berkeley", "CA", "USA",true);
            publisherRepository.save(pub);
        } else {
            pub = publisherRepository.findById("1389").get();
        }

        // 2. Setup Base Title "The Good Book"
        Title book = new Title();
        book.setTitleId("BU1332");
        book.setTitle("The Good Book");
        book.setPublisher(pub);
        book.setType("philosophy");
        book.setPrice(19.99);
        book.setPubdate(LocalDateTime.now());
        book.setIsActive(true);
        
        titleRepository.save(book);
        
        // Sync with DB
        entityManager.flush();
        entityManager.clear();
    }

    // --- ADD TEST (Gave it back!) ---
    @Test
    @DisplayName("Repo: Add New Title with Relationship")
    void testAddNewTitle() {
        // We need a unique Publisher for this specific new book
        Publisher testPub = new Publisher("9999", "Dev1 Publications", "Nagpur", "MH", "India",true);
        publisherRepository.save(testPub);

        Title newBook = new Title();
        newBook.setTitleId("BT7777");
        newBook.setTitle("Spring Boot Pro Guide");
        newBook.setType("popular_comp");
        newBook.setPrice(45.99);
        newBook.setPubdate(LocalDateTime.now());
        newBook.setPublisher(testPub); // Linking the object
        newBook.setIsActive(true);

        Title saved = titleRepository.save(newBook);

        // Verify the data was stored
        assertThat(saved.getTitleId()).isEqualTo("BT7777");
        assertThat(saved.getPublisher().getPubName()).isEqualTo("Dev1 Publications");

        // Verify it can be fetched back
        Optional<Title> fetched = titleRepository.findById("BT7777");
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getTitle()).isEqualTo("Spring Boot Pro Guide");
    }

    // --- Basic CRUD Tests ---
    @Test
    void testFindAll() {
        List<Title> titles = titleRepository.findAll();
        assertThat(titles).isNotEmpty();
    }

    @Test
    void testFindById() {
        Optional<Title> title = titleRepository.findById("BU1332");
        assertThat(title).isPresent();
    }

    // --- Search Logic Tests ---
    @Test
    void testFindByExactTitle() {
        Optional<Title> title = titleRepository.findByTitle("The Good Book");
        assertThat(title).isPresent();
    }

    @Test
    void testFindBySimilarTitle() {
        List<Title> list = titleRepository.findByTitleContainingIgnoreCase("Good");
        assertThat(list).isNotEmpty();
    }

    @Test
    void testFindByType() {
        List<Title> list = titleRepository.findByTypeIgnoreCase("philosophy");
        assertThat(list).isNotEmpty();
    }

    // --- Numeric Comparison (The Employee Style) ---
    @Test
    void testPriceComparison_Logic() {
        assertThat(titleRepository.findByPriceGreaterThan(15.00)).isNotEmpty();
        assertThat(titleRepository.findByPriceLessThan(10.00)).isEmpty();

        List<Title> results = titleRepository.findByPriceBetween(15.00, 25.00);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPrice()).isEqualTo(19.99);
    }

    // --- Soft Delete Test ---
    @Test
    @DisplayName("Repo: Soft Delete Logic (Check Hidden)")
    void testSoftDelete_Logic() {
        Title book = titleRepository.findById("BU1332").get();
        book.setIsActive(false);
        titleRepository.save(book);

        // Force Hibernate to check DB @Where filter
        entityManager.flush();
        entityManager.clear();

        Optional<Title> deletedBook = titleRepository.findById("BU1332");
        assertThat(deletedBook).isEmpty();
    }

    @Test
    @DisplayName("Repo: Partial Update Check")
    void testUpdateTitlePartial() {
        Title book = titleRepository.findById("BU1332").get();
        book.setPrice(99.99);
        titleRepository.save(book);

        Title updated = titleRepository.findById("BU1332").get();
        assertThat(updated.getPrice()).isEqualTo(99.99);
        assertThat(updated.getTitle()).isEqualTo("The Good Book"); // Title should remain same
    }
}