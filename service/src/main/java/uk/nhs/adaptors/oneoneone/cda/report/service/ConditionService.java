package uk.nhs.adaptors.oneoneone.cda.report.service;

import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class ConditionService {

    public Reference create(Condition condition) {
        return new Reference(condition);
    }
}
