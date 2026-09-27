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

    private String searchEndpoint =
            "https://minecraftonline.com/w/api.php?action=query&list=search&format=json&srsearch={query}";

    private String pageEndpoint =
            "https://minecraftonline.com/w/api.php?action=query&prop=revisions&rvprop=content&rvslots=main&format=json&titles={page}";

    private String xExtraction = "";

    private String yExtraction = "";

    private String zExtraction = "";

    private String dimensionExtraction = "";

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
            JsonObject object = JsonParser.parseString(json).getAsJsonObject();

            if (object.has("enabled")) {
                config.enabled = object.get("enabled").getAsBoolean();
            }

            if (object.has("tpCommand")) {
                config.tpCommand = object.get("tpCommand").getAsString();
            }

            if (object.has("searchEndpoint")) {
                config.searchEndpoint =
                        object.get("searchEndpoint").getAsString();
            }

            if (object.has("pageEndpoint")) {
                config.pageEndpoint =
                        object.get("pageEndpoint").getAsString();
            }

            if (object.has("xExtraction")) {
                config.xExtraction =
                        object.get("xExtraction").getAsString();
            }

            if (object.has("yExtraction")) {
                config.yExtraction =
                        object.get("yExtraction").getAsString();
            }

            if (object.has("zExtraction")) {
                config.zExtraction =
                        object.get("zExtraction").getAsString();
            }

            if (object.has("dimensionExtraction")) {
                config.dimensionExtraction =
                        object.get("dimensionExtraction").getAsString();
            }
        } catch (Exception ignored) {
            // If the config cannot be read, keep the default values.
        }

        return config;
    }

    public void save() {
        JsonObject object = new JsonObject();

        object.addProperty("enabled", enabled);
        object.addProperty("tpCommand", tpCommand);
        object.addProperty("searchEndpoint", searchEndpoint);
        object.addProperty("pageEndpoint", pageEndpoint);
        object.addProperty("xExtraction", xExtraction);
        object.addProperty("yExtraction", yExtraction);
        object.addProperty("zExtraction", zExtraction);
        object.addProperty("dimensionExtraction", dimensionExtraction);

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

    public String getSearchEndpoint() {
        return searchEndpoint;
    }

    public void setSearchEndpoint(String searchEndpoint) {
        this.searchEndpoint = searchEndpoint;
    }

    public String getPageEndpoint() {
        return pageEndpoint;
    }

    public void setPageEndpoint(String pageEndpoint) {
        this.pageEndpoint = pageEndpoint;
    }

    public String getXExtraction() {
        return xExtraction;
    }

    public void setXExtraction(String xExtraction) {
        this.xExtraction = xExtraction;
    }

    public String getYExtraction() {
        return yExtraction;
    }

    public void setYExtraction(String yExtraction) {
        this.yExtraction = yExtraction;
    }

    public String getZExtraction() {
        return zExtraction;
    }

    public void setZExtraction(String zExtraction) {
        this.zExtraction = zExtraction;
    }

    public String getDimensionExtraction() {
        return dimensionExtraction;
    }

    public void setDimensionExtraction(String dimensionExtraction) {
        this.dimensionExtraction = dimensionExtraction;
    }

    public void reset() {
        enabled = true;
        tpCommand = "/tpto {x} {y} {z} {dimension}";
        searchEndpoint =
                "https://minecraftonline.com/w/api.php?action=query&list=search&format=json&srsearch={query}";
        pageEndpoint =
                "https://minecraftonline.com/w/api.php?action=query&prop=revisions&rvprop=content&rvslots=main&format=json&titles={page}";
        xExtraction = "";
        yExtraction = "";
        zExtraction = "";
        dimensionExtraction = "";
    }
}