package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.ResourceUtil;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;

@Component
@RequiredArgsConstructor
public class ProcedureRequestMapper {

    private final ResourceUtil resourceUtil;
    private final PeriodMapper periodMapper;

    public ProcedureRequest mapProcedureRequest(POCDMT000002UK01ClinicalDocument1 clinicalDocument, Reference patient,
        ReferralRequest referralRequest) {
        ProcedureRequest procedureRequest = new ProcedureRequest();
        if (clinicalDocument.isSetComponentOf()) {
            if (clinicalDocument.getComponentOf().getEncompassingEncounter() != null) {
                if (clinicalDocument.getComponentOf().getEncompassingEncounter().isSetDischargeDispositionCode()) {
                    Coding coding = new Coding();
                    CE dischargeCode = clinicalDocument.getComponentOf().getEncompassingEncounter().getDischargeDispositionCode();

                    if (dischargeCode.isSetDisplayName()) {
                        coding.setDisplay(dischargeCode.getDisplayName());
                    }
                    if (dischargeCode.isSetCode()) {
                        coding.setCode(dischargeCode.getCode());
                    }
                    if (dischargeCode.isSetCodeSystem()) {
                        coding.setSystem(dischargeCode.getCodeSystem());
                    }
                    if (StringUtils.isNotBlank(coding.getCode()) || StringUtils.isNotBlank(coding.getDisplay())
                        || StringUtils.isNotBlank(coding.getSystem())) {
                        procedureRequest.setIdElement(resourceUtil.newRandomUuid());
                        procedureRequest.setStatus(ProcedureRequest.ProcedureRequestStatus.ACTIVE)
                            .setIntent(ProcedureRequest.ProcedureRequestIntent.PLAN)
                            .setPriority(ProcedureRequest.ProcedureRequestPriority.ROUTINE)
                            .setCode(new CodeableConcept().addCoding(coding))
                            .setSubject(patient)
                            .setDoNotPerform(false)
                            .setOccurrence(periodMapper.mapPeriod(clinicalDocument.getEffectiveTime()))
                            .setReasonReference(referralRequest.getReasonReference());
                    }
                }
            }
        }

        return procedureRequest;
    }
}
