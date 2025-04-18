package org.jankos.springling.models;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

@Value
@Builder
public class MappingModel {
    @Nullable FieldModel source;
    @Nullable FieldModel target;
    @Nullable String method;
}
