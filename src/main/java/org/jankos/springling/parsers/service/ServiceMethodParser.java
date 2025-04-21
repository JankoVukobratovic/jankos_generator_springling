package org.jankos.springling.parsers.service;

import com.intellij.openapi.project.Project;
import lombok.RequiredArgsConstructor;
import org.jankos.springling.models.EntityModel;
import org.jankos.springling.models.service.ServiceMethodModel;

import java.util.List;

@RequiredArgsConstructor
public class ServiceMethodParser {
    private final Project project;

    /**
     * Generates default service methods for the given entity model.
     * These include methods for saving, updating, deleting, and finding entities by id, and
     * finding all entities.
     * @param entityModel The entity model for which to generate service methods.
     * @return A list of service method models representing the default service methods.
     */
    public List<ServiceMethodModel> generateDefaults(EntityModel entityModel){
        return List.of(
                ServiceMethodModel.builder()
                        .methodName("save")
                        .fqnReturnType(entityModel.getFqnTypeName())
                        .fqnParameterTypes(List.of(entityModel.getFqnTypeName()))
                        .build(),
                ServiceMethodModel.builder()
                        .methodName("update")
                        .fqnReturnType(entityModel.getFqnTypeName())
                        .fqnParameterTypes(List.of(entityModel.getFqnTypeName()))
                        .build(),
                ServiceMethodModel.builder()
                        .methodName("delete")
                        .fqnReturnType("void")
                        .fqnParameterTypes(List.of(entityModel.getIdField().getFqnTypeName()))
                        .build(),
                ServiceMethodModel.builder()
                        .methodName("findById")
                        .fqnReturnType(entityModel.getFqnTypeName())
                        .fqnParameterTypes(List.of(entityModel.getIdField().getFqnTypeName()))
                        .build(),
                ServiceMethodModel.builder()
                        .methodName("findAll")
                        .fqnReturnType("java.util.List<" + entityModel.getFqnTypeName() + ">")
                        .fqnParameterTypes(null)
                        .build()
        );
    }

}
