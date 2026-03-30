package com.capgemini.book_partner_portal.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("local")
public class JobApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // --- 1. GET Tests ---
    @Test
    public void testGetAllJobs_ShouldReturnLookupList() throws Exception {
        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.jobs").exists())
                .andExpect(jsonPath("$._embedded.jobs").isNotEmpty());
    }

    @Test
    public void testGetJobById_WhenValid_ShouldReturnJob() throws Exception {
        // Job ID 5 is 'Publisher' in the standard dataset
        mockMvc.perform(get("/api/jobs/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobDesc", is("Publisher")));
    }

    // --- 2. POST Tests ---
    @Test
    public void testCreateJob_WithValidData_ShouldReturnCreated() throws Exception {
        // Correct behavior: Omitting the ID so the database auto-increments it
        String newJobJson = """
                {
                    "jobDesc": "Quality Assurance",
                    "minLvl": 50,
                    "maxLvl": 150
                }
                """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newJobJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobDesc", is("Quality Assurance")));
    }

    @Test
    public void testCreateJob_WhenIdAlreadyExists_ShouldReturnConflict() throws Exception {
        // Attempting the Upsert Hack: Passing an existing ID (1) in the POST body
        String hackerJson = """
                {
                    "jobId": 1,
                    "jobDesc": "Hacker Overwrite",
                    "minLvl": 10,
                    "maxLvl": 10
                }
                """;

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hackerJson))
                .andExpect(status().isConflict()); // HTTP 409 Conflict from JobEventHandler
    }

    // --- 3. PUT/PATCH Tests ---
    @Test
    public void testUpdateJob_WhenIdDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Attempting the Phantom Insert Hack: PUTting to a ghost ID
        String ghostJson = """
                {
                    "jobDesc": "Ghost Position",
                    "minLvl": 50,
                    "maxLvl": 100
                }
                """;

        mockMvc.perform(put("/api/jobs/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ghostJson))
                .andExpect(status().isNotFound()); // HTTP 404 Not Found from JobEventHandler
    }
}