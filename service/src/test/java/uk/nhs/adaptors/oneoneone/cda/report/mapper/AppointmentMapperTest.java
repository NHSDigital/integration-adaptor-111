package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import uk.nhs.connect.iucds.cda.ucr.IVLTS;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;
import uk.nhs.connect.iucds.cda.ucr.ST;
import uk.nhs.connect.iucds.cda.ucr.StrucDocContent;
import uk.nhs.connect.iucds.cda.ucr.StrucDocText;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.w3c.dom.Node;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentMapperTest {

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private AppointmentMapper appointmentMapper;

    @Mock
    private Location location;

    public static final String TITLE = "title";
    public static final String COMMENT = "comment";

    @Test
    public void mapAppointment() {
        POCDMT000002UK01Entry entry = mockEntry();
        POCDMT000002UK01Section section = mockSection();
        Reference referralRequest = mock(Reference.class);
        Reference patient = mock(Reference.class);

        when(locationMapper.mapRoleToLocation(any())).thenReturn(location);

        Optional<Appointment> appointmentOptional = appointmentMapper.mapAppointment(entry, section, referralRequest, patient);

        assertThat(appointmentOptional.isPresent());
        Appointment appointment = appointmentOptional.get();
        assertThat(appointment.getIdElement().getValue()).startsWith("urn:uuid:");
        assertThat(appointment.getStatus()).isEqualTo(Appointment.AppointmentStatus.BOOKED);
        assertThat(appointment.getIncomingReferral().get(0)).isEqualTo(referralRequest);
        assertThat(appointment.getStart()).isEqualTo(Date.from(Instant.parse("2011-05-19T19:45:00.00Z")));
        assertThat(appointment.getEnd()).isEqualTo(Date.from(Instant.parse("2011-05-19T19:55:00.00Z")));
        assertThat(appointment.getMinutesDuration()).isEqualTo(10);
        assertThat(appointment.getDescription()).isEqualTo(TITLE);
        assertThat(appointment.getComment()).isEqualTo(COMMENT);
        assertThat(appointment.getParticipantFirstRep().getActor()).isEqualTo(patient);
        assertThat(appointment.getParticipantFirstRep().getRequired()).isEqualTo(Appointment.ParticipantRequired.REQUIRED);
        assertThat(appointment.getParticipantFirstRep().getStatus()).isEqualTo(Appointment.ParticipationStatus.ACCEPTED);
    }

    private POCDMT000002UK01Entry mockEntry() {
        POCDMT000002UK01Entry entry = mock(POCDMT000002UK01Entry.class);
        POCDMT000002UK01Encounter encounter = mock(POCDMT000002UK01Encounter.class);
        POCDMT000002UK01Participant2 participant = mock(POCDMT000002UK01Participant2.class);
        POCDMT000002UK01ParticipantRole participantRole = mock(POCDMT000002UK01ParticipantRole.class);
        IVLTS time = mock(IVLTS.class);
        when(entry.getEncounter()).thenReturn(encounter);
        when(encounter.getParticipantArray()).thenReturn(new POCDMT000002UK01Participant2[] { participant });
        when(encounter.getEffectiveTime()).thenReturn(time);
        when(time.getValue()).thenReturn("201105191945+00");
        when(participant.getParticipantRole()).thenReturn(participantRole);
        return entry;
    }

    private POCDMT000002UK01Section mockSection() {
        POCDMT000002UK01Section section = mock(POCDMT000002UK01Section.class);

        ST title = mock(ST.class);
        when(section.getTitle()).thenReturn(title);
        mockTitleXmlStructure(title);

        StrucDocText description = mock(StrucDocText.class);
        when(section.getText()).thenReturn(description);
        mockDescriptionXmlStructure(description);

        return section;
    }

    private void mockDescriptionXmlStructure(StrucDocText description) {
        StrucDocContent structuredContent = mock(StrucDocContent.class);
        when(description.getContentArray(ArgumentMatchers.anyInt())).thenReturn(structuredContent);

        Node structuredContentNode = mock(Node.class);
        Node structuredContentSubnode = mock(Node.class);
        when(structuredContent.getDomNode()).thenReturn(structuredContentNode);
        when(structuredContentNode.getFirstChild()).thenReturn(structuredContentSubnode);
        when(structuredContentSubnode.getNodeValue()).thenReturn(COMMENT);
    }

    private void mockTitleXmlStructure(ST title) {
        Node titleNode = mock(Node.class);
        Node titleSubnode = mock(Node.class);
        when(title.getDomNode()).thenReturn(titleNode);
        when(titleNode.getFirstChild()).thenReturn(titleSubnode);
        when(titleSubnode.getNodeValue()).thenReturn(TITLE);
    }
}
