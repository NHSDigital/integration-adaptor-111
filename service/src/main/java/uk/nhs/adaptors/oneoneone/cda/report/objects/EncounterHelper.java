package uk.nhs.adaptors.oneoneone.cda.report.objects;

import java.util.Optional;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.ReferralRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EncounterHelper {
    private Encounter encounter;
    private ReferralRequest referralRequest;
    private Optional<Appointment> appointment;
}
