package uk.nhs.adaptors.oneoneone.cda.report.util;

import org.apache.xmlbeans.XmlObject;
import org.junit.Test;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NodeUtilTest {

    @Test
    public void shouldGetNodeString_nullNode() {
        var xmlObject = mock(XmlObject.class);

        assertNull(NodeUtil.getNodeValueString(xmlObject));
    }

    @Test
    public void shouldGetNodeStringNullChild() {
        var xmlObject = mock(XmlObject.class);
        var innerNode = mock(Node.class);
        when(xmlObject.getDomNode()).thenReturn(innerNode);

        assertNull(NodeUtil.getNodeValueString(xmlObject));
    }

    @Test
    public void shouldGetNodeStringNullValue() {
        var xmlObject = mock(XmlObject.class);
        var innerNode = mock(Node.class);
        when(xmlObject.getDomNode()).thenReturn(innerNode);
        var childNode = mock(Node.class);
        when(innerNode.getFirstChild()).thenReturn(childNode);

        assertNull(NodeUtil.getNodeValueString(xmlObject));
    }

    @Test
    public void shouldGetNodeString_happy() {
        var xmlObject = mock(XmlObject.class);
        var innerNode = mock(Node.class);
        when(xmlObject.getDomNode()).thenReturn(innerNode);
        var childNode = mock(Node.class);
        when(innerNode.getFirstChild()).thenReturn(childNode);
        when(childNode.getNodeValue()).thenReturn("happy path");

        assertEquals(NodeUtil.getNodeValueString(xmlObject), "happy path");
    }
}
