package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.BookPartnerPortalApplication;
import com.capgemini.book_partner_portal.entity.Author;
import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.entity.Title;
import com.capgemini.book_partner_portal.entity.TitleAuthor;
import com.capgemini.book_partner_portal.entity.TitleAuthorId;

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
    private AuthorRepository authorRepository;

    @Autowired
    private TitleAuthorRepository titleAuthorRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        titleRepository.findById("BU1332").ifPresent(t -> {
            titleRepository.delete(t); 
            entityManager.flush();
        });

        // 1. Setup Publisher
        if (!publisherRepository.existsById("1389")) {
            Publisher pub = new Publisher("1389", "Algodata Infosystems", "Berkeley", "CA", "USA", true);
            publisherRepository.save(pub);
        }

        // 2. Setup Base Title
        Title book = new Title();
        book.setTitleId("BU1332");
        book.setTitle("The Good Book");
        
        //  Use the pubId string, not the object, because the object is Read-Only
        book.setPubId("1389"); 
        
        book.setType("philosophy");
        book.setPrice(19.99);
        book.setPubdate(LocalDateTime.of(2026, 3, 1, 10, 0)); // Fixed date for testing
        book.setIsActive(true); //  Check if this should be setActive(true)
        
        titleRepository.save(book);
        entityManager.flush();
        entityManager.clear();
    }

    // --- ADD TEST ---
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

    @Test
    @DisplayName("Repo: Multi-Author Selection and Retrieval")
    void testMultiAuthorLinkingAndRetrieval() {
        // 1. Prepare Authors (Simulating the list available in your frontend dropdown)
        Author authorA = Author.builder()
                .auId("111-22-3333").firstName("Alice").lastName("Alpha")
                .contract(1).isActive(true).build();
        Author authorB = Author.builder()
                .auId("444-55-6666").firstName("Bob").lastName("Beta")
                .contract(1).isActive(true).build();
        
        authorRepository.saveAll(List.of(authorA, authorB));

        // 2. Fetch the Title created in setUp()
        Title book = titleRepository.findById("BU1332").get();

        // 3. Create "TitleAuthor" links (Simulating the frontend saving the selection)
        // Link Author A as Primary (Order 1)
        TitleAuthor link1 = new TitleAuthor(
            new TitleAuthorId(authorA.getAuId(), book.getTitleId()), 
            authorA, book, (byte) 1, 60
        );
        // Link Author B as Secondary (Order 2)
        TitleAuthor link2 = new TitleAuthor(
            new TitleAuthorId(authorB.getAuId(), book.getTitleId()), 
            authorB, book, (byte) 2, 40
        );

        titleAuthorRepository.saveAll(List.of(link1, link2));
        entityManager.flush();
        entityManager.clear();

        // 4. TEST: Retrieve authors by Title ID (The specific requirement)
        List<TitleAuthor> authorshipList = titleAuthorRepository.findById_TitleId("BU1332");

        // Verify counts and data integrity
        assertThat(authorshipList).hasSize(2);
        
        // Verify we can see Author names through the join table
        List<String> names = authorshipList.stream()
                .map(ta -> ta.getAuthor().getFirstName())
                .toList();
        
        assertThat(names).containsExactlyInAnyOrder("Alice", "Bob");

        // Verify the order and royalties are correct
        TitleAuthor primary = authorshipList.stream()
                .filter(ta -> ta.getAuOrd() == 1)
                .findFirst().get();
        assertThat(primary.getAuthor().getLastName()).isEqualTo("Alpha");
        assertThat(primary.getRoyaltyPer()).isEqualTo(60);
    }

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
    @DisplayName("Repo: Price Comparison with Full Database")
    void testPriceComparison_Logic() {
        // 1. Test Greater Than (15.00)
        // Our book "BU1332" is 19.99, so it MUST be in this list
        List<Title> greaterThan = titleRepository.findByPriceGreaterThan(15.00);
        assertThat(greaterThan).isNotEmpty();
        assertThat(greaterThan).anyMatch(t -> t.getTitleId().equals("BU1332"));

        // 2. Test Less Than (10.00)
        // In a full DB, there might be cheap books (e.g. 2.99). 
        // So we don't check for .isEmpty(). We check that OUR book is NOT there.
        List<Title> lessThan = titleRepository.findByPriceLessThan(10.00);
        assertThat(lessThan).noneMatch(t -> t.getTitleId().equals("BU1332"));

        // 3. Test Between (18.00 and 22.00)
        // In full DB, there might be 5 books in this range. 
        // We verify the list is not empty and contains our book.
        List<Title> results = titleRepository.findByPriceBetween(18.00, 22.00);
        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(t -> t.getTitleId().equals("BU1332") && t.getPrice() == 19.99);
    }

    @Test
    @DisplayName("Repo Security: pubdate and titleId are Immutable")
    void testImmutableFields_Logic() {
        Title book = titleRepository.findById("BU1332").get();
        LocalDateTime originalDate = book.getPubdate();

        // Attempt to change immutable fields
        book.setPubdate(LocalDateTime.now().plusDays(10)); 
        book.setPrice(55.55); // Change a mutable field to trigger update
        
        titleRepository.save(book);
        entityManager.flush();
        entityManager.clear();

        Title updated = titleRepository.findById("BU1332").get();
        
        // Verify: Price updated, but pubdate remained the same!
        assertThat(updated.getPrice()).isEqualTo(55.55);
        assertThat(updated.getPubdate()).isEqualTo(originalDate); 
    }

    // --- Soft Delete Test ---
    @Test
    @DisplayName("Repo: Soft Delete Logic (Check SQLRestriction)")
    void testSoftDelete_Logic() {
        Title book = titleRepository.findById("BU1332").get();
        book.setIsActive(false); // Trigger @SQLDelete
        titleRepository.save(book);

        entityManager.flush();
        entityManager.clear();

        // 🚨 Because of @SQLRestriction("is_active = true"), findById returns empty
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