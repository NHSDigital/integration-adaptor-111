package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import lombok.AllArgsConstructor;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.hl7.fhir.dstu3.model.Address.AddressType;
import org.springframework.stereotype.Component;

import uk.nhs.adaptors.oneoneone.cda.report.util.NodeUtil;
import uk.nhs.connect.iucds.cda.ucr.AD;

import java.util.Arrays;

@Component
@AllArgsConstructor
public class AddressMapper {

    private final PeriodMapper periodMapper;

    private final NodeUtil nodeUtil;

    public Address mapAddress(AD itkAddress) {
        Address address = new Address();

        Arrays.stream(itkAddress.getStreetAddressLineArray())
            .map(nodeUtil::getNodeValueString)
            .forEach(address::addLine);

        if (itkAddress.isSetUse()) {
            String addressTypeString = getAddressTypeString(itkAddress.getUse().get(0).toString());
            String addressUseString = getAddressUseString(itkAddress.getUse().get(0).toString());
            if (addressTypeString != null) {
                address.setType(AddressType.fromCode(addressTypeString));
            } else {
                address.setUse(AddressUse.fromCode(addressUseString));
            }
        }

        if (itkAddress.sizeOfPostalCodeArray() > 0) {
            address.setPostalCode(nodeUtil.getNodeValueString(itkAddress.getPostalCodeArray(0)));
        }

        if (itkAddress.sizeOfCityArray() > 0) {
            address.setCity(nodeUtil.getNodeValueString(itkAddress.getCityArray(0)));
        }

        if (itkAddress.sizeOfDescArray() > 0) {
            address.setText(nodeUtil.getNodeValueString(itkAddress.getDescArray(0)));
        }

        if (itkAddress.sizeOfCountryArray() > 0) {
            address.setCountry(nodeUtil.getNodeValueString(itkAddress.getCountryArray(0)));
        }

        if (itkAddress.sizeOfStateArray() > 0) {
            address.setState(nodeUtil.getNodeValueString(itkAddress.getStateArray(0)));
        }

        if (itkAddress.sizeOfStateArray() > 0) {
            address.setDistrict(nodeUtil.getNodeValueString(itkAddress.getPrecinctArray(0)));
        }

        if (itkAddress.sizeOfUseablePeriodArray() > 0) {
            address.setPeriod(periodMapper.mapPeriod((itkAddress.getUseablePeriodArray(0))));
        }

        return address;
    }

    private String getAddressTypeString(String typeCode) {
        return switch (typeCode) {
            case "PHYS" -> "physical";
            default -> null;
        };
    }

    private String getAddressUseString(String useCode) {
        return switch (useCode) {
            case "H", "HP" -> "home";
            case "WP" -> "work";
            case "TMP" -> "temp";
            default -> null;
        };
    }
}
