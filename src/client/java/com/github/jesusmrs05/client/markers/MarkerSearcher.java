package com.github.jesusmrs05.client.markers;

import java.util.List;
import java.util.Locale;

public final class MarkerSearcher {

    private MarkerSearcher() {
    }

    public static List<Marker> findExact(
            List<Marker> markers,
            String query
    ) {
        String normalizedQuery =
                query.trim().toLowerCase(Locale.ROOT);

        return markers.stream()
                .filter(
                        marker ->
                                marker.name()
                                        .toLowerCase(Locale.ROOT)
                                        .equals(normalizedQuery)
                )
                .toList();
    }

    public static List<Marker> findContaining(
            List<Marker> markers,
            String query
    ) {
        String normalizedQuery =
                query.trim().toLowerCase(Locale.ROOT);

        return markers.stream()
                .filter(
                        marker ->
                                marker.name()
                                        .toLowerCase(Locale.ROOT)
                                        .contains(normalizedQuery)
                )
                .toList();
    }
}