package com.github.jesusmrs05.client.markers;

import com.github.jesusmrs05.client.config.MCOGotoConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.net.URI;
import java.util.List;

public final class MCOGotoResolver {

    private MCOGotoResolver() {
    }

    public static void resolve(
            Minecraft client,
            MCOGotoConfig config,
            String location
    ) {
        MarkersDbFetcher.fetch(config)
                .thenApply(
                        source -> MarkersDbParser.parse(
                                source,
                                config
                        )
                )
                .thenAccept(
                        markers ->
                                runOnClientThread(
                                        client,
                                        () -> handleResults(
                                                client,
                                                config,
                                                location,
                                                markers
                                        )
                                )
                )
                .exceptionally(
                        throwable -> {
                            runOnClientThread(
                                    client,
                                    () -> sendError(
                                            client,
                                            "Failed to load locations: "
                                                    + getMessage(
                                                    throwable
                                            )
                                    )
                            );

                            return null;
                        }
                );
    }

    private static void handleResults(
            Minecraft client,
            MCOGotoConfig config,
            String location,
            List<Marker> markers
    ) {
        List<Marker> exactMatches =
                MarkerSearcher.findExact(
                        markers,
                        location
                );

        if (!exactMatches.isEmpty()) {
            teleport(
                    client,
                    config,
                    exactMatches.get(0)
            );
            return;
        }

        List<Marker> containing =
                MarkerSearcher.findContaining(
                        markers,
                        location
                );

        if (!containing.isEmpty()) {
            sendRecommendations(
                    client,
                    location,
                    containing
            );
            return;
        }

        if (client.player != null) {
            client.player.sendSystemMessage(
                    Component.literal(
                            "No location named \""
                                    + location
                                    + "\" found."
                    ).withStyle(
                            ChatFormatting.RED
                    )
            );
        }
    }

    private static void teleport(
            Minecraft client,
            MCOGotoConfig config,
            Marker marker
    ) {
        if (client.player == null) {
            return;
        }

        ResourceKey<Level> currentDimension =
                client.player.level().dimension();

        ResourceKey<Level> targetDimension =
                getDimensionKey(
                        marker.dimension()
                );

        boolean sameDimension =
                currentDimension.equals(targetDimension);

        double distance = 0.0;

        if (sameDimension) {
            double dx =
                    marker.x()
                            - client.player.getX();

            double dy =
                    marker.y()
                            - client.player.getY();

            double dz =
                    marker.z()
                            - client.player.getZ();

            distance =
                    Math.sqrt(
                            dx * dx
                                    + dy * dy
                                    + dz * dz
                    );
        }

        String command =
                config.getTpCommand()
                        .replace(
                                "{x}",
                                formatCoordinate(
                                        marker.x()
                                )
                        )
                        .replace(
                                "{y}",
                                formatCoordinate(
                                        marker.y()
                                )
                        )
                        .replace(
                                "{z}",
                                formatCoordinate(
                                        marker.z()
                                )
                        )
                        .replace(
                                "{dimension}",
                                marker.dimension()
                        );

        client.player.connection.sendCommand(
                command.startsWith("/")
                        ? command.substring(1)
                        : command
        );

        sendTeleportMessage(
                client,
                marker,
                sameDimension,
                distance
        );
    }

    private static void sendTeleportMessage(
            Minecraft client,
            Marker marker,
            boolean sameDimension,
            double distance
    ) {
        if (client.player == null) {
            return;
        }

        Component locationComponent =
                createLocationComponent(
                        marker
                );

        Component message;

        if (sameDimension) {
            String distanceText =
                    formatDistance(distance);

            message =
                    Component.literal(
                                    "You have been transported "
                            ).withStyle(
                                    ChatFormatting.GREEN
                            )
                            .append(
                                    Component.literal(
                                            distanceText
                                    ).withStyle(
                                            ChatFormatting.WHITE
                                    )
                            )
                            .append(
                                    Component.literal(
                                            "m"
                                    ).withStyle(
                                            ChatFormatting.GREEN
                                    )
                            )
                            .append(
                                    Component.literal(
                                            " to "
                                    ).withStyle(
                                            ChatFormatting.GREEN
                                    )
                            )
                            .append(
                                    locationComponent
                            );

        } else {
            String dimensionName =
                    formatDimensionName(
                            marker.dimension()
                    );

            message =
                    Component.literal(
                                    "You have been transported to "
                            ).withStyle(
                                    ChatFormatting.GREEN
                            )
                            .append(
                                    Component.literal(
                                            dimensionName
                                    ).withStyle(
                                            ChatFormatting.WHITE
                                    )
                            )
                            .append(
                                    Component.literal(
                                            ", to "
                                    ).withStyle(
                                            ChatFormatting.GREEN
                                    )
                            )
                            .append(
                                    locationComponent
                            );
        }

        client.player.sendSystemMessage(
                message
        );
    }

