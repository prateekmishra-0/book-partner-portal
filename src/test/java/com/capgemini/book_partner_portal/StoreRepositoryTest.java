package com.capgemini.book_partner_portal;

import com.capgemini.book_partner_portal.entity.Store;
import com.capgemini.book_partner_portal.repository.StoreRepository;
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
        // Step 1: Call the Repository method
        List<Store> stores = storeRepository.findAll();

        // Step 2: Use assertFalse to ensure the list is NOT empty
        assertFalse(stores.isEmpty(), "The store list should not be empty");

        // Step 3: Use assertEquals to check the expected count (6 stores in insertdata.sql)
        assertEquals(6, stores.size(), "Total store count should match the inserted data");

        // Step 4: Print all details EXCEPT the ID
        System.out.println("--- Store Master List (Page 2) ---");
        stores.forEach(store -> System.out.println(
                "Name: " + store.getStorName() +
                        " | Address: " + store.getStorAddress() +
                        " | City: " + store.getCity() +
                        " | State: " + store.getState()
        ));
    }
}
