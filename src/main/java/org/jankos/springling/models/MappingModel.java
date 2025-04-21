package org.jankos.springling.models;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value
@Builder
public class MappingModel {
    @Nullable FieldModel source;
    @Nullable FieldModel target;
    @Nullable List<String> mappingArguments;
    @Nullable String requiredService;
}
