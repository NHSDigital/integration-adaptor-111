package uk.nhs.adaptors.oneoneone.cda.report.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Calendar;

import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nhspathways.webservices.pathways.pathwayscase.PathwaysCaseDocument;

@ExtendWith(MockitoExtension.class)
public class PathwayUtilTest {
    @Mock
    private PathwaysCaseDocument.PathwaysCase pathwaysCase;

    @InjectMocks
    private PathwayUtil pathwayUtil;

    @Test
    public void isSetCaseReceiveEndShouldReturnTrueWhenCaseReceiveEndIsSet() {
        when(pathwaysCase.getCaseReceiveEnd()).thenReturn(new Calendar.Builder().build());
        when(pathwaysCase.isSetCaseReceiveEnd()).thenReturn(true);

        assertTrue(pathwayUtil.isSetCaseReceiveEnd(pathwaysCase));
    }

    @Test
    public void isSetCaseReceiveEndShouldReturnFalseWhenCaseReceiveEndIsNotSet() {
        when(pathwaysCase.getCaseReceiveEnd()).thenReturn(null);
        when(pathwaysCase.isSetCaseReceiveEnd()).thenReturn(false);

        assertFalse(pathwayUtil.isSetCaseReceiveEnd(pathwaysCase));
    }

    @Test
    public void isSetCaseReceiveEndShouldReturnFalseWhenGetCaseReceiveEndThrowsError() {
        when(pathwaysCase.getCaseReceiveEnd()).thenThrow(new XmlValueOutOfRangeException());

        assertFalse(pathwayUtil.isSetCaseReceiveEnd(pathwaysCase));
    }
}
