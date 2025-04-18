package org.jankos.springling.models;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Value
@Builder
public class MapperModel {
  String className;
  String packageName;
  String qualifiedName;
  @Nullable List<MappingModel> sourceToTargetMappings;
  @Nullable List<MappingModel> targetToSourceMappings;
}
