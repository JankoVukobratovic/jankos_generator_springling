package org.jankos.springling.managers;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jankos.springling.options.ControllerGenerationOptions;
import org.jankos.springling.exceptions.NotAnEntityException;
import org.jankos.springling.adapters.IdeMessageAdapter;
import org.jankos.springling.adapters.PsiAdapter;
import org.jankos.springling.models.EntityModel;
import org.jankos.springling.ui.RestApiSettingsDialog;

public class RestApiManager {
    public void generateRestApi(PsiClass psiClass, Project project) {
        if (!PsiAdapter.isEntity(psiClass)) {
            IdeMessageAdapter.showErrorNotification("The selected class is not an entity.", project);
            return;
        }

        EntityModel entityModel;
        try {
            entityModel = PsiAdapter.parseEntity(psiClass);
        } catch (NotAnEntityException e) {
            IdeMessageAdapter.showErrorNotification("Failed to parse the entity. Make sure the class is properly annotated.", project);
            return;
        }

        if(entityModel == null){
            IdeMessageAdapter.showErrorNotification("The selected class is not an entity.", project);
            return;
        }

        RestApiSettingsDialog dialog = new RestApiSettingsDialog();
        if (!dialog.showAndGet()) {
            // User canceled the dialog
            IdeMessageAdapter.showInfoNotification("REST API generation canceled.", project);
            return;
        }

        ControllerGenerationOptions config = dialog.getRestApiGenerationConfig();

        IdeMessageAdapter.showInfoNotification("REST API generation started with selected options.", project);

    }
}
