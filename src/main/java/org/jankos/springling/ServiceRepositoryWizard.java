package org.jankos.springling;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;

import javax.swing.*;

public class ServiceRepositoryWizard extends DialogWrapper {
  private final JBCheckBox generateRepoCheckbox = new JBCheckBox("Generate repository", true);
  private final JBCheckBox generateServiceCheckbox = new JBCheckBox("Generate service", true);

  public ServiceRepositoryWizard(Project project) {
    super(project);
    setTitle("Generate Spring Service and JPA Repository");
    init();
  }

  @Override
  protected JComponent createCenterPanel() {
    return buildComponentTab();
  }

  private JComponent buildComponentTab() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.add(generateRepoCheckbox);
    panel.add(generateServiceCheckbox);
    return panel;
  }

  public boolean shouldGenerateRepo() {
    return generateRepoCheckbox.isSelected();
  }

  public boolean shouldGenerateService() {
    return generateServiceCheckbox.isSelected();
  }
}
