package org.jankos.springling.managers;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jankos.springling.config.RestApiGenerationConfig;
import org.jankos.springling.exceptions.NotAnEntityException;
import org.jankos.springling.intellij.IdeMessage;
import org.jankos.springling.intellij.PsiAdapter;
import org.jankos.springling.models.DTOModel;
import org.jankos.springling.models.EntityModel;
import org.jankos.springling.parsers.DTOParser;
import org.jankos.springling.ui.RestApiSettingsDialog;

public class RestApiManager {
    public void generateRestApi(PsiClass psiClass, Project project) {
        if (!PsiAdapter.isEntity(psiClass)) {
            IdeMessage.showErrorNotification("The selected class is not an entity.", project);
            return;
        }

        EntityModel entityModel;
        try {
            entityModel = PsiAdapter.parseEntity(psiClass);
        } catch (NotAnEntityException e) {
            IdeMessage.showErrorNotification("Failed to parse the entity. Make sure the class is properly annotated.", project);
            return;
        }

        if(entityModel == null){
            IdeMessage.showErrorNotification("The selected class is not an entity.", project);
            return;
        }

        RestApiSettingsDialog dialog = new RestApiSettingsDialog();
        if (!dialog.showAndGet()) {
            // User canceled the dialog
            IdeMessage.showInfoNotification("REST API generation canceled.", project);
            return;
        }

        RestApiGenerationConfig config = dialog.getRestApiGenerationConfig();

        IdeMessage.showInfoNotification("REST API generation started with selected options.", project);

        DTOParser dtoParser = new DTOParser(project);
        DTOModel dtoModel = dtoParser.generateDtoFromEntity(entityModel);


    }
}
