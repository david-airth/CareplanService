package com.flex.dhp.careplanservice;

import com.flex.dhp.common.AbstractRestController;
import com.flex.dhp.common.EntityNotFoundException;
import com.flex.dhp.common.model.Careplan;
import com.flex.dhp.common.model.Patient;
import com.flex.dhp.common.repository.AssessmentRepository;
import com.flex.dhp.common.repository.CareplanRepository;
import com.flex.dhp.common.repository.InterventionRepository;
import com.flex.dhp.common.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Collection;

/**
 * Created by david.airth on 7/11/17.
 */
@RestController
@RequestMapping("patients/{patientId}/careplans")
public class CareplanRestController extends AbstractRestController<Careplan> {

    private final PatientRepository patientRepository;
    private final CareplanRepository careplanRepository;
    private final AssessmentRepository assessmentRepository;
    private final InterventionRepository interventionRepository;

    @Autowired
    CareplanRestController(PatientRepository patientRepository,
                           CareplanRepository careplanRepository,
                           AssessmentRepository assessmentRepository,
                           InterventionRepository interventionRepository) {

        this.patientRepository = patientRepository;
        this.careplanRepository = careplanRepository;
        this.assessmentRepository = assessmentRepository;
        this.interventionRepository = interventionRepository;
    }

    @Override
    protected Careplan doGet(Long patientId, long careplanId) {
        Assert.notNull(patientId, "patientId is required");
        Assert.isTrue(careplanId > 0, "careplanId is required");

        return this.validateCareplan(careplanId);
    }

    @Override
    protected Collection<Careplan> doGetList(Long patientId) {
        Assert.notNull(patientId, "patientId is required");

        return careplanRepository.findByPatientId(patientId.longValue());
    }

    @Override
    protected Careplan doUpdate(Long patientId, Careplan careplan) {
        Assert.notNull(patientId, "PatientId is required");

        Careplan currentCareplan = this.validateCareplan(careplan.getId());

        currentCareplan.setName(careplan.getName());

        return this.careplanRepository.save(currentCareplan);
    }

    @Transactional
    @Override
    protected void doDelete(Long patientId, long id) {

        Assert.notNull(patientId, "patientId is required");

        Careplan careplan = validateCareplan(id);

        assessmentRepository.delete(careplan.getAssessments());
        interventionRepository.delete(careplan.getInterventions());
        careplanRepository.delete(id);

    }

    @Override
    protected Careplan doCreate(Long patientId, Careplan careplan) {
        Assert.isTrue(patientId > 0, "patientId is required");
        Assert.notNull(careplan, "careplan is required");

        Patient patient = this.validatePatient(patientId);

        Careplan result = careplanRepository.save(new Careplan(patient, careplan.getName()));

        return result;
    }

    private Patient validatePatient(long patientId) {
        Patient patient = this.patientRepository.findOne(patientId);
        if (patient == null)
            throw new EntityNotFoundException("Patient", patientId);
        else
            return patient;
    }

    private Careplan validateCareplan(long careplanId) {
        Careplan careplan = this.careplanRepository.findOne(careplanId);
        if (careplan == null)
            throw new EntityNotFoundException("Careplan", careplanId);
        else
            return careplan;
    }
}
