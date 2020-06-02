package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.hl7.fhir.dstu3.model.Attachment;

public class AttachmentMapper {

    public static Attachment mapAttachment() {
        Attachment attachment = new Attachment();
        attachment.setContentType(null);
        attachment.setLanguage(null);
        attachment.setData(null);
        attachment.setUrl(null);
        attachment.setSize(0);
        attachment.setHash(null);
        attachment.setTitle(null);
        attachment.setCreation(null);
        return attachment;
    }

}
