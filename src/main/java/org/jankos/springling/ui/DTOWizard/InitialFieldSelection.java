package org.jankos.springling.ui.DTOWizard;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.jankos.springling.model.Field;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for displaying a dialog that allows the user to select fields from an
 * entity to be included in the generated Data Transfer Object (DTO). After this dialog is finished,
 * it opens the SecondaryFieldSelection It extends the DialogWrapper class from IntelliJ's UI
 * framework.
 */
public class InitialFieldSelection extends DialogWrapper {

  private final JPanel dtoFieldPanel = new JPanel();
  List<Field> fields;
  private final List<JBCheckBox> fieldCheckboxes = new ArrayList<>();

  public InitialFieldSelection(@Nullable Project project, @NonNull List<Field> fields) {
    super(project);
    setTitle("Generate Data Transfer Object (DTO) - Select Fields from Entity");
    this.fields = fields;
    init();
  }

  // TODO : Add an option to convert references to other entities to just their IDs (dynamically
  // select field based on @id annotation). This should be another panel.
  @Override
  protected @Nullable JComponent createCenterPanel() {
    return buildDtoPanel();
  }

  private JComponent buildDtoPanel() {
    dtoFieldPanel.setLayout(new BoxLayout(dtoFieldPanel, BoxLayout.Y_AXIS));

    for (Field field : fields) {
      JBCheckBox checkBox = new JBCheckBox();
      checkBox.setText(field.toLabel().getText());
      checkBox.setSelected(true);
      fieldCheckboxes.add(checkBox);
      dtoFieldPanel.add(checkBox);
    }

    JBScrollPane scrollPane = new JBScrollPane(dtoFieldPanel);
    scrollPane.setPreferredSize(new Dimension(900, 600));
    return scrollPane;
  }

  public List<Field> getSelectedFields() {
    List<Field> selectedFields = new ArrayList<>();
    for (int i = 0; i < fieldCheckboxes.size(); i++) {
      if (fieldCheckboxes.get(i).isSelected()) {
        selectedFields.add(fields.get(i));
      }
    }
    return selectedFields;
  }
}
