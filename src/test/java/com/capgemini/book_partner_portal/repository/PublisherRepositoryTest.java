package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Publisher;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class PublisherRepositoryTest {

    @Autowired
    private PublisherRepository publisherRepository;


    private Publisher testPublisher;

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

        List<Publisher> publishers = publisherRepository.findByPubNameContainingIgnoreCase(pubName.substring(0, pubName.length() - 2));
        assertThat(publishers).isNotEmpty();
        for (Publisher publisher : publishers) {
            Assertions.assertTrue(publisher.getPubName().contains(pubName));
        }
    }


    @Test
    void shouldReturnEmptyPublisherListByName() {
        String pubName = "random";

        List<Publisher> publishers = publisherRepository.findByPubNameContainingIgnoreCase(pubName.toLowerCase());

        Assertions.assertEquals(0, publishers.size());
    }


    @Test
    void shouldReturnPublisherListByCity() {
        String city = testPublisher.getCity();

        List<Publisher> publishers = publisherRepository.findByCityContainingIgnoreCase(city);
        assertThat(publishers).isNotEmpty();
        for (Publisher publisher : publishers) {
            Assertions.assertTrue(publisher.getCity().contains(city));
        }
    }


    @Test
    void shouldReturnEmptyPublisherListByCity() {
        String city = "random";

        List<Publisher> publishers = publisherRepository.findByCityContainingIgnoreCase(city.toLowerCase());

        Assertions.assertEquals(0, publishers.size());
    }


    @Test
    void shouldReturnPublisherListByState() {
        String state = testPublisher.getState();

        List<Publisher> publishers = publisherRepository.findByStateContainingIgnoreCase(state.toLowerCase());
        assertThat(publishers).isNotEmpty();
        for (Publisher publisher : publishers) {
            Assertions.assertTrue(publisher.getState().contains(state));
        }
    }

    @Test
    void shouldReturnEmptyPublisherListByState() {
        String state = "random";

        List<Publisher> publishers = publisherRepository.findByStateContainingIgnoreCase(state);

        Assertions.assertEquals(0, publishers.size());
    }

    @Test
    void shouldReturnPublisherListByCountry() {
        String country = testPublisher.getCountry();

        List<Publisher> publishers = publisherRepository.findByCountryContainingIgnoreCase(country.toLowerCase());
        Assertions.assertNotNull(publishers);
        Assertions.assertFalse(publishers.isEmpty());
        for (Publisher publisher : publishers) {
            Assertions.assertTrue(publisher.getCountry().contains(country));
        }
    }

    @Test
    void shouldReturnEmptyPublisherListByCountry() {
        String country = "random";

        List<Publisher> publishers = publisherRepository.findByCountryContainingIgnoreCase(country);
        Assertions.assertNotNull(publishers);
        Assertions.assertEquals(0, publishers.size());
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
     * TEST: Ensure default value is applied
     * Verifies that when a Publisher is saved without a country,
     * it defaults to "USA".
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
     * TEST: Verify partial update logic
     * Simulates the behavior of a PATCH request by retrieving,
     * modifying one field, and re-saving.
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
     * TEST: Invalid ID pattern (Constraint Violation)
     * Checks that an ID like '1234' (which fails the regex) triggers an exception.
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
     * TEST: @NotBlank constraint
     * Verifies that the repository won't allow saving a publisher without a name.
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
}
