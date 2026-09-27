package com.github.jesusmrs05.client.markers;

import com.github.jesusmrs05.client.config.MCOGotoConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public final class MarkersDbFetcher {

    private static final HttpClient HTTP_CLIENT =
            HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

    private MarkersDbFetcher() {}

    public static CompletableFuture<String> fetch(
            MCOGotoConfig config
    ) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.getLocationsEndpoint()))
                .header("User-Agent", "MCOGoto/1.0")
                .header("Accept", "application/javascript, application/json, */*")
                .GET()
                .build();

        return HTTP_CLIENT.sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString(
                                StandardCharsets.UTF_8
                        )
                )
                .thenApply(response -> {
                    if (response.statusCode() < 200
                            || response.statusCode() >= 300) {
                        throw new IllegalStateException(
                                "HTTP "
                                        + response.statusCode()
                                        + " from locations endpoint."
                        );
                    }

                    return response.body();
                });
    }
}