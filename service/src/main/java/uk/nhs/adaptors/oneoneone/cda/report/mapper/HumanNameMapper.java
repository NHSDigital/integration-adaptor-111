package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.stream.Stream;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.PN;
import org.hl7.fhir.dstu3.model.HumanName;

public class HumanNameMapper {

    public static HumanName mapHumanName(PN itk_person_name) {
        HumanName humanName = new HumanName();

        if (!NodeUtil.hasSubNodes(itk_person_name)) {
            return humanName.setText(NodeUtil.getNodeValueString(itk_person_name));
        }

        Stream.of(itk_person_name.getGivenArray())
            .map(NodeUtil::getNodeValueString)
            .forEach(humanName::addGiven);

        Stream.of(itk_person_name.getPrefixArray())
            .map(NodeUtil::getNodeValueString)
            .forEach(humanName::addPrefix);

        Stream.of(itk_person_name.getSuffixArray())
            .map(NodeUtil::getNodeValueString)
            .forEach(humanName::addSuffix);

        if (itk_person_name.sizeOfFamilyArray() >= 1) {
            humanName.setFamily(NodeUtil.getNodeValueString(itk_person_name.getFamilyArray(0)));
        }

        if (itk_person_name.isSetValidTime()) {
            humanName.setPeriod(PeriodMapper.mapPeriod(itk_person_name.getValidTime()));
        }

        return humanName;
    }
}
