package com.capgemini.book_partner_portal.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class EmployeeApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllEmployees_ShouldApplyProjectionMask() throws Exception {
        // Goal: Call GET /api/employees.
        // Assert HTTP 200.
        // Assert ALL employees have the public fields.
        // Assert NO employees have the hidden fields.

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                // Verify Spring Data REST HATEOAS structure exists
                .andExpect(jsonPath("$._embedded.employees").exists())

                // STRICT VERIFICATION: Ensure public fields exist for EVERY object in the array
                // The [*] wildcard creates a list of all values. .isNotEmpty() ensures the list has data.
                .andExpect(jsonPath("$._embedded.employees[*].empId").isNotEmpty())
                .andExpect(jsonPath("$._embedded.employees[*].fname").isNotEmpty())
                .andExpect(jsonPath("$._embedded.employees[*].lname").isNotEmpty())
                .andExpect(jsonPath("$._embedded.employees[*].jobLvl").isNotEmpty())

                // CRUCIAL LEAK CHECK: Ensure hidden fields do NOT exist on ANY object in the array
                // If even one employee has a hireDate or pubId, this will fail.
                .andExpect(jsonPath("$._embedded.employees[*].hireDate").doesNotExist())
                .andExpect(jsonPath("$._embedded.employees[*].pubId").doesNotExist());
    }
}