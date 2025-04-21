package org.jankos.springling.models.service;

import lombok.Builder;
import lombok.Value;
import org.jankos.springling.models.FieldModel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value
@Builder
public class ServiceMethodModel {
    String methodName;
    String fqnReturnType; //TODO is this necessary? Dont all service functions return the entity type?
    @Nullable List<FieldModel> arguments;
}