    private static Component createLocationComponent(
            Marker marker
    ) {
        Component location =
                Component.literal(
                        marker.name()
                ).withStyle(
                        style -> {
                            style = style
                                    .withColor(
                                            ChatFormatting.AQUA
                                    )
                                    .withUnderlined(
                                            true
                                    );

                            if (marker.wikiUrl() != null
                                    && !marker.wikiUrl().isBlank()) {
                                try {
                                    style =
                                            style.withClickEvent(
                                                    new ClickEvent.OpenUrl(
                                                            URI.create(
                                                                    marker.wikiUrl()
                                                            )
                                                    )
                                            );

                                    Component hover =
                                            Component.literal(
                                                            marker.name()
                                                    ).withStyle(
                                                            ChatFormatting.WHITE
                                                    )
                                                    .append(
                                                            Component.literal(
                                                                    " - MinecraftOnline Wiki"
                                                            ).withStyle(
                                                                    ChatFormatting.WHITE
                                                            )
                                                    )
                                                    .append(
                                                            Component.literal(
                                                                    "\n"
                                                            )
                                                    )
                                                    .append(
                                                            Component.literal(
                                                                    marker.wikiUrl()
                                                            ).withStyle(
                                                                    ChatFormatting.GRAY
                                                            )
                                                    );

                                    style =
                                            style.withHoverEvent(
                                                    new HoverEvent.ShowText(
                                                            hover
                                                    )
                                            );

                                } catch (IllegalArgumentException ignored) {
                                    // Invalid URL: keep the location
                                    // clickable/hover-free rather than
                                    // breaking the teleport message.
                                }
                            }

                            return style;
                        }
                );

        return location;
    }

    private static ResourceKey<Level> getDimensionKey(
            String dimension
    ) {
        return switch (dimension) {
            case "nether" ->
                    Level.NETHER;

            case "end" ->
                    Level.END;

            case "overworld" ->
                    Level.OVERWORLD;

            default ->
                    Level.OVERWORLD;
        };
    }

    private static String formatDimensionName(
            String dimension
    ) {
        return switch (dimension) {
            case "nether" ->
                    "Nether";

            case "end" ->
                    "End";

            case "overworld" ->
                    "Overworld";

            default ->
                    "Overworld";
        };
    }

    private static String formatDistance(
            double distance
    ) {
        return Long.toString(
                Math.round(distance)
        );
    }

    private static String formatCoordinate(
            double value
    ) {
        if (value == Math.rint(value)) {
            return Long.toString(
                    (long) value
            );
        }

        return Double.toString(value);
    }

    private static void sendRecommendations(
            Minecraft client,
            String location,
            List<Marker> results
    ) {
        if (client.player == null) {
            return;
        }

        Component message =
                Component.literal(
                        "There are "
                                + results.size()
                                + " locations matching \""
                ).withStyle(
                        ChatFormatting.YELLOW
                ).append(
                        Component.literal(
                                location
                        ).withStyle(
                                ChatFormatting.WHITE
                        )
                ).append(
                        Component.literal(
                                "\":"
                        ).withStyle(
                                ChatFormatting.YELLOW
                        )
                );

        client.player.sendSystemMessage(
                message
        );

        for (Marker marker : results) {
            client.player.sendSystemMessage(
                    Component.literal(
                            "* " + marker.name()
                    )
            );
        }
    }

    private static void sendError(
            Minecraft client,
            String message
    ) {
        if (client.player != null) {
            client.player.sendSystemMessage(
                    Component.literal(message)
                            .withStyle(
                                    ChatFormatting.RED
                            )
            );
        }
    }

    private static void runOnClientThread(
            Minecraft client,
            Runnable runnable
    ) {
        client.execute(runnable);
    }

    private static String getMessage(
            Throwable throwable
    ) {
        Throwable cause = throwable;

        while (cause.getCause() != null) {
            cause = cause.getCause();
        }

        String message =
                cause.getMessage();

        return message != null
                ? message
                : cause.getClass()
                .getSimpleName();
    }
}