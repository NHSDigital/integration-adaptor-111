package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Consent;
import org.hl7.fhir.dstu3.model.Consent.ConsentDataComponent;
import org.hl7.fhir.dstu3.model.Consent.ConsentDataMeaning;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.adaptors.oneoneone.cda.report.util.CodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.StructuredBodyUtil;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Authorization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Consent;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Observation;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

import java.util.Date;
import java.util.List;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@AllArgsConstructor
public class ConsentMapper {
    private static final String OPT_OUT_URI = "http://hl7.org/fhir/ConsentPolicy/opt-out";
    private static final String SYSTEM_SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";
    private static final String SYSTEM_CODE = "887031000000108";
    private static final String PERMISSION_TO_VIEW = "COCD_TP146050GB01#PermissionToView";

    public Consent mapConsent(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {
        Consent consent = new Consent();
        consent.setIdElement(newRandomUuid());

        Patient patient = (Patient) encounter.getSubjectTarget();

        Identifier docIdentifier = new Identifier();
        docIdentifier.setUse(Identifier.IdentifierUse.USUAL);
        docIdentifier.setValue(clinicalDocument.getSetId().getRoot());

        Date now = new Date();
        consent.setLanguage(encounter.getLanguage());
        consent.setStatus(Consent.ConsentState.ACTIVE)
                .setPeriod(encounter.getPeriod())
                .setPatientTarget(patient)
                .setPatient(encounter.getSubject())
                .addConsentingParty(encounter.getSubject())
                .addOrganization(encounter.getServiceProvider())
                .setData(List.of(new ConsentDataComponent()
                        .setMeaning(ConsentDataMeaning.RELATED)
                        .setReference(new Reference(encounter))))
                .setIdentifier(docIdentifier)
                .setDateTime(now)
                .setPolicyRule(OPT_OUT_URI);

        extractAuthCodesFromDoc(consent, clinicalDocument);
        POCDMT000002UK01StructuredBody structuredBody = StructuredBodyUtil.getStructuredBody(clinicalDocument);
        if (structuredBody != null) {
            extractDataPeriodFromDoc(consent, structuredBody);
            extractConsentSource(consent, structuredBody);
            extractTextBody(consent, structuredBody);
        }

        return consent;
    }

    private void extractAuthCodesFromDoc(Consent consent, POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        if (clinicalDocument.sizeOfAuthorizationArray() > 0) {
            for (POCDMT000002UK01Authorization auth : clinicalDocument.getAuthorizationArray()) {
                POCDMT000002UK01Consent authConsent = auth.getConsent();
                CE consentCode = authConsent.getCode();
                consent.addAction(CodeUtil.createCodeableConceptFromCE(consentCode));
            }
        }
    }

    private void extractDataPeriodFromDoc(Consent consent, POCDMT000002UK01StructuredBody structuredBody) {
        List<POCDMT000002UK01Entry> permissionEntries =
                StructuredBodyUtil.getEntriesOfType(structuredBody, PERMISSION_TO_VIEW);
        if (permissionEntries.isEmpty()) {
            return;
        }

        for (POCDMT000002UK01Entry permissionEntry : permissionEntries) {
            Period dataPeriod = getDataPeriod(permissionEntry);
            if (dataPeriod == null) continue;
            consent.setDataPeriod(dataPeriod);
        }
    }

    private Period getDataPeriod(POCDMT000002UK01Entry permissionEntry) {
        POCDMT000002UK01Observation observation = permissionEntry.getObservation();
        if (observation == null || !observation.isSetEffectiveTime()) {
            return null;
        }

        Period dataPeriod = new Period();
        IVLTS effectiveTime = observation.getEffectiveTime();
        if (effectiveTime.isSetLow()) {
            dataPeriod.setStart(DateUtil.parse(effectiveTime.getLow().getValue()));
        }
        if (effectiveTime.isSetHigh()) {
            dataPeriod.setEnd(DateUtil.parse(effectiveTime.getHigh().getValue()));
        }
        return dataPeriod;
    }

    private void extractTextBody(Consent consent, POCDMT000002UK01StructuredBody structuredBody) {
        List<POCDMT000002UK01Section> sections =
                StructuredBodyUtil.getSectionsOfType(structuredBody, SYSTEM_SNOMED, SYSTEM_CODE);
        for (POCDMT000002UK01Section section : sections) {
            Narrative narrative = new Narrative();
            narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
            narrative.setDivAsString(section.getText().xmlText());
            consent.setText(narrative);
        }
    }

    private void extractConsentSource(Consent consent, POCDMT000002UK01StructuredBody structuredBody) {
        List<POCDMT000002UK01Section> sections =
                StructuredBodyUtil.getSectionsOfType(structuredBody, SYSTEM_SNOMED, SYSTEM_CODE);
        for (POCDMT000002UK01Section section : sections) {
            consent.setSource(new Identifier().setValue(section.getId().getRoot()));
        }
    }
}
