package com.github.jesusmrs05.client.markers;

public record Marker(
        String name,
        double x,
        double y,
        double z,
        String dimension
) {}