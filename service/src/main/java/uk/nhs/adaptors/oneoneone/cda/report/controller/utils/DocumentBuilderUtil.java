package uk.nhs.adaptors.oneoneone.cda.report.controller.utils;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DocumentBuilderUtil {

    public static Document parseDocument(String xml) throws ParserConfigurationException, IOException, SAXException {
        return documentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    private static DocumentBuilder documentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        //        df.setAttribute(ACCESS_EXTERNAL_DTD, EMPTY);
        //        df.setAttribute(ACCESS_EXTERNAL_SCHEMA, EMPTY);
        return df.newDocumentBuilder();
    }
}
