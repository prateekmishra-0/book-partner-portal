package com.capgemini.book_partner_portal.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

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
                .andExpect(jsonPath("$._embedded.employees[*].fname").isNotEmpty())
                .andExpect(jsonPath("$._embedded.employees[*].lname").isNotEmpty())
                .andExpect(jsonPath("$._embedded.employees[*].jobLvl").isNotEmpty())

                // CRUCIAL LEAK CHECK: Ensure hidden fields do NOT exist on ANY object in the array
                // If even one employee has a hireDate or pubId, this will fail.
                .andExpect(jsonPath("$._embedded.employees[*].empId").doesNotExist())
                .andExpect(jsonPath("$._embedded.employees[*].hireDate").doesNotExist())
                .andExpect(jsonPath("$._embedded.employees[*].isActive").doesNotExist())
                .andExpect(jsonPath("$._embedded.employees[*].pubId").doesNotExist());
    }

    @Test
    public void testGetEmployees_WithPagination_ShouldReturnPaginatedList() throws Exception {
        // Goal: Call GET /api/employees with pagination parameters
        // Assert exactly 5 records are returned
        // Assert the Spring Data REST pagination metadata matches our exact database count

        mockMvc.perform(get("/api/employees?page=0&size=5"))
                .andExpect(status().isOk())

                // 1. Verify the array exists
                .andExpect(jsonPath("$._embedded.employees").exists())

                // 2. Verify exactly 5 items were returned in the array
                .andExpect(jsonPath("$._embedded.employees.length()", is(5)))

                // 3. Verify the pagination metadata at the bottom of the JSON
                .andExpect(jsonPath("$.page.size", is(5)))
                .andExpect(jsonPath("$.page.totalElements", is(43)))
                .andExpect(jsonPath("$.page.totalPages", is(9)))
                .andExpect(jsonPath("$.page.number", is(0))); // Page numbers are 0-indexed
    }

    @Test
    public void testGetEmployees_LastPage_ShouldReturnRemainingElements() throws Exception {
        // 43 total elements, size 5. Pages 0-7 will have 5 elements.
        // Page 8 (the 9th page) should have exactly 3 elements left.

        mockMvc.perform(get("/api/employees?page=8&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employees.length()", is(3))) // The leftovers!
                .andExpect(jsonPath("$.page.number", is(8)));
    }

    @Test
    public void testGetEmployeeById_WhenValidId_ShouldReturnEmployeeDetails() throws Exception {
        // Goal: Call GET /api/employees/{id} with a known valid ID.
        // Assert HTTP 200.
        // Assert the returned JSON correctly matches the specific employee.

        // PTC11962M is Philip Cramer from your insertdata.sql script
        mockMvc.perform(get("/api/employees/PTC11962M"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fname", is("Philip")))
                .andExpect(jsonPath("$.lname", is("Cramer")))
                .andExpect(jsonPath("$.empId").doesNotExist())
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/employees/PTC11962M")));
    }

    @Test
    public void testGetEmployeeById_WhenInvalidId_ShouldReturn404() throws Exception {
        // Goal: Call GET /api/employees/{id} with a totally fake ID.
        // Assert HTTP 404 Not Found to prove the API fails gracefully.

        mockMvc.perform(get("/api/employees/FAKE999M"))
                .andExpect(status().isNotFound());
    }

    // --- 1. First Name Search API Tests ---
    @Test
    public void testSearchByFnameApi_WhenValid_ShouldReturnResults() throws Exception {
        // Search for "phil" (Philip Cramer from insertdata.sql)
        mockMvc.perform(get("/api/employees/search/fname")
                        .param("fname", "phil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employees").exists())
                .andExpect(jsonPath("$._embedded.employees[*].fname", hasItem("Philip")));
    }

    @Test
    public void testSearchByFnameApi_WhenInvalid_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/employees/search/fname")
                        .param("fname", "Zack"))
                .andExpect(status().isOk())
                // FIXED: Check that the array is empty [], not non-existent
                .andExpect(jsonPath("$._embedded.employees").isEmpty());
    }

    // --- 2. Last Name Search API Tests ---
    @Test
    public void testSearchByLnameApi_WhenValid_ShouldReturnResults() throws Exception {
        // Search for "cram" (Philip Cramer from insertdata.sql)
        mockMvc.perform(get("/api/employees/search/lname")
                        .param("lname", "cram"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employees").exists())
                .andExpect(jsonPath("$._embedded.employees[*].lname", hasItem("Cramer")));
    }

    @Test
    public void testSearchByLnameApi_WhenInvalid_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/employees/search/lname")
                        .param("lname", "Zebra"))
                .andExpect(status().isOk())
                // FIXED: .isEmpty()
                .andExpect(jsonPath("$._embedded.employees").isEmpty());
    }

    // --- 3. Job Level Greater Than API Tests ---
    @Test
    public void testSearchByJobLvlGtApi_WhenValid_ShouldReturnResults() throws Exception {
        mockMvc.perform(get("/api/employees/search/joblevel-gt")
                        .param("level", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employees").exists())
                .andExpect(jsonPath("$._embedded.employees[*].jobLvl", everyItem(greaterThan(200))));
    }

    @Test
    public void testSearchByJobLvlGtApi_WhenInvalid_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/employees/search/joblevel-gt")
                        .param("level", "250"))
                .andExpect(status().isOk())
                // FIXED: .isEmpty()
                .andExpect(jsonPath("$._embedded.employees").isEmpty());
    }

    // --- 4. Job Level Less Than API Tests ---
    @Test
    public void testSearchByJobLvlLtApi_WhenValid_ShouldReturnResults() throws Exception {
        mockMvc.perform(get("/api/employees/search/joblevel-lt")
                        .param("level", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.employees").exists())
                .andExpect(jsonPath("$._embedded.employees[*].jobLvl", everyItem(lessThan(50))));
    }

    @Test
    public void testSearchByJobLvlLtApi_WhenInvalid_ShouldReturnEmpty() throws Exception {
        mockMvc.perform(get("/api/employees/search/joblevel-lt")
                        .param("level", "5"))
                .andExpect(status().isOk())
                // FIXED: .isEmpty()
                .andExpect(jsonPath("$._embedded.employees").isEmpty());
    }

    // --- 1. POST: The Full Form Test ---
    @Test
    public void testAddEmployee_WithAllFields_ShouldReturnCreated() throws Exception {
        String newEmployeeJson = """
                {
                    "empId": "ZZZ99999M",
                    "fname": "Full",
                    "lname": "Tester",
                    "jobLvl": 200,
                    "pubId": "1389",
                    "hireDate": "2026-03-26"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmployeeJson))
                .andExpect(status().isCreated())
                // FIXED: Check HATEOAS link instead of raw ID
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/employees/ZZZ99999M")))
                .andExpect(jsonPath("$.pubId", is("1389")));
    }

    // --- 2. POST: The Partial Form Test (Defaults) ---
    @Test
    public void testAddEmployee_WithOnlyMandatoryFields_ShouldReturnCreated() throws Exception {
        String newEmployeeJson = """
                {
                    "empId": "YYY88888F",
                    "fname": "Partial",
                    "lname": "Tester",
                    "jobLvl": 150
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmployeeJson))
                .andExpect(status().isCreated())
                // FIXED: Check HATEOAS link instead of raw ID
                .andExpect(jsonPath("$._links.self.href", endsWith("/api/employees/YYY88888F")))
                .andExpect(jsonPath("$.fname", is("Partial")));
    }

    // --- 3. POST: The Validation Failure Test ---
    @Test
    public void testAddEmployee_WithInvalidId_ShouldReturnBadRequest() throws Exception {
        String badEmployeeJson = """
                {
                    "empId": "INVALID123",
                    "fname": "Hacker",
                    "lname": "Tester",
                    "jobLvl": 100
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badEmployeeJson))
                .andExpect(status().isBadRequest()); // Expecting 400 Bad Request
    }

    // --- 4. POST: The Missing Mandatory Fields Test ---
    @Test
    public void testAddEmployee_WithMissingMandatoryFields_ShouldReturnBadRequest() throws Exception {
        // Goal: User tries to submit the form without a First Name and without a Job Level.
        // The @NotBlank on fname and @NotNull on jobLvl must catch this.
        String incompleteEmployeeJson = """
                {
                    "empId": "ZZZ11111M",
                    "fname": "", 
                    "lname": "Tester"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incompleteEmployeeJson))
                .andExpect(status().isBadRequest()); // Expecting 400 Bad Request
    }


    // --- 1. PUT: The Full Replacement Test ---
    @Test
    public void testUpdateEmployee_WithPut_ShouldReplaceData() throws Exception {
        // Goal: The frontend sends a full object to overwrite Philip Cramer's mutable fields.
        // We are changing his last name to "Smith" and his job level to 225.
        String updatedEmployeeJson = """
                {
                    "fname": "Philip",
                    "lname": "Smith",
                    "jobLvl": 225,
                    "pubId": "9952"
                }
                """;

        mockMvc.perform(put("/api/employees/PTC11962M")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedEmployeeJson))
                .andExpect(status().isOk()) // REST Standard: 200 OK for successful update
                .andExpect(jsonPath("$.lname", is("Smith")))
                .andExpect(jsonPath("$.jobLvl", is(225)));
    }

    // --- 2. PATCH: The Partial Update Test ---
    @Test
    public void testPartialUpdateEmployee_WithPatch_ShouldUpdateSingleField() throws Exception {
        // Goal: The frontend only sends a single changed field (Job Level).
        // We must prove that Spring Data REST updates the Job Level WITHOUT erasing his First/Last name.
        String patchJson = """
                {
                    "jobLvl": 250
                }
                """;

        mockMvc.perform(patch("/api/employees/PTC11962M")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobLvl", is(250)))
                // CRUCIAL: Prove the first name survived the partial update!
                .andExpect(jsonPath("$.fname", is("Philip")));
    }

    // --- 3. PATCH: The Validation Failure Test ---
    @Test
    public void testUpdateEmployee_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Goal: A hacker tries to erase Philip's first name by sending a blank string.
        // Our RestValidationConfig attached to the "beforeSave" event must catch this.
        String badPatchJson = """
                {
                    "fname": ""
                }
                """;

        mockMvc.perform(patch("/api/employees/PTC11962M")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badPatchJson))
                .andExpect(status().isBadRequest()); // Expecting 400 Bad Request
    }

    // --- DELETE: The Soft Delete Test ---
    @Test
    public void testDeleteEmployee_ShouldSoftDeleteAndReturnNotFound() throws Exception {
        // STEP 1: The Deletion Strike
        // We send a DELETE request for an existing employee.
        // Spring Data REST should return a 204 No Content on a successful delete.
        mockMvc.perform(delete("/api/employees/AMD15433F"))
                .andExpect(status().isNoContent());

        // STEP 2: The Ghost Verification
        // We immediately try to GET that exact same employee.
        // Because @SQLRestriction("is_active = true") is on the entity,
        // Hibernate will hide the record and return a 404 Not Found, proving the soft delete worked!
        mockMvc.perform(get("/api/employees/AMD15433F"))
                .andExpect(status().isNotFound());
    }

    // --- 5. POST: The Upsert Vulnerability Defense ---
    @Test
    public void testAddEmployee_WhenIdAlreadyExists_ShouldReturnConflict() throws Exception {
        // Goal: Prove that POSTing an existing ID throws a 409 Conflict and doesn't overwrite the original data.

        // PTC11962M belongs to Philip Cramer
        String duplicateEmployeeJson = """
                {
                    "empId": "PTC11962M",
                    "fname": "Hacker",
                    "lname": "Man",
                    "jobLvl": 200
                }
                """;

        // 1. Send the malicious POST request
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateEmployeeJson))
                .andExpect(status().isConflict()); // HTTP 409 Conflict from our Event Handler

        // 2. Verify Philip Cramer's data survived the attack
        mockMvc.perform(get("/api/employees/PTC11962M"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fname", is("Philip"))) // His name is still Philip
                .andExpect(jsonPath("$.lname", is("Cramer")));
    }

    // --- 6. POST: The Invisible Shield (isActive) Defense ---
    @Test
    public void testAddEmployee_WithIsActiveFalse_ShouldIgnoreAndSetTrue() throws Exception {
        // Goal: Prove that the @JsonIgnore annotation stops users from manipulating the isActive flag.

        String maliciousJson = """
                {
                    "empId": "HAK99999M",
                    "fname": "Sneaky",
                    "lname": "Hacker",
                    "jobLvl": 150,
                    "isActive": false 
                }
                """;

        // 1. The creation will succeed (201 Created) because the ID is new
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(maliciousJson))
                .andExpect(status().isCreated());

        // 2. The Verification Magic
        // If the hacker successfully set isActive=false, the @SQLRestriction would hide the record,
        // and this GET request would return a 404 Not Found.
        // If it returns 200 OK, it proves the parser ignored the hacker's false flag and used our default 'true'!
        mockMvc.perform(get("/api/employees/HAK99999M"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fname", is("Sneaky")));
    }

    // --- 7. PUT: The Phantom Insert Defense ---
    @Test
    public void testUpdateEmployee_WhenIdDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Goal: Prove that sending a PUT request to a non-existent ID throws a 404 Not Found
        // instead of accidentally creating a new employee.

        String fakeEmployeeJson = """
                {
                    "fname": "Phantom",
                    "lname": "Insert",
                    "jobLvl": 200,
                    "pubId": "1389"
                }
                """;

        mockMvc.perform(put("/api/employees/FAKE999M")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fakeEmployeeJson))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found from our Event Handler!
    }


    // =================================================================
    // --- Phase 4: The Page 3 Detailed Projection Test ---
    // =================================================================

    @Test
    public void testGetEmployeeDetail_WithProjection_ShouldReturnNestedData() throws Exception {
        // Goal: Call GET /api/employees/{id}?projection=employeeDetail
        // Prove that Spring Data REST successfully executes the JOINs and formats the JSON
        // exactly as defined in the EmployeeDetailProjection interface.

        mockMvc.perform(get("/api/employees/PTC11962M?projection=employeeDetail"))
                .andExpect(status().isOk())

                // 1. Verify standard employee fields
                .andExpect(jsonPath("$.fname", is("Philip")))
                .andExpect(jsonPath("$.lname", is("Cramer")))
                .andExpect(jsonPath("$.jobLvl", is(215)))

                // 2. Verify Nested Job View (The JOIN worked!)
                .andExpect(jsonPath("$.job").exists())
                .andExpect(jsonPath("$.job.jobDesc", is("Chief Executive Officer")))

                // 3. Verify Nested Publisher View (The JOIN worked!)
                .andExpect(jsonPath("$.publisher").exists())
                .andExpect(jsonPath("$.publisher.pubName", is("Scootney Books")))

                // 4. THE INVISIBLE SHIELD: Prove the projection hides internal fields
                .andExpect(jsonPath("$.empId").doesNotExist())     // Base Entity hidden
                .andExpect(jsonPath("$.isActive").doesNotExist())  // Base Entity hidden
                .andExpect(jsonPath("$.job.minLvl").doesNotExist()) // Nested Job Entity hidden
                .andExpect(jsonPath("$.publisher.city").doesNotExist()); // Nested Publisher Entity hidden
    }

}