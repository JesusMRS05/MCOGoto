package com.github.jesusmrs05.client.markers;

import com.github.jesusmrs05.client.config.MCOGotoConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

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

        if (containing.size() == 1) {
            teleport(
                    client,
                    config,
                    containing.get(0)
            );
            return;
        }

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
        String command =
                config.getTpCommand()
                        .replace(
                                "{x}",
                                formatCoordinate(marker.x())
                        )
                        .replace(
                                "{y}",
                                formatCoordinate(marker.y())
                        )
                        .replace(
                                "{z}",
                                formatCoordinate(marker.z())
                        )
                        .replace(
                                "{dimension}",
                                marker.dimension()
                        );

        if (client.player != null) {
            client.player.connection.sendCommand(
                    command.startsWith("/")
                            ? command.substring(1)
                            : command
            );
        }
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

        String message = cause.getMessage();

        return message != null
                ? message
                : cause.getClass()
                .getSimpleName();
    }
}