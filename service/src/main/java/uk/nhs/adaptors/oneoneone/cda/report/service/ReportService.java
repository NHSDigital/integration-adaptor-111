package uk.nhs.adaptors.oneoneone.cda.report.service;

import org.hl7.fhir.dstu3.model.Encounter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import uk.nhs.adaptors.oneoneone.cda.report.mapper.ReportMapper;
import uk.nhs.adaptors.oneoneone.properties.QueueProperties;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Service
public class ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QueueProperties queueProperties;

    public void transformAndPopulateToGP(POCDMT000002UK01ClinicalDocument1 clinicalDocumentDocument) throws JsonProcessingException {
        //TODO the most important piece of work - mapping - goes here
        Encounter encounter = reportMapper.mapReport(clinicalDocumentDocument);
        rabbitTemplate.convertAndSend(queueProperties.getExchange(), queueProperties.getRoutingKey(),
            objectMapper.writeValueAsString(encounter));
    }
}
