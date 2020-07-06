package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
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
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Authorization;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Consent;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Observation;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

@Component
@AllArgsConstructor
public class ConsentMapper {
    private static final String OPT_OUT_URI = "http://hl7.org/fhir/ConsentPolicy/opt-out";
    private static final String FHIR_SNOMED = "http://snomed.info/sct";
    private static final String ITK_SNOMED = "2.16.840.1.113883.2.1.3.2.4.15";
    private static final String NPFIT_CDA_CONTENT = "2.16.840.1.113883.2.1.3.2.4.18.16";
    private static final String SYSTEM_CODE = "887031000000108";
    private static final String PERMISSION_TO_VIEW = "COCD_TP146050GB01#PermissionToView";

    public Consent mapConsent(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Encounter encounter) {
        Consent consent = new Consent();
        consent.setIdElement(newRandomUuid());

        Patient patient = (Patient) encounter.getSubjectTarget();

        if (clinicalDocument.isSetSetId()) {
            Identifier docIdentifier = new Identifier();
            docIdentifier.setUse(Identifier.IdentifierUse.USUAL);
            docIdentifier.setValue(clinicalDocument.getSetId().getRoot());
            consent.setIdentifier(docIdentifier);
        }

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
            .setDateTime(now)
            .setPolicyRule(OPT_OUT_URI);

        extractAuthCodesFromDoc(consent, clinicalDocument);
        POCDMT000002UK01StructuredBody structuredBody = getStructuredBody(clinicalDocument);
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
                if (authConsent.isSetCode()) {
                    Optional<CodeableConcept> codeableConcept = getCodingFromCE(authConsent.getCode());
                    if (codeableConcept.isPresent()) {
                        consent.addAction(codeableConcept.get());
                    }
                }
            }
        }
    }

    private void extractDataPeriodFromDoc(Consent consent, POCDMT000002UK01StructuredBody structuredBody) {
        List<POCDMT000002UK01Entry> permissionEntries = getEntriesOfType(structuredBody, PERMISSION_TO_VIEW);
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
        List<POCDMT000002UK01Section> sections = getSectionsOfType(structuredBody, ITK_SNOMED, SYSTEM_CODE);
        for (POCDMT000002UK01Section section : sections) {
            Narrative narrative = new Narrative();
            narrative.setStatus(Narrative.NarrativeStatus.GENERATED);
            if (section.isSetText()) {
                narrative.setDivAsString(section.getText().xmlText());
                consent.setText(narrative);
            }
        }
    }

    private void extractConsentSource(Consent consent, POCDMT000002UK01StructuredBody structuredBody) {
        List<POCDMT000002UK01Section> sections = getSectionsOfType(structuredBody, ITK_SNOMED, SYSTEM_CODE);
        sections.stream()
            .filter(section -> section.isSetId())
            .forEach(section -> consent.setSource(new Identifier().setValue(section.getId().getRoot())));
    }

    private POCDMT000002UK01StructuredBody getStructuredBody(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        return Optional.ofNullable(clinicalDocument)
            .map(POCDMT000002UK01ClinicalDocument1::getComponent)
            .map(POCDMT000002UK01Component2::getStructuredBody)
            .orElse(null);
    }

    private List<POCDMT000002UK01Section> getSectionsOfType(POCDMT000002UK01StructuredBody structuredBody,
                                                            String system, String code) {
        if (structuredBody == null) return Collections.emptyList();

        return Arrays.stream(structuredBody.getComponentArray())
            .flatMap(component -> Optional.ofNullable(component.getSection()).stream())
            .flatMap(section -> Arrays.stream(section.getComponentArray()))
            .map(POCDMT000002UK01Component5::getSection)
            .filter(sectionHasCode(system, code))
            .collect(Collectors.toUnmodifiableList());
    }

    private List<POCDMT000002UK01Entry> getEntriesOfType(POCDMT000002UK01StructuredBody structuredBody,
                                                         String template) {
        if (structuredBody == null) return Collections.emptyList();

        return Arrays.stream(structuredBody.getComponentArray())
            .flatMap(component -> Optional.ofNullable(component.getSection()).stream())
            .flatMap(section -> Arrays.stream(section.getEntryArray()))
            .filter(entryHasTemplate(template))
            .collect(Collectors.toUnmodifiableList());
    }

    private Predicate<POCDMT000002UK01Section> sectionHasCode(String system, String code) {
        return section -> Optional.ofNullable(section.getCode())
            .map(codeElement -> system.equals(codeElement.getCodeSystem())
                && code.equals(codeElement.getCode()))
            .orElse(false);
    }

    private Predicate<? super POCDMT000002UK01Entry> entryHasTemplate(String template) {
        return entry -> entry.isSetContentId()
            && entry.getContentId().getRoot().equals(NPFIT_CDA_CONTENT)
            && entry.getContentId().getExtension().equals(template);
    }

    private Optional<CodeableConcept> getCodingFromCE(CE code) {
        if (code.isSetCodeSystem() && code.isSetCode() && code.isSetDisplayName()) {
            Coding coding = new Coding(code.getCodeSystem().equalsIgnoreCase(ITK_SNOMED) ?
                FHIR_SNOMED : code.getCodeSystem(), code.getCode(), code.getDisplayName());
            CodeableConcept cc = new CodeableConcept()
                .setCoding(List.of(coding)).setText(code.getDisplayName());
            return Optional.of(cc);
        } else {
            return Optional.empty();
        }
    }
}
