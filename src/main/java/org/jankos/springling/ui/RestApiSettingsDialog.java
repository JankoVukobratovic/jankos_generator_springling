package org.jankos.springling.ui;

import com.intellij.openapi.ui.DialogWrapper;
import org.jankos.springling.config.RestApiGenerationConfig;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class RestApiSettingsDialog extends DialogWrapper {
    private JCheckBox hideIdCheckbox;
    private JCheckBox usePaginationCheckbox;

    public RestApiSettingsDialog() {
        super(true); // Use current window as parent
        setTitle("REST API Settings");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1));

        hideIdCheckbox = new JCheckBox("Hide ID on GET endpoints");
        usePaginationCheckbox = new JCheckBox("Use Pagination for GET endpoints");

        panel.add(hideIdCheckbox);
        panel.add(usePaginationCheckbox);

        return panel;
    }

    public RestApiGenerationConfig getRestApiGenerationConfig() {
        return RestApiGenerationConfig.builder()
                .hideIdOnGetEndpoints(hideIdCheckbox.isSelected())
                .usePagination(usePaginationCheckbox.isSelected())
                .build();
    }

}
