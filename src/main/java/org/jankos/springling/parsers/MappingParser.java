package org.jankos.springling.parsers;

import com.intellij.openapi.project.Project;
import lombok.RequiredArgsConstructor;
import org.jankos.springling.models.FieldModel;
import org.jankos.springling.models.MappingModel;

@RequiredArgsConstructor
public class MappingParser {
    private final Project project;

    /**
     * Generate a MappingModel from the given source and target fields
     * @param source The source field
     * @param target The target field
     * @return A MappingModel representing the mapping between the source and target fields
     */
    public MappingModel parse(FieldModel source, FieldModel target){

    }
}
