package org.jankos.springling.managers;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jankos.springling.config.RestApiGenerationConfig;
import org.jankos.springling.intellij.IdeMessage;
import org.jankos.springling.ui.RestApiSettingsDialog;

public class RestApiManager {
    public void generateRestApi(PsiClass psiClass, Project project) {
        RestApiSettingsDialog dialog = new RestApiSettingsDialog();
        if (!dialog.showAndGet()) {
            // User canceled the dialog
            IdeMessage.showInfoNotification("REST API generation canceled.", project);
            return;
        }

        RestApiGenerationConfig config = dialog.getRestApiGenerationConfig();

        IdeMessage.showInfoNotification("REST API generation started with selected options.", project);
    }
}
