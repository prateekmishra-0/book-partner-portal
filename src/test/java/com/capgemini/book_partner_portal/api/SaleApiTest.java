package com.capgemini.book_partner_portal.api;

import com.capgemini.book_partner_portal.entity.Sale;
import com.capgemini.book_partner_portal.entity.SaleId;
import com.capgemini.book_partner_portal.repository.SaleRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("local")
public class SaleApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SaleRepository saleRepository;

    private SaleId testSaleId;

    @BeforeEach
    void setUp() {
        testSaleId = new SaleId("7131", "ORD-TEST-99", "BU1032");
        Sale testSale = new Sale(testSaleId, null, null, LocalDateTime.now(), (short) 5, "Net 30");
        saleRepository.save(testSale);
    }

    // ------------------- GET APIs & PROJECTION MATH -------------------

    @Test
    void getSalesByStore_ShouldReturnSales_WithCalculatedLineTotal() throws Exception {
        mockMvc.perform(get("/api/sales/search/byStore").param("storId", "7131"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.sales").isNotEmpty())

                // 1. Check if the SpEL flattened the OrdNum properly (from SaleDetailProjection)
                .andExpect(jsonPath("$._embedded.sales[0].ordNum").exists())

                // 2. Check if the dynamic SpEL calculation exists
                .andExpect(jsonPath("$._embedded.sales[0].totalAmount").isNumber());
    }

    @Test
    void getSaleById_UsingCompositeKeyUrl_ShouldReturnOk() throws Exception {
        // Spring Data REST uses comma-separated values for @EmbeddedId
        // Format: /api/sales/{stor_id},{ord_num},{title_id}
        String compositeUrl = "/api/sales/7131,ORD-TEST-99,BU1032";

        mockMvc.perform(get(compositeUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qty").value(5))
                .andExpect(jsonPath("$.payterms").value("Net 30"));
    }

    // ------------------- POST APIs & EVENT GUARDS -------------------

    @Test
    void insertSale_WithValidData_ShouldReturn201() throws Exception {
        String newSaleJson = """
            {
                "id": {
                    "storId": "7131",
                    "ordNum": "NEW-ORD-123",
                    "titleId": "BU1032"
                },
                "ordDate": "2023-11-01T10:00:00",
                "qty": 15,
                "payterms": "Net 60"
            }
            """;

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newSaleJson))
                .andExpect(status().isCreated());
    }

    @Test
    void insertSale_WhenCompositeKeyAlreadyExists_ShouldReturnConflict() throws Exception {
        // GUARD 1 TEST: Proves the Upsert Hack is blocked!
        // We try to POST the exact same ID we created in the setUp() method
        String duplicateSaleJson = """
            {
                "id": {
                    "storId": "7131",
                    "ordNum": "ORD-TEST-99",
                    "titleId": "BU1032"
                },
                "ordDate": "2023-11-01T00:00:00",
                "qty": 50,
                "payterms": "Net 60"
            }
            """;

        mockMvc.perform(post("/api/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateSaleJson))
                .andExpect(status().isConflict()); // HTTP 409 Conflict from SaleEventHandler
    }

    // ------------------- PUT/PATCH APIs & EVENT GUARDS -------------------

    @Test
    void patchSale_WhenGhostInsertAttempted_ShouldReturnNotFound() throws Exception {
        // GUARD 2 TEST: Proves you cannot PUT/PATCH a fake record into existence
        String ghostCompositeUrl = "/api/sales/9999,GHOST-ORD,BU1032";

        String fakeUpdateJson = """
            {
                "qty": 500
            }
            """;

        mockMvc.perform(patch(ghostCompositeUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fakeUpdateJson))
                .andExpect(status().isNotFound()); // HTTP 404 from SaleEventHandler
    }

    // -------------------PROJECTION & SECURITY TESTS ---

    @Test
    void getSaleById_WithSaleDetailProjection_ShouldReturnNestedDataAndHideInternalIds() throws Exception {
        // Goal: Prove that ?projection=saleDetail returns the correct nested data
        // while strictly hiding internal DB IDs and Dev 1's sensitive publisher data.

        // FIX: We query a REAL record from insertdata.sql to ensure the Title relationship is fully loaded by Hibernate
        String compositeUrl = "/api/sales/7131,N914008,PS2091?projection=saleDetail";

        mockMvc.perform(get(compositeUrl))
                .andExpect(status().isOk())
                // 1. Prove nested Title data works (The Firewall)
                .andExpect(jsonPath("$.title.title").exists())
                .andExpect(jsonPath("$.title.price").exists())
                // 2. Prove the SpEL flattened the OrdNum
                .andExpect(jsonPath("$.ordNum").value("N914008")) // Match the real order number!
                // 3. Prove the dynamic math works
                .andExpect(jsonPath("$.totalAmount").isNumber())

                // 4. CRITICAL LEAK CHECK: Ensure internal/raw fields aren't leaking
                // The raw composite key object should not be in the main JSON body
                .andExpect(jsonPath("$.id").doesNotExist())
                // Dev 1's sensitive data (like advances/royalties) must not leak through the TitleView
                .andExpect(jsonPath("$.title.advance").doesNotExist())
                .andExpect(jsonPath("$.title.titleId").doesNotExist());
    }

    @Test
    void putSale_WhenGhostInsertAttempted_ShouldReturnNotFound() throws Exception {
        // Goal: Prove that sending a PUT request to a fake composite ID returns 404
        // instead of accidentally creating a phantom financial record.
        String ghostCompositeUrl = "/api/sales/9999,GHOST-ORD,BU1032";

        String fakePutJson = """
            {
                "ordDate": "2024-01-01T10:00:00",
                "qty": 500,
                "payterms": "Net 30"
            }
            """;

        mockMvc.perform(put(ghostCompositeUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fakePutJson))
                .andExpect(status().isNotFound()); // Validates the SaleEventHandler PUT block
    }
}
