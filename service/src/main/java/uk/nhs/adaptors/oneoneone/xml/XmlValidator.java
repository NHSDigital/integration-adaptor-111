package uk.nhs.adaptors.oneoneone.xml;

import static java.util.stream.Collectors.toList;

import static org.apache.logging.log4j.util.Strings.join;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

public final class XmlValidator {
    public static void validate(XmlObject xmlObject) throws XmlException {
        List<XmlValidationError> errors = new ArrayList<>();
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setErrorListener(errors);
        xmlObject.validate(xmlOptions);

        if (!errors.isEmpty()) {
            List<String> messages = errors.stream().map(it -> it.getMessage()).collect(toList());
            throw new XmlException(join(messages, '\n'));
        }
    }
}
