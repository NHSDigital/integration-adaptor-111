package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static java.nio.file.Files.readAllBytes;

import static uk.nhs.adaptors.oneoneone.cda.report.controller.utils.ReportRequestUtils.extractClinicalDocument;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hl7.fhir.dstu3.model.Encounter.EncounterStatus.FINISHED;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

import org.apache.xmlbeans.XmlException;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest()
@RunWith(SpringRunner.class)
public class EncounterMapperTest {

    @Autowired
    ParticipantMapper participantMapper;

    @Autowired
    EncounterMapper encounterMapper;


    @Test
    public void mapEncounter() throws IOException, XmlException {

        URL resource = getClass().getResource("/xml/ITK_Report_request.xml");
        String doc =new String(readAllBytes(Paths.get(resource.getPath())));
        POCDMT000002UK01ClinicalDocument1 clinicalDocument = extractClinicalDocument(doc);

        Encounter encounter = encounterMapper.mapEncounter(clinicalDocument.getComponentOf().getEncompassingEncounter());
        assertThat(encounter.getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(encounter.getStatus()).isEqualTo(FINISHED);
        assertThat(encounter.getPeriod().getStart()).isEqualTo(Date.from(Instant.parse("2011-05-19T19:45:00.00Z")));
        assertThat(encounter.getPeriod().getEnd()).isEqualTo(Date.from(Instant.parse("2011-05-19T20:15:00.00Z")));

        List<Encounter.EncounterParticipantComponent> expectedParticipants = getEncounterParticipantComponents(clinicalDocument);
        verifyEncounterParticipantComponents(encounter.getParticipant(), expectedParticipants);
    }

    private void verifyEncounterParticipantComponents(List<Encounter.EncounterParticipantComponent> participants,
        List<Encounter.EncounterParticipantComponent> expectedParticipants ) {
        System.out.println("");
        assertThat(participants.size()).isEqualTo(expectedParticipants.size());
        for (int i=0; i<expectedParticipants.size(); i++) {
            Encounter.EncounterParticipantComponent participant  = participants.get(i);
            Encounter.EncounterParticipantComponent expectedParticipant  = expectedParticipants.get(i);

            assertThat(participant.getType().size()).isEqualTo(expectedParticipant.getType().size());
            assertThat(participant.getType().get(0).getText()).isEqualTo(expectedParticipant.getType().get(0).getText());

            verifyPeriod(participant.getPeriod(), expectedParticipant.getPeriod());

            Practitioner practitioner = (Practitioner) participant.getIndividual().getResource();
            Practitioner expectedPractitioner = (Practitioner) expectedParticipant.getIndividual().getResource();
            assertThat(practitioner.getIdElement().getValue()).startsWith("urn:uuid:");
            assertThat(practitioner.getActive()).isEqualTo(expectedPractitioner.getActive());
            verifyHumanName(practitioner.getName(), expectedPractitioner.getName());
            verifyContactPoint(practitioner.getTelecom(), expectedPractitioner.getTelecom());
        }
    }

    private List<Encounter.EncounterParticipantComponent> getEncounterParticipantComponents(POCDMT000002UK01ClinicalDocument1 clinicalDocument) {
        return Arrays.stream(clinicalDocument
            .getComponentOf()
            .getEncompassingEncounter()
            .getEncounterParticipantArray())
            .map(participantMapper::mapEncounterParticipant)
            .collect(Collectors.toList());
    }

    private void verifyContactPoint(List<ContactPoint> actual, List<ContactPoint> expected) {
        assertThat(actual.size()).isEqualTo(expected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat(actual.get(i).getValue()).isEqualTo(expected.get(i).getValue());
            assertThat(actual.get(i).getUse().toCode()).isEqualTo(expected.get(i).getUse().toCode());
        }
    }

    private void verifyPeriod(Period actual, Period expected) {
        assertThat(actual.getStart()).isEqualTo(expected.getStart());
        assertThat(actual.getEnd()).isEqualTo(expected.getEnd());
    }

    private void verifyHumanName(List<HumanName> actual, List<HumanName> expected) {
        assertThat(actual.size()).isEqualTo(expected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertThat(actual.get(i).getFamily()).isEqualTo(expected.get(i).getFamily());
            assertThat(actual.get(i).getGiven().toString()).isEqualTo(expected.get(i).getGiven().toString());
            assertThat(actual.get(i).getSuffix()).isEqualTo(expected.get(i).getSuffix());
            assertThat(actual.get(i).getPrefix()).isEqualTo(expected.get(i).getPrefix());
        }
    }

}
