package uk.nhs.adaptors.oneoneone.cda.report.util;

import java.util.Arrays;
import java.util.function.Predicate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Reference;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReferenceUtil {

  /**
   * @param type DomainResource type
   * @return Predicate which returns true if the FHIR Reference matches the name of the type <strong>exactly</strong>.
   */
  public static Predicate<Reference> ofType(Class<? extends DomainResource> type) {
    return ref -> {
      if(ref.getResource() != null) {
        return type.isInstance(ref.getResource());
      }
      else if (ref.hasReferenceElement()) {
        return ref.getReferenceElement().getResourceType().equals(type.getSimpleName());
      }

      return false;
    };
  }

  @SafeVarargs
  public static Predicate<DomainResource> ofTypes(Class<? extends DomainResource>... types) {
    return Arrays.stream(types)
        .map(type -> (Predicate<DomainResource>) type::isInstance)
        .reduce(Predicate::or)
        .orElse(x -> false);

  }

}
