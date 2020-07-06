package uk.nhs.adaptors.oneoneone.cda.report.util;

import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NodeUtilTest {

    private NodeUtil nodeUtil;

    @Before
    public void setup() {
        nodeUtil = new NodeUtil();
    }

    @Test
    public void shouldGetNodeString_nullNode() {
        var xmlObject = mock(XmlObject.class);

        assertNull(nodeUtil.getNodeValueString(xmlObject));
    }

    @Test
    public void shouldGetNodeStringNullChild() {
        var xmlObject = mock(XmlObject.class);
        var innerNode = mock(Node.class);
        when(xmlObject.getDomNode()).thenReturn(innerNode);

        assertNull(nodeUtil.getNodeValueString(xmlObject));
    }

    @Test
    public void shouldGetNodeStringNullValue() {
        var xmlObject = mock(XmlObject.class);
        var innerNode = mock(Node.class);
        when(xmlObject.getDomNode()).thenReturn(innerNode);
        var childNode = mock(Node.class);
        when(innerNode.getFirstChild()).thenReturn(childNode);

        assertNull(nodeUtil.getNodeValueString(xmlObject));
    }

    @Test
    public void shouldGetNodeString_happy() {
        var xmlObject = mock(XmlObject.class);
        var innerNode = mock(Node.class);
        when(xmlObject.getDomNode()).thenReturn(innerNode);
        var childNode = mock(Node.class);
        when(innerNode.getFirstChild()).thenReturn(childNode);
        when(childNode.getNodeValue()).thenReturn("happy path");

        assertEquals(nodeUtil.getNodeValueString(xmlObject), "happy path");
    }
}
