package com.flex.dhp.careplanservice;

import com.flex.dhp.common.model.*;
import com.flex.dhp.common.repository.AssessmentRepository;
import com.flex.dhp.common.repository.CareplanRepository;
import com.flex.dhp.common.repository.InterventionRepository;
import com.flex.dhp.common.repository.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;

@Configuration
@ComponentScan(basePackages = "com.flex.dhp")
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "com.flex.dhp")
@EntityScan(basePackages = "com.flex.dhp")
@SpringBootApplication(scanBasePackages = "com.flex.dhp")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(PatientRepository patientRepository,
                           CareplanRepository careplanRepository,
                           AssessmentRepository assessmentRepository,
                           InterventionRepository interventionRepository) {
        return (evt) -> Arrays.asList(
                "Joe:Smith,Jan:Jones,Bob:Adams,Janet:Lee,Richard:Rho,Don:Fisher,Shirley:Pollack,Susan:Long".split(","))
                .forEach(
                        a -> {
                            String name[] = a.split(":");
                            Patient patient = patientRepository.save(new Patient(name[0], name[1]));

                            Careplan careplan1 = new Careplan(patient, "Care Plan 1");
                            Careplan careplan2 = new Careplan(patient, "Care Plan 2");
                            careplanRepository.save(careplan1);
                            careplanRepository.save(careplan2);

                            assessmentRepository.save(new Assessment(careplan1, "Assessment 1"));
                            assessmentRepository.save(new Assessment(careplan1, "Assessment 2"));
                            assessmentRepository.save(new Assessment(careplan2, "Assessment 1"));
                            assessmentRepository.save(new Assessment(careplan2, "Assessment 2"));

                            interventionRepository.save(new Intervention(careplan1, InterventionType.Medication, "Intervention 1"));
                            interventionRepository.save(new Intervention(careplan1, InterventionType.Medication, "Intervention 2"));
                            interventionRepository.save(new Intervention(careplan2, InterventionType.Medication, "Intervention 1"));
                            interventionRepository.save(new Intervention(careplan2, InterventionType.Medication, "Intervention 2"));
                        });
    }
}
