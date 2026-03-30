package com.capgemini.book_partner_portal.repository;

import com.capgemini.book_partner_portal.entity.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("local")
public class JobRepositoryTest {

    @Autowired
    private JobRepository jobRepository;

    @Test
    public void testFindAll_ShouldReturnPreloadedJobs() {
        // The jobs table is populated by insertdata.sql
        List<Job> jobs = jobRepository.findAll();

        assertThat(jobs).isNotEmpty();
        // Prove we loaded at least Job ID 1 ("New Hire - Job not specified")
        assertThat(jobs).anyMatch(job -> job.getJobId() == 1);
    }

    @Test
    public void testFindById_WhenValid_ShouldReturnJob() {
        // Job ID 5 is "Publisher" in insertdata.sql
        Optional<Job> job = jobRepository.findById((short) 5);

        assertThat(job).isPresent();
        assertThat(job.get().getJobDesc()).isEqualTo("Publisher");
    }

    @Test
    public void testSave_ShouldPersistNewJobWithAutoIncrementId() {
        Job newJob = new Job();
        newJob.setJobDesc("Security Auditor");
        newJob.setMinLvl(150);
        newJob.setMaxLvl(250);

        Job savedJob = jobRepository.save(newJob);

        // Prove Hibernate auto-generated the ID
        assertThat(savedJob.getJobId()).isNotNull();
        assertThat(savedJob.getJobDesc()).isEqualTo("Security Auditor");
    }
}