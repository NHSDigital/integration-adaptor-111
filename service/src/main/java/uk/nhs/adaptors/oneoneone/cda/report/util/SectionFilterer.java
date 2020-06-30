package uk.nhs.adaptors.oneoneone.cda.report.util;

import org.apache.commons.lang3.ArrayUtils;
import uk.nhs.connect.iucds.cda.ucr.CE;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Component5;
import uk.nhs.connect.iucds.cda.ucr.POCDMT000002UK01Section;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SectionFilterer {

    private List<String> filterCodes = new ArrayList<>();

    public SectionFilterer(List<String> filterCodes) {
        this.filterCodes = filterCodes;
    }

    public List<POCDMT000002UK01Section> findValidSections(POCDMT000002UK01Section section) {

        // Base case: If there are no nested sub components
        if (ArrayUtils.isEmpty(section.getComponentArray())) {
            return Collections.emptyList();
        }

        // Find care plans at this level.
        List<POCDMT000002UK01Section> subSections = Arrays.stream(section.getComponentArray())
                .map(POCDMT000002UK01Component5::getSection)
                .collect(Collectors.toUnmodifiableList());

        List<POCDMT000002UK01Section> validSections = subSections.stream()
                .filter(this::isValidSection)
                .collect(Collectors.toList());

        // Recursively find care plans in nested subsections.
        subSections.stream()
                .map(this::findValidSections)
                .forEach(validSections::addAll);

        return validSections;
    }

    private boolean isValidSection(POCDMT000002UK01Section section) {
        CE code = section.getCode();
        return code != null &&
                filterCodes.contains(code.getCode()) &&
                filterCodes.contains(code.getCodeSystem());
    }
}
