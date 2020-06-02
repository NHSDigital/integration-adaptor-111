package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import uk.nhs.connect.iucds.cda.ucr.II;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01AssignedEntity;

import org.hl7.fhir.dstu3.model.Identifier;

public class IdentifierMapper {

    public static Identifier mapIdentifier(II itk_id) {
        Identifier identifier = new Identifier();
        identifier.setUse(null);
        identifier.setType(null);
        identifier.setSystem(null);
        identifier.setValue(itk_id.getAssigningAuthorityName());
        identifier.setPeriod(null);
        identifier.setAssigner(null);
        identifier.setAssignerTarget(null);
        return identifier;
    }
}
