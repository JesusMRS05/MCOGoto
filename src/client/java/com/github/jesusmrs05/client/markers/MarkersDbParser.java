package com.github.jesusmrs05.client.markers;

import com.github.jesusmrs05.client.config.MCOGotoConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MarkersDbParser {

    private static final String DEFAULT_DIMENSION = "overworld";

    private MarkersDbParser() {
    }

    public static List<Marker> parse(
            String source,
            MCOGotoConfig config
    ) {
        String json = extractJson(source);

        JsonElement root =
                JsonParser.parseString(json);

        if (!root.isJsonObject()) {
            throw new IllegalStateException(
                    "Markers database root is not a JSON object."
            );
        }

        Pattern dimensionPattern =
                compileDimensionPattern(
                        config.getDimensionRegex()
                );

        List<Marker> markers = new ArrayList<>();

        for (Map.Entry<String, JsonElement> entry :
                root.getAsJsonObject().entrySet()) {

            String bucketName = entry.getKey();
            JsonElement bucketElement = entry.getValue();

            if (!bucketElement.isJsonObject()) {
                continue;
            }

            JsonObject bucket =
                    bucketElement.getAsJsonObject();

            List<JsonElement> markerElements =
                    findMarkerElements(
                            bucket,
                            config.getMarkerPath()
                    );

            String dimension =
                    extractDimension(
                            bucketName,
                            dimensionPattern
                    );

            for (JsonElement markerElement :
                    markerElements) {

                if (!markerElement.isJsonObject()) {
                    continue;
                }

                JsonObject marker =
                        markerElement.getAsJsonObject();

                String name =
                        getString(
                                marker,
                                config.getNamePath()
                        );

                Double x =
                        getNumber(
                                marker,
                                config.getXPath()
                        );

                Double y =
                        getNumber(
                                marker,
                                config.getYPath()
                        );

                Double z =
                        getNumber(
                                marker,
                                config.getZPath()
                        );

                if (name == null
                        || x == null
                        || y == null
                        || z == null) {
                    continue;
                }

                markers.add(
                        new Marker(
                                name,
                                x,
                                y,
                                z,
                                dimension
                        )
                );
            }
        }

        return markers;
    }

    private static List<JsonElement> findMarkerElements(
            JsonObject bucket,
            String markerPath
    ) {
        String path = markerPath.trim();

        /*
         * The configured default is:
         *
         * $.*.raw[*]
         *
         * The parser is currently already inside the bucket
         * represented by the first wildcard, so the useful
         * remaining part is:
         *
         * raw[*]
         */
        if (path.equals("$.*.raw[*]")) {
            return getArrayContents(
                    bucket,
                    "raw"
            );
        }

        /*
         * Also accept:
         *
         * raw[*]
         * $.raw[*]
         */
        if (path.equals("raw[*]")
                || path.equals("$.raw[*]")) {
            return getArrayContents(
                    bucket,
                    "raw"
            );
        }

        /*
         * Generic fallback for simple:
         *
         * property[*]
         *
         * paths.
         */
        if (path.endsWith("[*]")) {
            String property =
                    path.substring(
                            0,
                            path.length() - 3
                    );

            if (property.startsWith("$.")) {
                property =
                        property.substring(2);
            } else if (property.startsWith(".")) {
                property =
                        property.substring(1);
            }

            return getArrayContents(
                    bucket,
                    property
            );
        }

        return List.of();
    }

    private static List<JsonElement> getArrayContents(
            JsonObject object,
            String property
    ) {
        if (!object.has(property)) {
            return List.of();
        }

        JsonElement element =
                object.get(property);

        if (!element.isJsonArray()) {
            return List.of();
        }

        List<JsonElement> result =
                new ArrayList<>();

        for (JsonElement child :
                element.getAsJsonArray()) {
            result.add(child);
        }

        return result;
    }

    private static String extractDimension(
            String bucketName,
            Pattern pattern
    ) {
        Matcher matcher =
                pattern.matcher(bucketName);

        if (!matcher.matches()) {
            return DEFAULT_DIMENSION;
        }

        String dimension;

        try {
            dimension = matcher.group(1);
        } catch (Exception exception) {
            return DEFAULT_DIMENSION;
        }

        if (!isValidDimension(dimension)) {
            return DEFAULT_DIMENSION;
        }

        return dimension;
    }

    private static Pattern compileDimensionPattern(
            String regex
    ) {
        try {
            return Pattern.compile(regex);
        } catch (Exception exception) {
            return Pattern.compile(
                    "^.+_(overworld|nether|end)$"
            );
        }
    }

    private static boolean isValidDimension(
            String dimension
    ) {
        return "overworld".equals(dimension)
                || "nether".equals(dimension)
                || "end".equals(dimension);
    }

    private static String getString(
            JsonObject object,
            String path
    ) {
        JsonElement element =
                getProperty(
                        object,
                        path
                );

        if (element == null
                || element.isJsonNull()) {
            return null;
        }

        try {
            return element.getAsString();
        } catch (Exception exception) {
            return null;
        }
    }

    private static Double getNumber(
            JsonObject object,
            String path
    ) {
        JsonElement element =
                getProperty(
                        object,
                        path
                );

        if (element == null
                || element.isJsonNull()) {
            return null;
        }

        try {
            return element.getAsDouble();
        } catch (Exception exception) {
            return null;
        }
    }

    private static JsonElement getProperty(
            JsonObject object,
            String path
    ) {
        String normalized =
                path.trim();

        if (normalized.startsWith("$.")) {
            normalized =
                    normalized.substring(2);
        } else if (normalized.startsWith("$")) {
            normalized =
                    normalized.substring(1);
        }

        if (normalized.startsWith(".")) {
            normalized =
                    normalized.substring(1);
        }

        String[] parts =
                normalized.split("\\.");

        JsonElement current = object;

        for (String part : parts) {
            if (part.isBlank()
                    || !current.isJsonObject()) {
                return null;
            }

            JsonObject currentObject =
                    current.getAsJsonObject();

            if (!currentObject.has(part)) {
                return null;
            }

            current =
                    currentObject.get(part);
        }

        return current;
    }

    private static String extractJson(
            String source
    ) {
        String trimmed =
                source.trim();

        if (trimmed.startsWith("var markersDB=")) {
            trimmed =
                    trimmed.substring(
                            "var markersDB=".length()
                    ).trim();
        }

        if (trimmed.endsWith(";")) {
            trimmed =
                    trimmed.substring(
                            0,
                            trimmed.length() - 1
                    ).trim();
        }

        return trimmed;
    }
}