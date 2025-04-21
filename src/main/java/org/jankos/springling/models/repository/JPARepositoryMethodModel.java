package org.jankos.springling.models.repository;

import lombok.Builder;
import lombok.Value;
import org.jankos.springling.models.FieldModel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value
@Builder
public class JPARepositoryMethodModel {
    String methodName;
    String fqnReturnType;
    @Nullable List<FieldModel> arguments;
}
