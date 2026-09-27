package com.github.jesusmrs05.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MCOGotoConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path CONFIG_PATH =
            FabricLoader.getInstance()
                    .getConfigDir()
                    .resolve("mco-goto.json");

    private boolean enabled = true;

    private String tpCommand =
            "/tpto {x} {y} {z} {dimension}";

    private String locationsEndpoint =
            "https://map.minecraftonline.com/markersDB.js";

    private String markerPath =
            "$.*.raw[*]";

    private String namePath =
            "hovertext";

    private String xPath =
            "x";

    private String yPath =
            "y";

    private String zPath =
            "z";

    private String dimensionRegex =
            "^.+_(overworld|nether|end)$";

    private MCOGotoConfig() {
    }

    public static MCOGotoConfig load() {
        MCOGotoConfig config = new MCOGotoConfig();

        if (!Files.exists(CONFIG_PATH)) {
            config.save();
            return config;
        }

        try {
            String json = Files.readString(CONFIG_PATH);
            JsonObject object =
                    JsonParser.parseString(json)
                            .getAsJsonObject();

            if (object.has("enabled")) {
                config.enabled =
                        object.get("enabled")
                                .getAsBoolean();
            }

            if (object.has("tpCommand")) {
                String value =
                        object.get("tpCommand")
                                .getAsString();

                if (!value.isBlank()) {
                    config.tpCommand = value;
                }
            }

            if (object.has("locationsEndpoint")) {
                String value =
                        object.get("locationsEndpoint")
                                .getAsString();

                if (!value.isBlank()) {
                    config.locationsEndpoint = value;
                }
            }

            if (object.has("markerPath")) {
                String value =
                        object.get("markerPath")
                                .getAsString();

                if (!value.isBlank()) {
                    config.markerPath = value;
                }
            }

            if (object.has("namePath")) {
                String value =
                        object.get("namePath")
                                .getAsString();

                if (!value.isBlank()) {
                    config.namePath = value;
                }
            }

            if (object.has("xPath")) {
                String value =
                        object.get("xPath")
                                .getAsString();

                if (!value.isBlank()) {
                    config.xPath = value;
                }
            }

            if (object.has("yPath")) {
                String value =
                        object.get("yPath")
                                .getAsString();

                if (!value.isBlank()) {
                    config.yPath = value;
                }
            }

            if (object.has("zPath")) {
                String value =
                        object.get("zPath")
                                .getAsString();

                if (!value.isBlank()) {
                    config.zPath = value;
                }
            }

            if (object.has("dimensionRegex")) {
                String value =
                        object.get("dimensionRegex")
                                .getAsString();

                if (!value.isBlank()) {
                    config.dimensionRegex = value;
                }
            }

            /*
             * Rewrite the config so newly introduced defaults
             * are persisted immediately.
             */
            config.save();

        } catch (Exception ignored) {
            /*
             * If the config cannot be read, keep the default values.
             */
        }

        return config;
    }

    public void save() {
        JsonObject object = new JsonObject();

        object.addProperty("enabled", enabled);
        object.addProperty("tpCommand", tpCommand);
        object.addProperty("locationsEndpoint", locationsEndpoint);
        object.addProperty("markerPath", markerPath);
        object.addProperty("namePath", namePath);
        object.addProperty("xPath", xPath);
        object.addProperty("yPath", yPath);
        object.addProperty("zPath", zPath);
        object.addProperty("dimensionRegex", dimensionRegex);

        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            Files.writeString(
                    CONFIG_PATH,
                    GSON.toJson(object)
            );

        } catch (IOException ignored) {
            // Config saving failure should not prevent the mod from running.
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTpCommand() {
        return tpCommand;
    }

    public void setTpCommand(String tpCommand) {
        this.tpCommand = tpCommand;
    }

    public String getLocationsEndpoint() {
        return locationsEndpoint;
    }

    public void setLocationsEndpoint(String locationsEndpoint) {
        this.locationsEndpoint = locationsEndpoint;
    }

    public String getMarkerPath() {
        return markerPath;
    }

    public void setMarkerPath(String markerPath) {
        this.markerPath = markerPath;
    }

    public String getNamePath() {
        return namePath;
    }

    public void setNamePath(String namePath) {
        this.namePath = namePath;
    }

    public String getXPath() {
        return xPath;
    }

    public void setXPath(String xPath) {
        this.xPath = xPath;
    }

    public String getYPath() {
        return yPath;
    }

    public void setYPath(String yPath) {
        this.yPath = yPath;
    }

    public String getZPath() {
        return zPath;
    }

    public void setZPath(String zPath) {
        this.zPath = zPath;
    }

    public String getDimensionRegex() {
        return dimensionRegex;
    }

    public void setDimensionRegex(String dimensionRegex) {
        this.dimensionRegex = dimensionRegex;
    }

    public void reset() {
        enabled = true;
        tpCommand = "/tpto {x} {y} {z} {dimension}";
        locationsEndpoint =
                "https://map.minecraftonline.com/markersDB.js";
        markerPath = "$.*.raw[*]";
        namePath = "hovertext";
        xPath = "x";
        yPath = "y";
        zPath = "z";
        dimensionRegex =
                "^.+_(overworld|nether|end)$";
    }
}