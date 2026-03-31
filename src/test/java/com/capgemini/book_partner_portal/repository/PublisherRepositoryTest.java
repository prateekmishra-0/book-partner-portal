package com.capgemini.book_partner_portal.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.capgemini.book_partner_portal.entity.Publisher;
import com.capgemini.book_partner_portal.entity.Title;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class PublisherRepositoryTest {

    @Autowired
    private PublisherRepository publisherRepository;

    private Publisher testPublisher;

    private final Title testTitle = new Title();

    @Autowired
    private TitleRepository titleRepository;

    @BeforeEach
    void setUpTestPublisher() {
        testPublisher = Publisher.builder()
                .pubId("9994")
                .pubName("sujal nimje")
                .city("nagpur")
                .state("MH")
                .country("India").build();

        publisherRepository.save(testPublisher);

        // 3. Setup Base Title (Matching your new Repository Test data)
        testTitle.setTitleId("BU1332");
        testTitle.setTitle("The Good Book");
        testTitle.setPublisher(testPublisher);
        testTitle.setType("philosophy");
        testTitle.setPrice(19.99);
        testTitle.setRoyalty(10);
        testTitle.setPubdate(LocalDateTime.now());
        testTitle.setIsActive(true);
        testTitle.setPubId(testPublisher.getPubId());
        titleRepository.save(testTitle);
    }

    @Test
    void shouldReturnAllPublishers() {

        List<Publisher> publishers = publisherRepository.findAll();

        Assertions.assertNotNull(publishers);
        assertThat(publishers).isNotEmpty();
    }

    @Test
    void shouldReturnPublisherById() {

        // Publisher
        String id = testPublisher.getPubId();

        Optional<Publisher> optionalPublisher = publisherRepository.findById(id);

        Assertions.assertTrue(optionalPublisher.isPresent());
    }

    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {

        String id = "9998";

        Optional<Publisher> optionalPublisher = publisherRepository.findById(id);

        Assertions.assertTrue(optionalPublisher.isEmpty());
    }

    @Test
    void shouldReturnPublisherByName() {
        String pubName = testPublisher.getPubName();

        Optional<Publisher> optionalPublisher = publisherRepository.findByPubName(pubName);

        Assertions.assertTrue(optionalPublisher.isPresent());
        Assertions.assertEquals(testPublisher.getPubName(), optionalPublisher.get().getPubName());
    }

    @Test
    void shouldReturnNotFoundWhenPublisherNameDoesNotExists() {
        String pubName = "random";

        Optional<Publisher> optionalPublisher = publisherRepository.findByPubName(pubName);

        Assertions.assertFalse(optionalPublisher.isPresent());
    }

    @Test
    void shouldReturnPublisherListByName() {
        String pubName = testPublisher.getPubName().substring(0, testPublisher.getPubName().length() - 2);

        Page<Publisher> publisherPage = publisherRepository.findByPubNameContainingIgnoreCase(pubName.substring(0, pubName.length() - 2), PageRequest.of(0, 10));
        assertThat(publisherPage.getContent()).isNotEmpty();
        for (Publisher publisher : publisherPage.getContent()) {
            Assertions.assertTrue(publisher.getPubName().contains(pubName));
        }
    }

    @Test
    void shouldReturnEmptyPublisherListByName() {
        String pubName = "random";

        Page<Publisher> publisherPage = publisherRepository.findByPubNameContainingIgnoreCase(pubName.toLowerCase(), PageRequest.of(0, 10));

        Assertions.assertEquals(0, publisherPage.getContent().size());
    }

    @Test
    void shouldReturnPublisherListByCity() {
        String city = testPublisher.getCity();

        Page<Publisher> publisherPage = publisherRepository.findByCityContainingIgnoreCase(city, PageRequest.of(0, 10));
        assertThat(publisherPage.getContent()).isNotEmpty();
        for (Publisher publisher : publisherPage.getContent()) {
            Assertions.assertTrue(publisher.getCity().contains(city));
        }
    }

    @Test
    void shouldReturnEmptyPublisherListByCity() {
        String city = "random";

        Page<Publisher> publisherPage = publisherRepository.findByCityContainingIgnoreCase(city.toLowerCase(), PageRequest.of(0, 10));

        Assertions.assertEquals(0, publisherPage.getContent().size());
    }

    @Test
    void shouldReturnPublisherListByState() {
        String state = testPublisher.getState();

        Page<Publisher> publisherPage = publisherRepository.findByStateContainingIgnoreCase(state.toLowerCase(), PageRequest.of(0, 10));
        assertThat(publisherPage.getContent()).isNotEmpty();
        for (Publisher publisher : publisherPage.getContent()) {
            Assertions.assertTrue(publisher.getState().contains(state));
        }
    }

    @Test
    void shouldReturnEmptyPublisherListByState() {
        String state = "random";

        Page<Publisher> publisherPage = publisherRepository.findByStateContainingIgnoreCase(state, PageRequest.of(0, 10));

        Assertions.assertEquals(0, publisherPage.getContent().size());
    }

    @Test
    void shouldReturnPublisherListByCountry() {
        String country = testPublisher.getCountry();

        Page<Publisher> publisherPage = publisherRepository.findByCountryContainingIgnoreCase(country.toLowerCase(), PageRequest.of(0, 10));
        Assertions.assertNotNull(publisherPage);
        Assertions.assertFalse(publisherPage.getContent().isEmpty());
        for (Publisher publisher : publisherPage.getContent()) {
            Assertions.assertTrue(publisher.getCountry().contains(country));
        }
    }

    @Test
    void shouldReturnEmptyPublisherListByCountry() {
        String country = "random";

        Page<Publisher> publisherPage = publisherRepository.findByCountryContainingIgnoreCase(country, PageRequest.of(0, 10));
        Assertions.assertNotNull(publisherPage);
        Assertions.assertEquals(0, publisherPage.getContent().size());
    }

    // save
    @Test
    void shouldSavePublisher() {

        Publisher publisher = Publisher.builder()
                .pubId("9921")
                .city("nagpur")
                .pubName("john")
                .state("MH")
                .country("India")
                .build();

        publisherRepository.save(publisher);

        Optional<Publisher> optionalPublisher = publisherRepository.findById(publisher.getPubId());

        Assertions.assertTrue(optionalPublisher.isPresent());
        Assertions.assertEquals(publisher.getPubId(), optionalPublisher.get().getPubId());
    }

    // save duplicate id but count doesnt increased
    @Test
    void shouldNotCreateDuplicateWhenIdIsSame() {

        // first save before each
        long countAfterFirstSave = publisherRepository.count();

        // Second save with same ID
        publisherRepository.save(testPublisher);
        long countAfterSecondSave = publisherRepository.count();

        // Assertions
        Assertions.assertEquals(countAfterFirstSave, countAfterSecondSave, "Count should not increase for duplicate ID");
    }

    // update the existing record
    @Test
    void shouldUpdateExistingRecord() {

        // we have inserted test record in before each now updating that record
        testPublisher.setPubName("random_name");

        publisherRepository.save(testPublisher);

        // find by id in database
        Optional<Publisher> optionalPublisher = publisherRepository.findById(testPublisher.getPubId());

        // Assertions
        Assertions.assertTrue(optionalPublisher.isPresent());
        Assertions.assertEquals(testPublisher.getPubName(), optionalPublisher.get().getPubName());
    }

    // saving publisher with constraint voiletion
    @Test
    void shouldThrowErrorWhileSavingPublisher() {
        Publisher publisher = Publisher.builder()
                .pubId("0000")
                .city("nagpur")
                .pubName("john")
                .state("MH")
                .country("India")
                .build();

        // We use saveAndFlush to force Hibernate to validate the entity NOW
        assertThrows(ConstraintViolationException.class, () -> {
            publisherRepository.saveAndFlush(publisher);
        });
    }

    @Test
    void shouldThrowErrorWhileSavingPublisherStateConstraintVoileted() {
        // ID "0000" does not match the regex ^(1389|0736|0877|1622|1756|99\d{2})$
        Publisher publisher = Publisher.builder()
                .pubId("9993")
                .pubName("John Doe")
                .city("Nagpur")
                .state("MHZ")
                .country("India")
                .build();

        // We use saveAndFlush to force Hibernate to validate the entity NOW
        assertThrows(ConstraintViolationException.class, () -> {
            publisherRepository.saveAndFlush(publisher);
        });
    }

    /**
     * TEST: Ensure default value is applied Verifies that when a Publisher is
     * saved without a country, it defaults to "USA".
     */
    @Test
    void shouldApplyDefaultCountryWhenNotProvided() {
        Publisher publisher = Publisher.builder()
                .pubId("1622") // Valid ID from regex
                .pubName("Default Test Pub")
                .state("NY")
                .build(); // country is not set

        Publisher saved = publisherRepository.save(publisher);

        Assertions.assertEquals("USA", saved.getCountry(), "Country should default to USA");
    }

    /**
     * TEST: Verify partial update logic Simulates the behavior of a PATCH
     * request by retrieving, modifying one field, and re-saving.
     */
    @Test
    void shouldOnlyUpdateProvidedField() {
        // 1. Get existing publisher from @BeforeEach (testPublisher)
        String originalCity = testPublisher.getCity();
        String newName = "New Updated Name";

        // 2. Perform partial change
        testPublisher.setPubName(newName);
        publisherRepository.save(testPublisher);

        // 3. Verify
        Publisher updated = publisherRepository.findById(testPublisher.getPubId()).get();
        Assertions.assertEquals(newName, updated.getPubName());
        Assertions.assertEquals(originalCity, updated.getCity(), "City should remain unchanged");
    }

    /**
     * TEST: Invalid ID pattern (Constraint Violation) Checks that an ID like
     * '1234' (which fails the regex) triggers an exception.
     */
    @Test
    void shouldThrowExceptionWhenIdFormatIsInvalid() {
        Publisher invalidIdPub = Publisher.builder()
                .pubId("1234") // Valid length (4), but not in the approved regex list
                .pubName("Invalid ID Pub")
                .state("CA")
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            publisherRepository.saveAndFlush(invalidIdPub);
        });
    }

    /**
     * TEST: @NotBlank constraint Verifies that the repository won't allow
     * saving a publisher without a name.
     */
    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        Publisher noNamePub = Publisher.builder()
                .pubId("9988")
                .pubName("") // Blank name
                .state("TX")
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            publisherRepository.saveAndFlush(noNamePub);
        });
    }

    /**
     * REPOSITORY TEST: Find All Pagination Verifies that the database correctly
     * slices the data into pages.
     */
    @Test
    void shouldReturnCorrectPageSliceForFindAll() {

        long count = publisherRepository.count();
        // 1. Setup: Save 3 publishers to the database
        publisherRepository.save(Publisher.builder().pubId("9911").pubName("Alpha").build());
        publisherRepository.save(Publisher.builder().pubId("9912").pubName("Beta").build());
        publisherRepository.save(Publisher.builder().pubId("9913").pubName("Gamma").build());

        // 2. Action: Request Page 0 with a size of 2, sorted by ID
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by("pubId").ascending());
        Page<Publisher> result = publisherRepository.findAll(pageRequest);

        // 3. Assertions
        Assertions.assertEquals(2, result.getContent().size(), "Should only return 2 records for the first page");
        Assertions.assertEquals(count + 3, result.getTotalElements(), "Total count in DB should be count + 3");
        Assertions.assertTrue(result.hasNext(), "There should be a next page since total is 3 and size is 2");
    }

    // for delete
    @Test
    void shouldDeletePublisherById() {

        String pubId = testPublisher.getPubId();

        publisherRepository.deleteById(pubId);

        Optional<Publisher> optionalPublisher = publisherRepository.findById(pubId);

        Assertions.assertTrue(optionalPublisher.isEmpty());
    }

    // find titles by publisher id
    @Test
    void shouldReturnAllTitlesByPublisherId() {

        String pubId = testPublisher.getPubId();

        Page<Title> titles = titleRepository.findByPubId(pubId, PageRequest.of(0, 5));

        Assertions.assertNotNull(titles);
        Assertions.assertEquals(1, titles.getNumberOfElements());
        Assertions.assertEquals(testPublisher.getPubId(), titles.getContent().get(0).getPublisher().getPubId());
    }

    @Test
    void shouldReturnAllEmptyTitlesByPublisherId() {

        String pubId = "9910"; // random

        Page<Title> titles = titleRepository.findByPubId(pubId, PageRequest.of(0, 5));

        Assertions.assertNotNull(titles);
        Assertions.assertEquals(0, titles.getNumberOfElements());
    }
}
