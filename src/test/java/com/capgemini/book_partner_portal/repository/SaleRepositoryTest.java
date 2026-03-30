package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Sale;
import com.capgemini.book_partner_portal.entity.SaleId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
public class SaleRepositoryTest {

    @Autowired
    private SaleRepository saleRepository;

    private SaleId testSaleId1;
    private SaleId testSaleId2;

    @BeforeEach
    public void setUp() {
        // Create composite keys
        testSaleId1 = new SaleId("7131", "ORD-TEST-01", "BU1032");
        testSaleId2 = new SaleId("7131", "ORD-TEST-02", "PS2091");

        // Save mock sales (This is the only "Create" allowed, just to set up the DB for reading!)
        saleRepository.save(new Sale(testSaleId1, null, null, LocalDateTime.now(), (short) 5, "Net 30"));
        saleRepository.save(new Sale(testSaleId2, null, null, LocalDateTime.now(), (short) 10, "Net 60"));
    }

    // --- READ Tests ---

    @Test
    public void testFindById_WithCompositeKey_ShouldReturnSale() {
        Optional<Sale> sale = saleRepository.findById(testSaleId1);

        assertThat(sale).isPresent();
        assertThat(sale.get().getQty()).isEqualTo((short) 5);
        assertThat(sale.get().getPayterms()).isEqualTo("Net 30");
    }

    @Test
    public void testFindById_WithInvalidCompositeKey_ShouldReturnEmpty() {
        SaleId fakeId = new SaleId("9999", "GHOST-ORD", "NO-TITLE");
        Optional<Sale> sale = saleRepository.findById(fakeId);

        assertThat(sale).isEmpty();
    }

    // --- SEARCH Tests (Page 3 Requirement) ---

    @Test
    public void testFindById_StorId_ShouldReturnAllSalesForStore() {
        // This tests the exact method Dev 3's Page 3 relies on!
        List<Sale> storeSales = saleRepository.findById_StorId("7131");

        assertThat(storeSales).isNotEmpty();
        // Both setup records belong to store 7131
        assertThat(storeSales.size()).isGreaterThanOrEqualTo(2);
        assertThat(storeSales).allMatch(s -> s.getId().getStorId().equals("7131"));
    }

    @Test
    public void testFindById_StorId_WithUnknownStore_ShouldReturnEmptyList() {
        List<Sale> storeSales = saleRepository.findById_StorId("0000");
        assertThat(storeSales).isEmpty();
    }
}
