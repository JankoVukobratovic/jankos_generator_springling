package org.jankos.springling.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jankos.springling.ServiceRepositoryWizard;
import org.jetbrains.annotations.NotNull;

public class OpenServiceRepositoryWizardAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    if (project == null) return;

    ServiceRepositoryWizard wizard = new ServiceRepositoryWizard(project);
    if (wizard.showAndGet()) {
      boolean generateRepo = wizard.shouldGenerateRepo();
      boolean generateService = wizard.shouldGenerateService();

      System.out.println("Repo: " + generateRepo);
      System.out.println("Service: " + generateService);
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
