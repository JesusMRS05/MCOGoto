package com.github.jesusmrs05.client.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class MCOGotoConfigScreen extends Screen {

    private static final int MAX_SCREEN_WIDTH = 500;
    private static final int MIN_SIDE_MARGIN = 12;

    private static final int BUTTON_WIDTH = 80;
    private static final int FIELD_BUTTON_GAP = 8;

    private static final int ENABLED_TOP = 56;
    private static final int ENABLED_HEIGHT = 20;

    private static final int SCROLL_TOP = 82;
    private static final int SCROLL_BOTTOM_MARGIN = 38;

    private static final int LABEL_HEIGHT = 12;
    private static final int FIELD_HEIGHT = 20;
    private static final int LABEL_FIELD_GAP = 3;
    private static final int ROW_GAP = 6;

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

    private ScrollableLayout scrollableLayout;

    private int contentLeft;
    private int contentWidth;
    private int fieldWidth;
    private int resetLeft;
    private int scrollHeight;

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

        scrollHeight =
                doneY
                        - SCROLL_TOP
                        - SCROLL_BOTTOM_MARGIN;

        /*
         * Enabled stays outside the scroll area.
         */
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

        /*
         * Everything below this point belongs to the
         * ScrollableLayout.
         */
        LinearLayout content =
                LinearLayout.vertical()
                        .spacing(ROW_GAP);

        tpCommandField = addRow(
                content,
                "TP command",
                config.getTpCommand(),
                () -> tpCommandField.setValue(
                        "/tpto {x} {y} {z} {dimension}"
                )
        );

        locationsEndpointField = addRow(
                content,
                "Locations endpoint",
                config.getLocationsEndpoint(),
                () -> locationsEndpointField.setValue(
                        "https://map.minecraftonline.com/markersDB.js"
                )
        );

        markerPathField = addRow(
                content,
                "Marker path",
                config.getMarkerPath(),
                () -> markerPathField.setValue(
                        "$.*.raw[*]"
                )
        );

        namePathField = addRow(
                content,
                "Name path",
                config.getNamePath(),
                () -> namePathField.setValue(
                        "hovertext"
                )
        );

        xPathField = addRow(
                content,
                "X path",
                config.getXPath(),
                () -> xPathField.setValue(
                        "x"
                )
        );

        yPathField = addRow(
                content,
                "Y path",
                config.getYPath(),
                () -> yPathField.setValue(
                        "y"
                )
        );

        zPathField = addRow(
                content,
                "Z path",
                config.getZPath(),
                () -> zPathField.setValue(
                        "z"
                )
        );

        dimensionRegexField = addRow(
                content,
                "Dimension regex",
                config.getDimensionRegex(),
                () -> dimensionRegexField.setValue(
                        "^.+_(overworld|nether|end)$"
                )
        );

        /*
         * The native 26.2 scroll container handles:
         *
         * - mouse wheel scrolling
         * - clipping
         * - widget positions
         * - scrollbar
         * - mouse/focus events
         *
         * We do NOT move widgets ourselves.
         */
        scrollableLayout =
                new ScrollableLayout(
                        minecraft,
                        content,
                        scrollHeight
                );

        scrollableLayout.setMinWidth(
                contentWidth
        );

        scrollableLayout.setX(
                contentLeft
        );

        scrollableLayout.setY(
                SCROLL_TOP
        );

        scrollableLayout.arrangeElements();

        /*
         * Layout#visitWidgets() traverses the layout and exposes
         * the ScrollableLayout's actual container widget to the
         * Screen. The individual fields remain children of that
         * container.
         */
        scrollableLayout.visitWidgets(
                this::addRenderableWidget
        );

        Button doneButton =
                Button.builder(
                        Component.literal("Done"),
                        button -> onClose()
                ).bounds(
                        contentLeft,
                        doneY,
                        contentWidth,
                        DONE_HEIGHT
                ).build();

        addRenderableWidget(doneButton);
    }

    private <T extends EditBox> T addRow(
            LinearLayout content,
            String labelText,
            String value,
            Runnable resetAction
    ) {
        LinearLayout row =
                LinearLayout.vertical()
                        .spacing(LABEL_FIELD_GAP);

        StringWidget label =
                new StringWidget(
                        fieldWidth,
                        LABEL_HEIGHT,
                        Component.literal(labelText),
                        font
                );

        row.addChild(label);

        LinearLayout controls =
                LinearLayout.horizontal()
                        .spacing(FIELD_BUTTON_GAP);

        T field =
                (T) new EditBox(
                        font,
                        0,
                        0,
                        fieldWidth,
                        FIELD_HEIGHT,
                        Component.literal(labelText)
                );

        field.setMaxLength(4096);
        field.setValue(value);

        controls.addChild(field);

        Button reset =
                Button.builder(
                        Component.literal("Reset"),
                        button -> resetAction.run()
                ).bounds(
                        0,
                        0,
                        BUTTON_WIDTH,
                        FIELD_HEIGHT
                ).build();

        controls.addChild(reset);

        row.addChild(controls);

        content.addChild(row);

        return field;
    }

    @Override
    public void onClose() {
        saveConfig();

        if (minecraft != null) {
            minecraft.gui.setScreen(
                    previousScreen
            );
        }
    }

    private void saveConfig() {
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
    }

    private Component getEnabledText() {
        return Component.literal("Enabled: ")
                .append(
                        Component.literal(
                                config.isEnabled()
                                        ? "ON"
                                        : "OFF"
                        )
                );
    }
}