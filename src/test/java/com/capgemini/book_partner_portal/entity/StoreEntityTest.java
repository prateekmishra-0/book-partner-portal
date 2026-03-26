package com.capgemini.book_partner_portal.entity;

import com.capgemini.book_partner_portal.repository.StoreRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StoreEntityTest {

    @Autowired
    private StoreRepository storeRepository;

    private Store validStore;

    @BeforeEach
    public void setUp() {
        validStore = new Store();
        validStore.setStorId("9999");
        validStore.setStorName("Nagpur Book Center");
        validStore.setStorAddress("Dharampeth Main Road");
        validStore.setCity("Nagpur");
        validStore.setState("MH");
        validStore.setZip("44010");
    }

    // NULL PK
    @Test
    public void testStorId_Null_ShouldFail() {
        validStore.setStorId(null);
        assertThrows(Exception.class, () -> storeRepository.saveAndFlush(validStore));
    }

    // NAME LENGTH (VARCHAR 40)
    @Test
    public void testStorName_TooLong_ShouldFail() {
        // String with 41 characters
        validStore.setStorName("A".repeat(41));
        assertThrows(Exception.class, () -> storeRepository.saveAndFlush(validStore));
    }

    // STATE FORMAT (CHAR 2)
    @Test
    public void testState_TooLong_ShouldFail() {
        // State column is CHAR(2), so 3 chars should fail
        validStore.setState("MHR");
        assertThrows(Exception.class, () -> storeRepository.saveAndFlush(validStore));
    }

    // ZIP FORMAT (CHAR 5)
    @Test
    public void testZip_TooLong_ShouldFail() {
        // Zip is CHAR(5), so 6 chars should fail
        validStore.setZip("440010");
        assertThrows(Exception.class, () -> storeRepository.saveAndFlush(validStore));
    }

    // --- SUCCESS CASE: All Valid ---
    @Test
    public void testValidStore_ShouldSaveSuccessfully() {
        Store saved = storeRepository.saveAndFlush(validStore);
        assertNotNull(saved.getStorId());
        assertEquals("Nagpur", saved.getCity());
    }
}
