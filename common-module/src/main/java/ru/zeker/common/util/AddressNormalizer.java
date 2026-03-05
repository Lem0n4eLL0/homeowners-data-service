package ru.zeker.common.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.Map;
import java.util.regex.Pattern;

@UtilityClass
public final class AddressNormalizer {

    private static final Map<String, String[]> STREET_TYPES = Map.ofEntries(
            Map.entry("улица", new String[]{"ул", "ул.", "улица"}),
            Map.entry("переулок", new String[]{"пер", "пер.", "переулок"}),
            Map.entry("проспект", new String[]{"пр", "пр.", "просп", "просп.", "проспект", "пр-кт", "пр-кт."}),
            Map.entry("шоссе", new String[]{"ш", "ш.", "шоссе"}),
            Map.entry("бульвар", new String[]{"б", "б.", "бульвар"}),
            Map.entry("проезд", new String[]{"проезд", "пр-д", "пр-д."}),
            Map.entry("тупик", new String[]{"тупик", "туп.", "т"}),
            Map.entry("микрорайон", new String[]{"мкр", "мкр.", "микрорайон"}),
            Map.entry("квартал", new String[]{"кв-л", "кв-л.", "квартал"})
    );

    private static final Map<String, String[]> BUILDING_TYPES = Map.ofEntries(
            Map.entry("дом", new String[]{"д", "д.", "дом", "домовладение"}),
            Map.entry("корпус", new String[]{"к", "к.", "корп", "корп.", "корпус"}),
            Map.entry("строение", new String[]{"стр", "стр.", "строение"}),
            Map.entry("квартира", new String[]{"кв", "кв.", "квартира", "пом", "офис"})
    );

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");

    public static String normalize(String input) {
        if (StringUtils.isBlank(input)) {
            return StringUtils.EMPTY;
        }

        String result = input.trim().toLowerCase();
        result = normalizeDictionary(result, STREET_TYPES);
        result = normalizeDictionary(result, BUILDING_TYPES);
        result = result.replaceAll("\\s*([,\\.])\\s*", "$1 ");
        result = WHITESPACE_PATTERN.matcher(result).replaceAll(" ").trim();

        return result;
    }

    private static String normalizeDictionary(String input, Map<String, String[]> dictionary) {
        String result = input;

        var sortedKeys = dictionary.keySet().stream()
                .sorted(Comparator.comparingInt(String::length).reversed())
                .toList();

        for (String canonical : sortedKeys) {
            for (String variation : dictionary.get(canonical)) {
                String safeVariation = Pattern.quote(variation);
                String regex = "\\b" + safeVariation + "\\b";
                result = result.replaceAll(regex, canonical);
            }
        }
        return result;
    }

    public static boolean matches(String dbValue, String inputValue) {
        if (dbValue == null && inputValue == null) return true;
        if (dbValue == null || inputValue == null) return false;
        return normalize(dbValue).equals(normalize(inputValue));
    }
}