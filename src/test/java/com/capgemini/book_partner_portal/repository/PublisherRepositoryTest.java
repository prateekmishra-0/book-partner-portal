package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Publisher;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

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
    void shouldReturnPublisherByName() {
        String pubName = testPublisher.getPubName();

        Optional<Publisher> optionalPublisher = publisherRepository.findByPubNameIgnoreCase(pubName);

        Assertions.assertTrue(optionalPublisher.isPresent());
        Assertions.assertEquals(testPublisher.getPubName(), optionalPublisher.get().getPubName());
    }


    @Test
    void shouldReturnEmptyWhenIdDoesNotExist() {

        String id = "9998";

        Optional<Publisher> optionalPublisher = publisherRepository.findById(id);

        Assertions.assertTrue(optionalPublisher.isEmpty());
    }


}
