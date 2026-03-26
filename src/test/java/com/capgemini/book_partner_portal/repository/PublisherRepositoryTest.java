package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Publisher;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class PublisherRepositoryTest {

    @Autowired
    private PublisherRepository publisherRepository;



    @Test
    void shouldReturnAllPublishers() {

        List<Publisher> publishers = publisherRepository.findAll();

        Assertions.assertNotNull(publishers);
        Assertions.assertEquals(8, publishers.size());
    }

    @Test
    void shouldReturnPublisherById() {

        // Publisher
        String id = "9999";

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
    void shouldReturnEmptyWhenIdIsInvalid() {
        String id = "1";

        Optional<Publisher> optionalPublisher = publisherRepository.findById(id);

        Assertions.assertTrue(optionalPublisher.isEmpty());
    }


}
