package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01ClinicalDocument1;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component2;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Entry;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01StructuredBody;

@UtilityClass
public class StructuredBodyUtil {

    private static final String NPFIT_CDA_CONTENT = "2.16.840.1.113883.2.1.3.2.4.18.16";

    public POCDMT000002UK01StructuredBody getStructuredBody(
        POCDMT000002UK01ClinicalDocument1 clinicalDocument) {

        return Optional.ofNullable(clinicalDocument)
            .map(POCDMT000002UK01ClinicalDocument1::getComponent)
            .map(POCDMT000002UK01Component2::getStructuredBody)
            .orElse(null);
    }

    public Predicate<? super POCDMT000002UK01Entry> entryHasTemplate(String template) {
        return entry -> entry.isSetContentId()
            && entry.getContentId().getRoot().equals(NPFIT_CDA_CONTENT)
            && entry.getContentId().getExtension().equals(template);
    }

    public List<POCDMT000002UK01Entry> getEntriesOfType(POCDMT000002UK01StructuredBody structuredBody,
        String template) {

        if (structuredBody == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(structuredBody.getComponentArray())
            .flatMap(component -> Optional.ofNullable(component.getSection()).stream())
            .flatMap(section -> Arrays.stream(section.getEntryArray()))
            .filter(entryHasTemplate(template))
            .collect(Collectors.toUnmodifiableList());
    }
}

