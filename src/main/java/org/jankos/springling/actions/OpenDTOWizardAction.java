package org.jankos.springling.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jankos.springling.model.Field;
import org.jankos.springling.manager.DTOWizardManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OpenDTOWizardAction extends AnAction {
  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project == null) return;

    PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
    Editor editor = e.getData(CommonDataKeys.EDITOR);

    if (file instanceof PsiJavaFile && editor != null) {
      PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
      PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
      if (psiClass != null) {
        // Extract the fields of the class
        List<Field> fields = new ArrayList<>();
        for (PsiField field : psiClass.getFields()) {
          fields.add(new Field(field.getName(), field.getType().toString().substring(8)));
        }

        // Pass the fields to the wizard
        DTOWizardManager wizard = new DTOWizardManager(project, fields, element);
        if (wizard.Open()) {
          System.out.println("Wizard finished successfully.");
        } else {
          System.out.println("Wizard was cancelled.");
        }
      }
    }
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
    Editor editor = e.getData(CommonDataKeys.EDITOR);
    boolean isEntityClass = false;

    if (file instanceof PsiJavaFile && editor != null) {
      PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
      PsiClass psiClass = PsiTreeUtil.getParentOfType(element, PsiClass.class);
      if (psiClass != null && psiClass.hasAnnotation("jakarta.persistence.Entity")) {
        isEntityClass = true;
      }
    }

    e.getPresentation().setEnabledAndVisible(isEntityClass);
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.BGT;
  }
}
