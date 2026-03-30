package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat; // AssertJ for better readability

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
public class StoreRepositoryTest {

    @Autowired
    private StoreRepository storeRepository;

    @BeforeEach
    public void setUp() {
        storeRepository.save(new Store("9901", "Barnum's", "567 Pasadena", "Tustin", "CA", "92789", true));
        storeRepository.save(new Store("9902", "Eric the Read", "123 Main", "Seattle", "WA", "98101", true));
        storeRepository.save(new Store("9903", "Nagpur Books", "Civil Lines", "Nagpur", "MH", "44001", true));
    }

    // --- READ Tests ---

    @Test
    void testFindAll_ShouldReturnSavedStores() {
        List<Store> stores = storeRepository.findAll();
        assertThat(stores).isNotEmpty();
        assertThat(stores).anyMatch(s -> s.getStorId().equals("9901"));
    }

    @Test
    public void testFindById_WithValidId_ShouldReturnStore() {
        Optional<Store> store = storeRepository.findById("9901");
        assertThat(store).isPresent();
        assertThat(store.get().getStorName()).isEqualTo("Barnum's");
    }

    @Test
    public void testFindById_WithInvalidId_ShouldReturnEmpty() {
        Optional<Store> store = storeRepository.findById("9999");
        assertThat(store).isEmpty();
    }

    // --- SEARCH Tests ---

    @Test
    public void testFindByCity_WhenValid_ShouldReturnMatches() {
        List<Store> results = storeRepository.findByCityIgnoreCase("seattle");
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(s -> s.getCity().equalsIgnoreCase("Seattle"));
    }

    @Test
    public void testFindByCity_WhenCityNotExists_ShouldReturnEmptyList() {
        List<Store> results = storeRepository.findByCityIgnoreCase("Gondia");
        assertThat(results).isEmpty();
    }

    @Test
    public void testFindByState_WhenValid_ShouldReturnStores() {
        List<Store> results = storeRepository.findByStateIgnoreCase("CA");
        assertThat(results).isNotEmpty();
        assertThat(results).allMatch(s -> s.getState().equalsIgnoreCase("CA"));
    }

    @Test
    public void testFindByState_WhenStateNotExists_ShouldReturnEmptyList() {
        List<Store> results = storeRepository.findByStateIgnoreCase("NY");
        assertThat(results).isEmpty();
    }

    @Test
    public void testSearchByName_ShouldFindPartialMatch() {
        List<Store> results = storeRepository.findByStorNameContainingIgnoreCase("Barnum");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getStorName()).contains("Barnum");
    }

    // --- CUD (Create, Update, Delete) Tests ---

    @Test
    void testSave_WhenValidStore_ShouldPersistInDb() {
        Store newStore = new Store("9920", "Test Store", "123 Road", "Nagpur", "MH", "44001", true);
        storeRepository.save(newStore);

        Optional<Store> found = storeRepository.findById("9920");
        assertThat(found).isPresent();
        assertThat(found.get().getStorName()).isEqualTo("Test Store");
    }

    @Test
    void testUpdate_ShouldChangeData() {
        Store store = storeRepository.findById("9901").get();
        store.setStorName("Updated Name");
        storeRepository.save(store);

        Optional<Store> updated = storeRepository.findById("9901");
        assertThat(updated).isPresent();
        assertThat(updated.get().getStorName()).isEqualTo("Updated Name");
    }

    @Test
    void testDeleteStore_ShouldPerformSoftDelete_AndHideFromRepository() {
        String idToDelete = "9901";

        // Before delete confirm it's there
        assertThat(storeRepository.findById(idToDelete)).isPresent();

        storeRepository.deleteById(idToDelete);

        Optional<Store> deletedStore = storeRepository.findById(idToDelete);
        assertThat(deletedStore).isEmpty();
    }



}