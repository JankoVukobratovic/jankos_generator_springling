package org.jankos.springling.manager;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import lombok.NonNull;
import org.jankos.springling.generator.DTOGenerator;
import org.jankos.springling.model.Field;
import org.jankos.springling.ui.DTOWizard.InitialFieldSelection;
import org.jankos.springling.ui.DTOWizard.SecondaryFieldSelection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * This class is responsible for managing the Data Transfer Object (DTO) generation wizards. It
 * orchestrates the initial field selection, the secondary field selection stages, and optionally
 * the mapper wizard. The user can select fields and specify a destination path for the DTO. After
 * this dialog is finished, the user has the option to open the Mapper Wizard.
 */
public class DTOWizardManager {
  @NonNull private final Project project;
  @NonNull private final List<Field> fields;
  @NonNull private final PsiElement context;

  public DTOWizardManager(
      @NotNull Project project, @NotNull List<Field> fields, @NonNull PsiElement context) {
    this.project = project;
    this.fields = fields;
    this.context = context;
  }

  // TODO MAPPER WIZARD!!!!!!!!!!!!
  /**
   * Opens the Data Transfer Object (DTO) generation wizard. This method orchestrates the DTO
   * generation and Mapper generation process.
   *
   * @return true if the wizard was completed successfully, false if the user canceled at any point.
   */
  public boolean Open() {
    InitialFieldSelection initialFieldSelectionUI = new InitialFieldSelection(project, fields);

    if (!initialFieldSelectionUI.showAndGet()) {
      return false;
    }
    // Get the selected fields from the UI
    List<Field> selectedFields = initialFieldSelectionUI.getSelectedFields();
    selectedFields.forEach(field -> System.out.println(field.toLabel().getText()));

    SecondaryFieldSelection secondaryFieldSelectionUI =
        new SecondaryFieldSelection(project, selectedFields);
    if (!secondaryFieldSelectionUI.showAndGet()) {
      return false; // User canceled the second stage
    }

    // Get the final selected fields and the destination path from the secondary stage
    List<Field> finalSelectedFields = secondaryFieldSelectionUI.getSelectedFields();
    String destinationPath = secondaryFieldSelectionUI.getPackageName();
    String fileName = secondaryFieldSelectionUI.getFileName();

    finalSelectedFields.forEach(field -> System.out.println(field.toLabel().getText()));
    System.out.println("Destination Path: " + destinationPath);
    System.out.println("File Name: " + fileName);

    DTOGenerator dtoGenerator;
    try {
      dtoGenerator = new DTOGenerator(project, context);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    dtoGenerator
        .setPackageName(destinationPath)
        .setClassName(fileName)
        .setFields(finalSelectedFields);

    try {
      dtoGenerator.generateDTO();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    VirtualFileManager.getInstance().syncRefresh();

    return true;
  }
}
