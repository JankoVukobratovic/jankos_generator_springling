package org.jankos.springling.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jankos.springling.intellij.IdeMessage;
import org.jankos.springling.intellij.PsiAdapter;
import org.jankos.springling.managers.RestApiManager;
import org.jetbrains.annotations.NotNull;

public class GenerateRestApiAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            IdeMessage.showErrorNotification("No project found.", null);
            return;
        }

        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            IdeMessage.showErrorNotification("No file selected.", project);
            return;
        }

        PsiClass psiClass = anActionEvent.getData(CommonDataKeys.PSI_ELEMENT) instanceof PsiClass
                ? (PsiClass) anActionEvent.getData(CommonDataKeys.PSI_ELEMENT)
                : null;

        if (psiClass == null) {
            IdeMessage.showErrorNotification("Please select a valid entity class.", project);
            return;
        }

        try {
            new RestApiManager().generateRestApi(psiClass, project);
            IdeMessage.showInfoNotification("REST API generated successfully.", project);
        } catch (Exception e) {
            IdeMessage.showErrorNotification("Failed to generate REST API: " + e.getMessage(), project);
        }

    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        PsiClass psiClass = PsiAdapter.getPsiClassFromContext(e);

        boolean enabled = project != null && psiFile != null && PsiAdapter.isEntity(psiClass); // Add check for @Entity etc.
        e.getPresentation().setEnabledAndVisible(enabled);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
