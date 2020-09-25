package uk.nhs.adaptors.oneoneone.cda.report.enums;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language implements Concept {
    ARABIC("ar", "Arabic"),
    BENGALI("bn", "Bengali"),
    CZECH("cs", "Czech"),
    DANISH("da", "Danish"),
    GERMAN("de", "German"),
    GERMAN_AUSTRIA("de-at", "German (Austria)"),
    GERMAN_SWITZERLAND("de-ch", "German (Switzerland)"),
    GERMAN_GERMANY("de-de", "German (Germany)"),
    GREEK("el", "Greek"),
    ENGLISH("en", "English"),
    ENGLISH_Australia("en-au", "English (Australia)"),
    ENGLISH_Canada("en-ca", "English (Canada)"),
    ENGLISH_GREAT_BRITAIN("en-gb", "English (Great Britain)"),
    ENGLISH_INDIA("en-in", "English (India)"),
    ENGLISH_NEW_ZELAND("en-nz", "English (New Zeland)"),
    ENGLISH_SINGAPORE("en-sg", "English (Singapore)"),
    ENGLISH_UNITED_STATES("en-us", "English (United States)"),
    SPANISH("es", "Spanish"),
    SPANISH_ARGENTINA("es-ar", "Spanish (Argentina)"),
    SPANISH_SPAIN("es-es", "Spanish (Spain)"),
    SPANISH_URUGUAY("es-uy", "Spanish (Uruguay)"),
    FINNISH("fi", "Finnish"),
    FRENCH("fr", "French"),
    FRENCH_BELGIUM("fr-be", "French (Belgium)"),
    FRENCH_SWITZERLAND("fr-ch", "French (Switzerland)"),
    FRENCH_FRANCE("fr-fr", "French (France)"),
    FRYSIAN("fy", "Frysian"),
    FRYSIAN_NETHERLANDS("fy-nl", "Frysian (Netherlands)"),
    HINDI("hi", "Hindi"),
    CROATIAN("hr", "Croatian"),
    ITALIAN("it", "Italian"),
    ITALIAN_SWITZERLAND("it-ch", "Italian (Switzerland)"),
    ITALIAN_ITALY("it-it", "Italian (Italy)"),
    JAPANESE("ja", "Japanese"),
    KOREAN("ko", "Korean"),
    DUTCH("nl", "Dutch"),
    DUTCH_BELGIUM("nl-be", "Dutch (Belgium)"),
    DUTCH_NETHERLANDS("nl-nl", "Dutch (Netherlands)"),
    NORWEGIAN("no", "Norwegian"),
    NORWEGIAN_NORWAY("no-no", "Norwegian (Norway)"),
    PUNJABI("pa", "Punjabi"),
    PORTUGUESE("pt", "Portuguese"),
    PORTUGUESE_BRAZIL("pt-br", "Portuguese (Brazil)"),
    RUSSIAN("ru", "Russian"),
    RUSSIAN_RUSSIAN("ru-ru", "Russian (Russia)"),
    SERBIAN("sr", "Serbian"),
    SERBIAN_SERBIA("sr-sp", "Serbian (Serbia)"),
    SWEDISH("sv", "Swedish"),
    SWEDISH_SWEDEN("sv-se", "Swedish (Sweden)"),
    TELEGU("te", "Telegu"),
    CHINESE("zh", "Chinese"),
    CHINESE_CHINA("zh-cn", "Chinese (China)"),
    CHINESE_HONG_KONG("zh-hk", "Chinese (Hong Kong)"),
    CHINESE_SINGAPORE("zh-sg", "Chinese (Singapore)"),
    CHINESE_TAIWAN("zh-tw", "Chinese (Taiwan)");

    private final String system = "http://hl7.org/fhir/ValueSet/languages";
    private final String code;
    private final String display;

    public static uk.nhs.adaptors.oneoneone.cda.report.enums.Language fromCode(String code) {
        return Stream.of(values())
            .filter(am -> code.toLowerCase().equals(am.code))
            .findFirst()
            .orElse(null);
    }
}
