package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Store;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
public class StoreRepositoryTest {
    @Autowired
    private StoreRepository storeRepository;

    @Test
    public void testFindAll_ShouldReturnStoreList() {
        List<Store> stores = storeRepository.findAll();

        //assertFalse to ensure the list is NOT empty
        assertFalse(stores.isEmpty());

        //assertEquals used to check the expected count (6 stores in insertdata.sql)
        assertEquals(6, stores.size());
    }

    @Test
    public void testFindByCity_ShouldReturnSpecificStoreData() {
        String city = "Seattle";
        List<Store> results = storeRepository.findByCity(city);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());

        assertEquals("Eric the Read Books", results.get(0).getStorName());
        assertEquals("788 Catamaugus Ave.", results.get(0).getStorAddress());
    }
}
