package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import static org.hl7.fhir.dstu3.model.HumanName.NameUse.OFFICIAL;

import java.util.stream.Stream;

import org.hl7.fhir.dstu3.model.HumanName;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.PN;

@Component
@AllArgsConstructor
public class HumanNameMapper {

    private final PeriodMapper periodMapper;

    private final NodeUtil nodeUtil;

    public HumanName mapHumanName(PN itkPersonName) {
        HumanName humanName = new HumanName();

        if (!nodeUtil.hasSubNodes(itkPersonName)) {
            return humanName.setText(nodeUtil.getNodeValueString(itkPersonName));
        }

        Stream.of(itkPersonName.getGivenArray())
            .map(nodeUtil::getNodeValueString)
            .forEach(humanName::addGiven);

        Stream.of(itkPersonName.getPrefixArray())
            .map(nodeUtil::getNodeValueString)
            .forEach(humanName::addPrefix);

        Stream.of(itkPersonName.getSuffixArray())
            .map(nodeUtil::getNodeValueString)
            .forEach(humanName::addSuffix);

        if (itkPersonName.sizeOfFamilyArray() >= 1) {
            humanName.setFamily(nodeUtil.getNodeValueString(itkPersonName.getFamilyArray(0)));
        }

        if (itkPersonName.isSetValidTime()) {
            humanName.setPeriod(periodMapper.mapPeriod(itkPersonName.getValidTime()));
        }

        humanName.setUse(OFFICIAL);

        return humanName;
    }
}
