package com.flex.dhp.careplanservice;

import com.flex.dhp.common.model.*;
import com.flex.dhp.common.repository.AssessmentRepository;
import com.flex.dhp.common.repository.CareplanRepository;
import com.flex.dhp.common.repository.InterventionRepository;
import com.flex.dhp.common.repository.PatientRepository;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by david.airth on 7/11/17.
 */
public abstract class AbstractRestControllerTests {
    protected MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    protected MockMvc mockMvc;

    protected String firstName = "Jane";
    protected String lastName = "Doe";

    protected HttpMessageConverter mappingJackson2HttpMessageConverter;

    protected Patient patient;

    protected List<Careplan> careplanList = new ArrayList<>();
    protected List<Assessment> assessmentList = new ArrayList<>();
    protected List<Intervention> interventionList = new ArrayList<>();

    @Autowired
    protected InterventionRepository interventionRepository;

    @Autowired
    protected AssessmentRepository assessmentRepository;

    @Autowired
    protected PatientRepository patientRepository;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected CareplanRepository cardcareRepository;

    @Autowired
    protected void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.interventionRepository.deleteAllInBatch();
        this.assessmentRepository.deleteAllInBatch();
        this.cardcareRepository.deleteAllInBatch();
        this.patientRepository.deleteAllInBatch();

        this.patient = patientRepository.save(new Patient(firstName, lastName));

        this.careplanList.add(cardcareRepository.save(new Careplan(patient, "Care Plan 1")));
        this.careplanList.add(cardcareRepository.save(new Careplan(patient, "Care Plan 2")));

        this.assessmentList.add(assessmentRepository.save(new Assessment(this.careplanList.get(0), "Assessment 1")));
        this.assessmentList.add(assessmentRepository.save(new Assessment(this.careplanList.get(0), "Assessment 2")));
        this.assessmentList.add(assessmentRepository.save(new Assessment(this.careplanList.get(1), "Assessment 1")));
        this.assessmentList.add(assessmentRepository.save(new Assessment(this.careplanList.get(1), "Assessment 2")));

        this.interventionList.add(interventionRepository.save(new Intervention(this.careplanList.get(0), InterventionType.Medication, "Intervention 1")));
        this.interventionList.add(interventionRepository.save(new Intervention(this.careplanList.get(0), InterventionType.Medication, "Intervention 2")));
        this.interventionList.add(interventionRepository.save(new Intervention(this.careplanList.get(1), InterventionType.Medication, "Intervention 1")));
        this.interventionList.add(interventionRepository.save(new Intervention(this.careplanList.get(1), InterventionType.Medication, "Intervention 2")));
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}
