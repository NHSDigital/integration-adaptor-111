package uk.nhs.adaptors.oneoneone.cda.report.mapper;

import uk.nhs.connect.iucds.cda.ucr.AD;

import org.hl7.fhir.dstu3.model.Address;

public class AddressMapper {

    public static Address mapAddress(AD itkAddress) {
        Address address = new Address();
        address.setUse(null);
        address.setType(null);
        address.setText(itkAddress.getDescArray(0).toString());
        address.setLine(null);
        address.setCity(itkAddress.getCityArray(0).toString());
        address.setDistrict(null);
        address.setState(itkAddress.getStateArray(0).toString());
        address.setPostalCode(itkAddress.getPostalCodeArray(0).toString());
        address.setCountry(itkAddress.getCountryArray(0).toString());
        address.setPeriod(null);

        return address;
    }
}
