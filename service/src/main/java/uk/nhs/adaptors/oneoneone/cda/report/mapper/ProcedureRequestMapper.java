package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.IdType.newRandomUuid;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
@RequiredArgsConstructor
public class ProcedureRequestMapper {
    public ProcedureRequest mapProcedureRequest(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Reference patient,
        ReferralRequest referralRequest) {
        ProcedureRequest procedureRequest = new ProcedureRequest();
        if (clinicalDocument.isSetComponentOf()) {
            if (clinicalDocument.getComponentOf().getEncompassingEncounter() != null) {
                if (clinicalDocument.getComponentOf().getEncompassingEncounter().isSetDischargeDispositionCode()) {
                    Coding coding = new Coding();
                    CE dichargeCode = clinicalDocument.getComponentOf().getEncompassingEncounter().getDischargeDispositionCode();

                    if (dichargeCode.isSetDisplayName()) {
                        coding.setDisplay(dichargeCode.getDisplayName());
                    }
                    if (dichargeCode.isSetCode()) {
                        coding.setCode(dichargeCode.getCode());
                    }
                    if (dichargeCode.isSetCodeSystem()) {
                        coding.setSystem(dichargeCode.getCodeSystem());
                    }
                    if (StringUtils.isNotBlank(coding.getCode()) || StringUtils.isNotBlank(coding.getDisplay())
                        || StringUtils.isNotBlank(coding.getSystem())) {
                        procedureRequest.setIdElement(newRandomUuid());
                        procedureRequest.setStatus(ProcedureRequest.ProcedureRequestStatus.ACTIVE)
                            .setIntent(ProcedureRequest.ProcedureRequestIntent.PLAN)
                            .setPriority(ProcedureRequest.ProcedureRequestPriority.ROUTINE)
                            .setCode(new CodeableConcept().addCoding(coding))
                            .setSubject(patient)
                            .setDoNotPerform(false)
                            .setOccurrence(referralRequest.getOccurrence())
                            .setReasonReference(referralRequest.getReasonReference());
                    }
                }
            }
        }

        return procedureRequest;
    }
}
