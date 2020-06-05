package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.stream.Stream;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.PN;
import org.hl7.fhir.dstu3.model.HumanName;
import org.springframework.stereotype.Component;

@Component
public class HumanNameMapper {

    private PeriodMapper periodMapper;

    public HumanNameMapper(PeriodMapper periodMapper) {
        this.periodMapper = periodMapper;
    }

    public HumanName mapHumanName(PN itkPersonName) {
        HumanName humanName = new HumanName();

        if (!NodeUtil.hasSubNodes(itkPersonName)) {
            return humanName.setText(NodeUtil.getNodeValueString(itkPersonName));
        }

        Stream.of(itkPersonName.getGivenArray())
            .map(NodeUtil::getNodeValueString)
            .forEach(humanName::addGiven);

        Stream.of(itkPersonName.getPrefixArray())
            .map(NodeUtil::getNodeValueString)
            .forEach(humanName::addPrefix);

        Stream.of(itkPersonName.getSuffixArray())
            .map(NodeUtil::getNodeValueString)
            .forEach(humanName::addSuffix);

        if (itkPersonName.sizeOfFamilyArray() >= 1) {
            humanName.setFamily(NodeUtil.getNodeValueString(itkPersonName.getFamilyArray(0)));
        }

        if (itkPersonName.isSetValidTime()) {
            humanName.setPeriod(periodMapper.mapPeriod(itkPersonName.getValidTime()));
        }

        return humanName;
    }
}
