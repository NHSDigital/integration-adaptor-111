package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;

import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Appointment.AppointmentParticipantComponent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.BOOKED;
import static org.hl7.fhir.dstu3.model.Appointment.ParticipantRequired.REQUIRED;
import static org.hl7.fhir.dstu3.model.Appointment.ParticipationStatus.ACCEPTED;

@Component
@AllArgsConstructor
public class AppointmentMapper {

    private final LocationMapper locationMapper;

    private final NodeUtil nodeUtil;
    private final ResourceUtil resourceUtil;

    public Optional<Appointment> mapAppointment(POCDMT000002UK01Entry entry, POCDMT000002UK01Section matchingSection,
        Reference patient) {
        POCDMT000002UK01Encounter itkEncounter = entry.getEncounter();
        Appointment appointment = new Appointment()
            .setStatus(BOOKED);
        appointment.setIdElement(resourceUtil.newRandomUuid());

        if (matchingSection != null) {
            appointment
                .setDescription(nodeUtil.getNodeValueString(matchingSection.getTitle()))
                .setComment(nodeUtil.getNodeValueString(matchingSection.getText().getContentArray(0)));
        }

        appointment.addParticipant(new AppointmentParticipantComponent()
            .setActor(patient)
            .setActorTarget((Patient) patient.getResource())
            .setRequired(REQUIRED)
            .setStatus(ACCEPTED));

        getAppointmentParticipantComponents(itkEncounter).forEach(appointment::addParticipant);
        appointment.addReason(getReason(entry));
        return Optional.of(appointment);
    }

    private CodeableConcept getReason(POCDMT000002UK01Entry entry) {
        if (entry.isSetEncounter()) {
            POCDMT000002UK01Encounter encounterITK = entry.getEncounter();
            if (encounterITK.isSetCode()) {
                String reason = encounterITK.getCode().getDisplayName();
                if (reason != null) {
                    return new CodeableConcept().setText(reason);
                }
            }
        }

        return null;
    }

    private List<AppointmentParticipantComponent> getAppointmentParticipantComponents(POCDMT000002UK01Encounter itkEncounter) {
        List<AppointmentParticipantComponent> appointmentParticipantComponents = new ArrayList<>();
        POCDMT000002UK01Participant2[] participants = itkEncounter.getParticipantArray();
        for (POCDMT000002UK01Participant2 participant2 : participants) {
            POCDMT000002UK01ParticipantRole role = participant2.getParticipantRole();

            Location location = locationMapper.mapRoleToLocation(role);

            AppointmentParticipantComponent appointmentParticipantComponent = new AppointmentParticipantComponent();
            appointmentParticipantComponent.setActor(resourceUtil.createReference(location));
            appointmentParticipantComponent.setActorTarget(location);
            appointmentParticipantComponent.setRequired(REQUIRED);
            appointmentParticipantComponent.setStatus(ACCEPTED);

            appointmentParticipantComponents.add(appointmentParticipantComponent);
        }
        return appointmentParticipantComponents;
    }
}
