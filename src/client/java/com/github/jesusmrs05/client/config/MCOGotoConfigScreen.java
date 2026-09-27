package com.github.jesusmrs05.client.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class MCOGotoConfigScreen extends Screen {

    private static final int MAX_SCREEN_WIDTH = 500;
    private static final int MIN_SIDE_MARGIN = 12;

    private static final int BUTTON_WIDTH = 80;
    private static final int FIELD_BUTTON_GAP = 8;

    private static final int HEADER_TOP = 18;
    private static final int DESCRIPTION_Y = 31;
    private static final int SECOND_DESCRIPTION_Y = 43;

    private static final int ENABLED_TOP = 56;
    private static final int ENABLED_HEIGHT = 20;

    private static final int SETTINGS_TOP = 82;
    private static final int ROW_MIN_HEIGHT = 34;

    private static final int DONE_HEIGHT = 20;
    private static final int DONE_BOTTOM_MARGIN = 12;

    private final Screen previousScreen;
    private final MCOGotoConfig config;

    private EditBox tpCommandField;
    private EditBox locationsEndpointField;
    private EditBox markerPathField;
    private EditBox namePathField;
    private EditBox xPathField;
    private EditBox yPathField;
    private EditBox zPathField;
    private EditBox dimensionRegexField;

    private Button enabledButton;

    private int contentLeft;
    private int contentWidth;
    private int fieldWidth;
    private int resetLeft;
    private int rowHeight;

    public MCOGotoConfigScreen(
            Screen previousScreen,
            MCOGotoConfig config
    ) {
        super(Component.literal("MCO Goto"));

        this.previousScreen = previousScreen;
        this.config = config;
    }

    @Override
    protected void init() {
        super.init();

        contentWidth = Math.min(
                MAX_SCREEN_WIDTH,
                width - MIN_SIDE_MARGIN * 2
        );

        contentLeft =
                (width - contentWidth) / 2;

        resetLeft =
                contentLeft
                        + contentWidth
                        - BUTTON_WIDTH;

        fieldWidth =
                resetLeft
                        - contentLeft
                        - FIELD_BUTTON_GAP;

        int doneY =
                height
                        - DONE_BOTTOM_MARGIN
                        - DONE_HEIGHT;

        int availableSettingsHeight =
                doneY - SETTINGS_TOP - 8;

        rowHeight =
                Math.max(
                        ROW_MIN_HEIGHT,
                        availableSettingsHeight / 8
                );

        enabledButton = Button.builder(
                getEnabledText(),
                button -> {
                    config.setEnabled(
                            !config.isEnabled()
                    );

                    button.setMessage(
                            getEnabledText()
                    );
                }
        ).bounds(
                contentLeft,
                ENABLED_TOP,
                contentWidth,
                ENABLED_HEIGHT
        ).build();

        addRenderableWidget(enabledButton);

        int y = SETTINGS_TOP;

        tpCommandField = createField(
                "TP command",
                y,
                config.getTpCommand()
        );

        addRenderableWidget(tpCommandField);

        addRenderableWidget(
                createResetButton(
                        y,
                        () -> tpCommandField.setValue(
                                "/tpto {x} {y} {z} {dimension}"
                        )
                )
        );

        y += rowHeight;

        locationsEndpointField = createField(
                "Locations endpoint",
                y,
                config.getLocationsEndpoint()
        );

        addRenderableWidget(locationsEndpointField);

        addRenderableWidget(
                createResetButton(
                        y,
                        () -> locationsEndpointField.setValue(
                                "https://map.minecraftonline.com/markersDB.js"
                        )
                )
        );

        y += rowHeight;

        markerPathField = createField(
                "Marker path",
                y,
                config.getMarkerPath()
        );

        addRenderableWidget(markerPathField);

        addRenderableWidget(
                createResetButton(
                        y,
                        () -> markerPathField.setValue(
                                "$.*.raw[*]"
                        )
                )
        );

        y += rowHeight;

        namePathField = createField(
                "Name path",
                y,
                config.getNamePath()
        );

        addRenderableWidget(namePathField);

        addRenderableWidget(
                createResetButton(
                        y,
                        () -> namePathField.setValue(
                                "hovertext"
                        )
                )
        );

        y += rowHeight;

        xPathField = createField(
                "X path",
                y,
                config.getXPath()
        );

        addRenderableWidget(xPathField);

        addRenderableWidget(
                createResetButton(
                        y,
                        () -> xPathField.setValue("x")
                )
        );

        y += rowHeight;

        yPathField = createField(
                "Y path",
                y,
                config.getYPath()
        );

        addRenderableWidget(yPathField);

        addRenderableWidget(
                createResetButton(
                        y,
                        () -> yPathField.setValue("y")
                )
        );

        y += rowHeight;

        zPathField = createField(
                "Z path",
                y,
                config.getZPath()
        );

        addRenderableWidget(zPathField);

        addRenderableWidget(
                createResetButton(
                        y,
                        () -> zPathField.setValue("z")
                )
        );

        y += rowHeight;

        dimensionRegexField = createField(
                "Dimension regex",
                y,
                config.getDimensionRegex()
        );

        addRenderableWidget(dimensionRegexField);

        addRenderableWidget(
                createResetButton(
                        y,
                        () -> dimensionRegexField.setValue(
                                "^.+_(overworld|nether|end)$"
                        )
                )
        );

        addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        button -> onClose()
                ).bounds(
                        contentLeft,
                        doneY,
                        contentWidth,
                        DONE_HEIGHT
                ).build()
        );

        setInitialFocus(tpCommandField);
    }

    private EditBox createField(
            String name,
            int rowTop,
            String value
    ) {
        EditBox field = new EditBox(
                font,
                contentLeft,
                rowTop + 11,
                fieldWidth,
                20,
                Component.literal(name)
        );

        field.setMaxLength(4096);
        field.setValue(value);

        return field;
    }

    private Button createResetButton(
            int rowTop,
            Runnable resetAction
    ) {
        return Button.builder(
                Component.literal("Reset"),
                button -> resetAction.run()
        ).bounds(
                resetLeft,
                rowTop + 11,
                BUTTON_WIDTH,
                20
        ).build();
    }

    private Component getEnabledText() {
        return Component.literal(
                config.isEnabled()
                        ? "Enabled: ON"
                        : "Enabled: OFF"
        );
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.extractRenderState(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        graphics.text(
                font,
                Component.literal("MCO Goto Configuration"),
                contentLeft,
                HEADER_TOP,
                0xFFFFFFFF,
                true
        );

        graphics.text(
                font,
                Component.literal(
                        "{x}, {y}, {z}, and {dimension} are replaced with the location's values."
                ),
                contentLeft,
                DESCRIPTION_Y,
                0xFFAAAAAA,
                false
        );

        graphics.text(
                font,
                Component.literal(
                        "Paths control how marker data is extracted from markersDB.js."
                ),
                contentLeft,
                SECOND_DESCRIPTION_Y,
                0xFFAAAAAA,
                false
        );

        drawLabel(
                graphics,
                "TP command template",
                SETTINGS_TOP
        );

        drawLabel(
                graphics,
                "Locations endpoint",
                SETTINGS_TOP + rowHeight
        );

        drawLabel(
                graphics,
                "Marker path",
                SETTINGS_TOP + rowHeight * 2
        );

        drawLabel(
                graphics,
                "Name path",
                SETTINGS_TOP + rowHeight * 3
        );

        drawLabel(
                graphics,
                "X path",
                SETTINGS_TOP + rowHeight * 4
        );

        drawLabel(
                graphics,
                "Y path",
                SETTINGS_TOP + rowHeight * 5
        );

        drawLabel(
                graphics,
                "Z path",
                SETTINGS_TOP + rowHeight * 6
        );

        drawLabel(
                graphics,
                "Dimension regex",
                SETTINGS_TOP + rowHeight * 7
        );
    }

    private void drawLabel(
            GuiGraphicsExtractor graphics,
            String text,
            int rowTop
    ) {
        graphics.text(
                font,
                Component.literal(text),
                contentLeft,
                rowTop,
                0xFFFFFFFF,
                true
        );
    }

    @Override
    public void onClose() {
        config.setTpCommand(
                tpCommandField.getValue()
        );

        config.setLocationsEndpoint(
                locationsEndpointField.getValue()
        );

        config.setMarkerPath(
                markerPathField.getValue()
        );

        config.setNamePath(
                namePathField.getValue()
        );

        config.setXPath(
                xPathField.getValue()
        );

        config.setYPath(
                yPathField.getValue()
        );

        config.setZPath(
                zPathField.getValue()
        );

        config.setDimensionRegex(
                dimensionRegexField.getValue()
        );

        config.save();

        if (minecraft != null) {
            minecraft.gui.setScreen(
                    previousScreen
            );
        }
    }
}