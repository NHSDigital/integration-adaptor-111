package uk.nhs.adaptors.oneoneone.cda.report.enums;

import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language implements Concept {
    ARABIC("ar", "Arabic"),
    BENGALI("bn", "Bengali"),
    CHINESE_CHINA("zh-cn", "Chinese (China)"),
    CHINESE_HONG_KONG("zh-hk", "Chinese (Hong Kong)"),
    CHINESE_SINGAPORE("zh-sg", "Chinese (Singapore)"),
    CHINESE_TAIWAN("zh-tw", "Chinese (Taiwan)"),
    CHINESE("zh", "Chinese"),
    CROATIAN("hr", "Croatian"),
    CZECH("cs", "Czech"),
    DANISH("da", "Danish"),
    DUTCH_BELGIUM("nl-be", "Dutch (Belgium)"),
    DUTCH_NETHERLANDS("nl-nl", "Dutch (Netherlands)"),
    DUTCH("nl", "Dutch"),
    ENGLISH_Australia("en-au", "English (Australia)"),
    ENGLISH_Canada("en-ca", "English (Canada)"),
    ENGLISH_GREAT_BRITAIN("en-gb", "English (Great Britain)"),
    ENGLISH_INDIA("en-in", "English (India)"),
    ENGLISH_NEW_ZELAND("en-nz", "English (New Zeland)"),
    ENGLISH_SINGAPORE("en-sg", "English (Singapore)"),
    ENGLISH_UNITED_STATES("en-us", "English (United States)"),
    ENGLISH("en", "English"),
    FINNISH("fi", "Finnish"),
    FRENCH_BELGIUM("fr-be", "French (Belgium)"),
    FRENCH_FRANCE("fr-fr", "French (France)"),
    FRENCH_SWITZERLAND("fr-ch", "French (Switzerland)"),
    FRENCH("fr", "French"),
    FRYSIAN_NETHERLANDS("fy-nl", "Frysian (Netherlands)"),
    FRYSIAN("fy", "Frysian"),
    GERMAN_AUSTRIA("de-at", "German (Austria)"),
    GERMAN_GERMANY("de-de", "German (Germany)"),
    GERMAN_SWITZERLAND("de-ch", "German (Switzerland)"),
    GERMAN("de", "German"),
    GREEK("el", "Greek"),
    HINDI("hi", "Hindi"),
    ITALIAN_ITALY("it-it", "Italian (Italy)"),
    ITALIAN_SWITZERLAND("it-ch", "Italian (Switzerland)"),
    ITALIAN("it", "Italian"),
    JAPANESE("ja", "Japanese"),
    KOREAN("ko", "Korean"),
    NORWEGIAN_NORWAY("no-no", "Norwegian (Norway)"),
    NORWEGIAN("no", "Norwegian"),
    PORTUGUESE_BRAZIL("pt-br", "Portuguese (Brazil)"),
    PORTUGUESE("pt", "Portuguese"),
    PUNJABI("pa", "Punjabi"),
    RUSSIAN_RUSSIAN("ru-ru", "Russian (Russia)"),
    RUSSIAN("ru", "Russian"),
    SERBIAN_SERBIA("sr-sp", "Serbian (Serbia)"),
    SERBIAN("sr", "Serbian"),
    SPANISH_ARGENTINA("es-ar", "Spanish (Argentina)"),
    SPANISH_SPAIN("es-es", "Spanish (Spain)"),
    SPANISH_URUGUAY("es-uy", "Spanish (Uruguay)"),
    SPANISH("es", "Spanish"),
    SWEDISH_SWEDEN("sv-se", "Swedish (Sweden)"),
    SWEDISH("sv", "Swedish"),
    TELEGU("te", "Telegu");

    private final String system = "http://hl7.org/fhir/ValueSet/languages";
    private final String code;
    private final String display;

    public static Optional<uk.nhs.adaptors.oneoneone.cda.report.enums.Language> fromCode(String code) {
        return Stream.of(values())
            .filter(am -> code.toLowerCase().equals(am.code))
            .findFirst();
    }
}
