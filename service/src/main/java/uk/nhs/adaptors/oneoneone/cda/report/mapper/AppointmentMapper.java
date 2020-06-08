package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.DateUtil;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Encounter;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Participant2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ParticipantRole;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

import org.apache.commons.lang3.time.DateUtils;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AppointmentMapper {

    private LocationMapper locationMapper;

    public Optional<Appointment> mapAppointment(POCDMT000002UK01Entry entry, POCDMT000002UK01Section matchingSection, Reference referralRequest, Reference patient) {
        POCDMT000002UK01Encounter itkEncounter= entry.getEncounter();
        Date startDate = DateUtil.parse(itkEncounter.getEffectiveTime().getValue());
        Date endDate = DateUtils.addMinutes(startDate, 10);
        Appointment appointment = new Appointment()
            .setStatus(Appointment.AppointmentStatus.BOOKED)
            .addIncomingReferral(referralRequest)
            .setStart(startDate)
            .setEnd(endDate)
            .setMinutesDuration(10);
        appointment.setIdElement(IdType.newRandomUuid());

        if (matchingSection != null) {
            appointment
                .setDescription(NodeUtil.getNodeValueString(matchingSection.getTitle()))
                .setComment(NodeUtil.getNodeValueString(matchingSection.getText().getContentArray(0)));
        }

        appointment.addParticipant(new Appointment.AppointmentParticipantComponent()
            .setActor(patient)
            .setRequired(Appointment.ParticipantRequired.REQUIRED)
            .setStatus(Appointment.ParticipationStatus.ACCEPTED));

        getAppointmentParticipantComponents(itkEncounter).forEach(appointment::addParticipant);

        return Optional.of(appointment);
    }

    private List<Appointment.AppointmentParticipantComponent> getAppointmentParticipantComponents(POCDMT000002UK01Encounter itkEncounter) {
        List<Appointment.AppointmentParticipantComponent> appointmentParticipantComponents = new ArrayList<>();
        POCDMT000002UK01Participant2[] participants = itkEncounter.getParticipantArray();
        for (POCDMT000002UK01Participant2 participant2 : participants) {
            POCDMT000002UK01ParticipantRole role = participant2.getParticipantRole();

            Location location = locationMapper.mapRoleToLocation(role);

            Appointment.AppointmentParticipantComponent appointmentParticipantComponent = new Appointment.AppointmentParticipantComponent();
            appointmentParticipantComponent.setActor(new Reference(location));
            appointmentParticipantComponent.setActorTarget(location);
            appointmentParticipantComponent.setRequired(Appointment.ParticipantRequired.REQUIRED);
            appointmentParticipantComponent.setStatus(Appointment.ParticipationStatus.ACCEPTED);

            appointmentParticipantComponents.add(appointmentParticipantComponent);
        }
        return appointmentParticipantComponents;
    }
}
