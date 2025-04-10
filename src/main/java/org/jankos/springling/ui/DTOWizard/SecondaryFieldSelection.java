package org.jankos.springling.ui.DTOWizard;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;
import org.jankos.springling.model.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * This class is responsible for displaying a dialog that allows the user to add or remove fields
 * from the generated Data Transfer Object (DTO). The user can specify a destination path for the
 * DTO. After this dialog is finished, the user has the option to open the Mapper Wizard. It extends
 * the DialogWrapper class from IntelliJ's UI framework.
 */
public class SecondaryFieldSelection extends DialogWrapper {
  private final Project project;
  private final List<Field> fields;

  private final JTextField packageNameTextField = new JTextField();
  private final JTextField filenameTextField = new JTextField();

  private final DefaultListModel<JLabel> listModel = new DefaultListModel<>();
  private final JBList<JLabel> fieldJBList = new JBList<>(listModel);

  private final JButton addButton = new JButton("+");
  private final JButton removeButton = new JButton("-");

  public SecondaryFieldSelection(@Nullable Project project, @NonNull List<Field> fields) {
    super(project);
    setTitle("Generate Data Transfer Object (DTO) - Add or Remove Fields");
    this.fields = fields;
    this.project = project;
    init();
  }

  // TODO : Add validation for package name and class name
  // TODO : Add default package name and class name (package name, at the start being the
  // src/main/java (or /dto) path) but later changes to whatever was last used
  @Override
  protected @Nullable JComponent createCenterPanel() {

    // Populate the list with the existing fields (showing type and name)
    for (Field field : fields) {
      listModel.addElement(field.toLabel()); // Assuming toLabel() gives us a formatted string
    }

    // Set a custom cell renderer to render HTML
    fieldJBList.setCellRenderer(
        (list, value, index, isSelected, cellHasFocus) -> {
          value.setOpaque(true);
          value.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
          value.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
          return value;
        });

    // JList inside a JScrollPane
    JBScrollPane scrollPane = buildFieldListJbScrollPane();

    // Button panel to add/remove fields
    JPanel buttonPanel = buildBottomNavButtonPanel();

    // Destination path field
    JPanel destinationPanel = buildDestinationPanel();

    // Combine all components
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());

    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.add(scrollPane, BorderLayout.CENTER);

    JPanel bottomPanel = new JPanel();

    bottomPanel.setLayout(new BorderLayout(5, 5));
    bottomPanel.add(buttonPanel, BorderLayout.WEST);
    bottomPanel.add(destinationPanel, BorderLayout.SOUTH);

    mainPanel.add(bottomPanel, BorderLayout.SOUTH);

    return mainPanel;
  }

  private @NotNull JPanel buildDestinationPanel() {
    JPanel destinationPanel = new JPanel();
    destinationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    destinationPanel.add(new JLabel("DTO Package name:"));
    packageNameTextField.setPreferredSize(new Dimension(200, 25));
    destinationPanel.add(packageNameTextField);

    destinationPanel.add(new JLabel("Class name:"));
    packageNameTextField.setPreferredSize(new Dimension(200, 25));
    destinationPanel.add(filenameTextField);
    return destinationPanel;
  }

  private @NotNull JPanel buildBottomNavButtonPanel() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

    addButton.addActionListener(e -> showAddFieldDialog());
    removeButton.addActionListener(e -> removeSelectedField());

    buttonPanel.add(addButton);
    buttonPanel.add(removeButton);
    return buttonPanel;
  }

  private @NotNull JBScrollPane buildFieldListJbScrollPane() {
    JBScrollPane scrollPane = new JBScrollPane(fieldJBList);
    scrollPane.setPreferredSize(
        new Dimension(620, 400)); // 620 to avoid awkward horizontal scrollbar
    fields.forEach(field -> fieldJBList.add(field.toLabel()));
    fieldJBList.setPreferredSize(new Dimension(600, 400));
    return scrollPane;
  }

  // TODO : Add validation for field name and field type (e.g. check if field name is not empty,
  // contains forbidden special characters, etc.)
  private void showAddFieldDialog() {
    String newFieldName = JOptionPane.showInputDialog(this.getOwner(), "Enter new field name:");
    if (newFieldName != null && !newFieldName.trim().isEmpty()) {
      // Create a new field object and add it to the list
      if (newFieldName.split(" ").length != 2) {
        return;
      }
      String[] fieldParts = newFieldName.split(" ");

      addNewField(fieldParts[0], fieldParts[1]);
    }
  }

  private void removeSelectedField() {
    int selectedIndex = fieldJBList.getSelectedIndex();
    if (selectedIndex != -1) {
      removeField(selectedIndex);
    }
  }

  public List<Field> getSelectedFields() {
    return fields;
  }

  public String getPackageName() {
    return packageNameTextField.getText().trim();
  }

  public String getFileName() {
    return filenameTextField.getText().trim();
  }

  private void addNewField(String type, String name) {
    Field newField = new Field(name, type);
    fields.add(newField);
    listModel.addElement(newField.toLabel()); // Add to list model
  }

  private void removeField(int index) {
    fields.remove(index);
    listModel.remove(index);
  }
}
