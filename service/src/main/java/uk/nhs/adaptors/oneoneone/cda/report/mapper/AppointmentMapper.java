package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus.BOOKED;
import static org.hl7.fhir.dstu3.model.Appointment.ParticipantRequired.REQUIRED;
import static org.hl7.fhir.dstu3.model.Appointment.ParticipationStatus.ACCEPTED;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.time.DateUtils;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Appointment.AppointmentParticipantComponent;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

@Component
@AllArgsConstructor
public class AppointmentMapper {

    private static final int MINUTES_DURATION = 10;
    private final LocationMapper locationMapper;

    private final NodeUtil nodeUtil;

    public Optional<Appointment> mapAppointment(POCDMT000002UK01Entry entry, POCDMT000002UK01Section matchingSection,
        Reference referralRequest, Reference patient) {
        POCDMT000002UK01Encounter itkEncounter = entry.getEncounter();
        Date startDate = DateUtil.parse(itkEncounter.getEffectiveTime().getValue());
        Date endDate = DateUtils.addMinutes(startDate, MINUTES_DURATION);
        Appointment appointment = new Appointment()
            .setStatus(BOOKED)
            .addIncomingReferral(referralRequest)
            .setStart(startDate)
            .setEnd(endDate)
            .setMinutesDuration(MINUTES_DURATION);
        appointment.setIdElement(IdType.newRandomUuid());

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

        return Optional.of(appointment);
    }

    private List<AppointmentParticipantComponent> getAppointmentParticipantComponents(POCDMT000002UK01Encounter itkEncounter) {
        List<AppointmentParticipantComponent> appointmentParticipantComponents = new ArrayList<>();
        POCDMT000002UK01Participant2[] participants = itkEncounter.getParticipantArray();
        for (POCDMT000002UK01Participant2 participant2 : participants) {
            POCDMT000002UK01ParticipantRole role = participant2.getParticipantRole();

            Location location = locationMapper.mapRoleToLocation(role);

            AppointmentParticipantComponent appointmentParticipantComponent = new AppointmentParticipantComponent();
            appointmentParticipantComponent.setActor(new Reference(location));
            appointmentParticipantComponent.setActorTarget(location);
            appointmentParticipantComponent.setRequired(REQUIRED);
            appointmentParticipantComponent.setStatus(ACCEPTED);

            appointmentParticipantComponents.add(appointmentParticipantComponent);
        }
        return appointmentParticipantComponents;
    }
}
