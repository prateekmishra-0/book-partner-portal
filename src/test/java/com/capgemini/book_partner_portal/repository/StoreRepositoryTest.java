package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Store;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class StoreRepositoryTest {
    @Autowired
    private StoreRepository storeRepository;

    // find all stores
    @Test
    void findAll_WhenStoresExist_ShouldReturnNonEmptyList(){
        List<Store> stores = storeRepository.findAll();
        assertFalse(stores.isEmpty());
    }

    // find store with valid id
    @Test
    public void testFindById_WithValidId_ShouldReturnStore() {
        Optional<Store> store = storeRepository.findById("7066");
        assertTrue(store.isPresent());
        assertEquals("Barnum's", store.get().getStorName());
    }

    // find store with invalid id
    @Test
    public void testFindById_WithInvalidId_ShouldReturnEmpty() {
        Optional<Store> store = storeRepository.findById("9999");
        assertTrue(store.isEmpty());
    }

    // find store with valid city
    @Test
    public void testFindByCity_ShouldReturnSpecificStoreData() {
        String city = "Seattle";
        List<Store> results = storeRepository.findByCityIgnoreCase(city);

        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(s -> s.getStorName().equals("Eric the Read Books")));
    }

    // find store with valid state
    @Test
    public void testFindByState_ShouldReturnStores() {
        List<Store> results = storeRepository.findByStateIgnoreCase("CA");

        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(s -> s.getState().equalsIgnoreCase("CA")));
    }

    // find store with partial name match
    @Test
    public void testSearchByName_ShouldFindPartialMatch() {
        List<Store> results = storeRepository.findByStorNameContainingIgnoreCase("Barnum");

        assertFalse(results.isEmpty());
        assertEquals("Barnum's", results.get(0).getStorName());
    }

    // find store when city not exists
    @Test
    public void testFindByCity_WhenCityNotExists_ShouldReturnEmptyList() {
        List<Store> results = storeRepository.findByCityIgnoreCase("Gondia");
        assertTrue(results.isEmpty());
    }

    // find store when state not exists
    @Test
    public void testFindByState_WhenStateNotExists_ShouldReturnEmptyList() {
        List<Store> results = storeRepository.findByStateIgnoreCase("NY");
        assertTrue(results.isEmpty());
    }


    //---------POST,PUT test

    // Test: Create/Insert Store
    @Test
    void save_WhenValidStore_ShouldPersistInDb() {
        // Constructor use karne ke liye AllArgsConstructor annotation zaruri hai
        Store newStore = new Store("9920", "Test Store", "123 Road", "Nagpur", "MH", "44001");

        storeRepository.save(newStore);

        Optional<Store> found = storeRepository.findById("9920");

        assertTrue(found.isPresent());
        if(found.isPresent()) {
            assertEquals("Test Store", found.get().getStorName());
        }
    }

    // Test: Update Store
    @Test
    void save_WhenUpdateExistingStore_ShouldChangeData() {
        Optional<Store> storeOpt = storeRepository.findById("7066");
        Store store = storeOpt.get();

        store.setStorName("Updated Name");
        storeRepository.save(store);

        assertEquals("Updated Name", storeRepository.findById("7066").get().getStorName());
    }
}
