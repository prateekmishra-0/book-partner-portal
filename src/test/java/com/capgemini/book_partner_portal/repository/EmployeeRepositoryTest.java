package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Employee;
import com.capgemini.book_partner_portal.entity.Job;
import com.capgemini.book_partner_portal.entity.Publisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    public void setUp() {
        // Using strict schema data:
        // 1. emp_id matches the CHECK constraint (3 letters, digit 1-9, 4 digits, F/M)
        // 2. pub_id uses actual IDs from the publishers table ("0877" and "1389")
        employeeRepository.save(new Employee("ABC12345M", "Clark", "Kent", LocalDate.now(), "0877", 10, true));
        employeeRepository.save(new Employee("XYZ98765F", "Bruce", "Wayne", LocalDate.now(), "1389", 10, true));

        // CRITICAL FIX: Force Hibernate to sync with the database and wipe its cache.
        // This guarantees the tests fetch fresh objects with fully loaded relationships!
        entityManager.flush();
        entityManager.clear();
    }

    // STRICTLY testing only the findAll() method currently used by the API
    @Test
    public void testFindAll_ShouldReturnSavedEmployees() {
        List<Employee> employees = employeeRepository.findAll();

        assertThat(employees).isNotEmpty();

        // Verify our specific test data was successfully mapped and retrieved
        assertThat(employees).anyMatch(e->e.getEmpId().equals("ABC12345M"));
        assertThat(employees).anyMatch(e->e.getEmpId().equals("XYZ98765F"));
    }

    // --- 1. First Name Search Tests ---
    @Test
    public void testFindByFname_WhenValid_ShouldReturnMatch() {
        // "Clark" was saved in the @BeforeEach setup
        List<Employee> results = employeeRepository.findByFnameContainingIgnoreCase("cla");
        assertThat(results).isNotEmpty();
        // Dynamic check: We don't care about the order, just prove Clark is in the results
        assertThat(results).anyMatch(e -> e.getFname().equalsIgnoreCase("Clark"));
    }

    @Test
    public void testFindByFname_WhenInvalid_ShouldReturnEmptyList() {
        List<Employee> results = employeeRepository.findByFnameContainingIgnoreCase("Zack");
        assertThat(results).isEmpty();
    }

    // --- 2. Last Name Search Tests ---
    @Test
    public void testFindByLname_WhenValid_ShouldReturnMatch() {
        // "Wayne" was saved in the @BeforeEach setup
        List<Employee> results = employeeRepository.findByLnameContainingIgnoreCase("way");
        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(e -> e.getLname().equalsIgnoreCase("Wayne"));
    }

    @Test
    public void testFindByLname_WhenInvalid_ShouldReturnEmptyList() {
        List<Employee> results = employeeRepository.findByLnameContainingIgnoreCase("Zebra");
        assertThat(results).isEmpty();
    }

    // --- 3. Job Level Greater Than Tests ---
    @Test
    public void testFindByJobLvlGreaterThan_WhenValid_ShouldReturnMatches() {
        // Search for > 200. There are several in insertdata.sql (e.g. Philip Cramer is 215)
        List<Employee> results = employeeRepository.findByJobLvlGreaterThan(200);
        assertThat(results).isNotEmpty();

        // Dynamic check: Prove that EVERY single employee returned actually has > 200
        assertThat(results).allMatch(e -> e.getJobLvl() > 200);
    }

    @Test
    public void testFindByJobLvlGreaterThan_WhenInvalid_ShouldReturnEmptyList() {
        // The max_lvl in the 'jobs' table is strictly capped at 250. This must return 0.
        List<Employee> results = employeeRepository.findByJobLvlGreaterThan(250);
        assertThat(results).isEmpty();
    }

    // --- 4. Job Level Less Than Tests ---
    @Test
    public void testFindByJobLvlLessThan_WhenValid_ShouldReturnMatches() {
        // Search for < 50. Helen Bennett has 35, etc.
        List<Employee> results = employeeRepository.findByJobLvlLessThan(50);
        assertThat(results).isNotEmpty();

        // Dynamic check: Prove that EVERY single employee returned actually has < 50
        assertThat(results).allMatch(e -> e.getJobLvl() < 50);
    }

    @Test
    public void testFindByJobLvlLessThan_WhenInvalid_ShouldReturnEmptyList() {
        // The min_lvl in the 'jobs' table is strictly 10. This must return 0.
        List<Employee> results = employeeRepository.findByJobLvlLessThan(5);
        assertThat(results).isEmpty();
    }

    // =================================================================
    // --- Phase 3: Relationship Mapping Tests ---
    // =================================================================

    @Test
    public void testEmployeeToJobRelationship_ShouldLoadJobData() {
        // Goal: Prove that fetching an employee correctly lazy-loads their Job entity.

        // Fetch Clark Kent (ABC12345M) saved in the @BeforeEach block
        Optional<Employee> employee = employeeRepository.findById("ABC12345M");
        assertThat(employee).isPresent();

        // Because of FetchType.LAZY, calling .getJob() inside this @Transactional test
        // will trigger Hibernate to fetch the Job record.
        Job linkedJob = employee.get().getJob();

        assertThat(linkedJob).isNotNull();
        // Since we didn't specify a jobId in the custom constructor, it defaults to 1.
        // Job ID 1 is "New Hire - Job not specified" in your insertdata.sql
        assertThat(linkedJob.getJobDesc()).isEqualTo("New Hire - Job not specified");
    }

    @Test
    public void testEmployeeToPublisherRelationship_ShouldLoadPublisherData() {
        // Goal: Prove that fetching an employee correctly lazy-loads their Publisher entity.

        // Fetch Bruce Wayne (XYZ98765F) who was explicitly assigned pubId "1389" in @BeforeEach
        Optional<Employee> employee = employeeRepository.findById("XYZ98765F");
        assertThat(employee).isPresent();

        Publisher linkedPublisher = employee.get().getPublisher();

        assertThat(linkedPublisher).isNotNull();
        // pubId "1389" is strictly tied to "Algodata Infosystems" in insertdata.sql
        assertThat(linkedPublisher.getPubName()).isEqualTo("Algodata Infosystems");
    }

}