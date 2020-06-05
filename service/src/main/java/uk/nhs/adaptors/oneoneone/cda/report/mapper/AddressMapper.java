package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import java.util.Arrays;
import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;

import org.hl7.fhir.dstu3.model.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    private PeriodMapper periodMapper;

    public AddressMapper(PeriodMapper periodMapper) {
        this.periodMapper = periodMapper;
    }

    public Address mapAddress(AD itkAddress) {
        Address address = new Address();

        Arrays.stream(itkAddress.getStreetAddressLineArray())
            .map(NodeUtil::getNodeValueString)
            .forEach(address::addLine);

        if (itkAddress.sizeOfPostalCodeArray() > 0) {
            address.setPostalCode(NodeUtil.getNodeValueString(itkAddress.getPostalCodeArray(0)));
        }

        if (itkAddress.sizeOfCityArray() > 0) {
            address.setCity(NodeUtil.getNodeValueString(itkAddress.getCityArray(0)));
        }

        if (itkAddress.sizeOfDescArray() > 0) {
            address.setText(NodeUtil.getNodeValueString(itkAddress.getDescArray(0)));
        }

        if (itkAddress.sizeOfCountryArray() > 0) {
            address.setCountry(NodeUtil.getNodeValueString(itkAddress.getCountryArray(0)));
        }

        if (itkAddress.sizeOfStateArray() > 0) {
            address.setState(NodeUtil.getNodeValueString(itkAddress.getStateArray(0)));
        }

        if (itkAddress.sizeOfStateArray() > 0) {
            address.setDistrict(NodeUtil.getNodeValueString(itkAddress.getPrecinctArray(0)));
        }

        if (itkAddress.sizeOfUseablePeriodArray() > 0) {
            address.setPeriod(periodMapper.mapPeriod((itkAddress.getUseablePeriodArray(0))));
        }

        return address;
    }
}
