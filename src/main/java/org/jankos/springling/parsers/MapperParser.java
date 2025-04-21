package org.jankos.springling.parsers;

import com.intellij.openapi.project.Project;
import lombok.RequiredArgsConstructor;
import org.jankos.springling.models.FieldModel;
import org.jankos.springling.models.MapperModel;
import org.jankos.springling.models.MappingModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MapperParser {

  private final Project project;

  /**
   * Generate a MapperModel from the given source's and target's list of fields. Can be used to
   * generate a mapper for any or both directions.
   *
   * @param source The source list of fields.
   * @param target The target list of fields.
   * @param direction The direction of the mapping. Can be SOURCE_TO_TARGET, TARGET_TO_SOURCE or
   *     BOTH.
   * @return A MapperModel representing the mapping between the source and target fields.
   *
   */
  @Nullable
  public MapperModel parse(
      @NotNull List<FieldModel> source,
      @NotNull List<FieldModel> target,
      @NotNull MappingDirection direction) {

    // source -> target
    if (direction == MappingDirection.SOURCE_TO_TARGET) {
      List<MappingModel> mappingModels = generateMappingModels(source, target);

      if (mappingModels == null) return null;

      return MapperModel.builder()
          .className()
          .packageName()
          .qualifiedName()
          .sourceToTargetMappings(generateMappingModels(source, target))
          .build();
    }

    // target -> source
    if (direction == MappingDirection.TARGET_TO_SOURCE) {
      List<MappingModel> mappingModels = generateMappingModels(target, source);

      if (mappingModels == null) return null;

      return MapperModel.builder()
          .className()
          .packageName()
          .qualifiedName()
          .targetToSourceMappings(generateMappingModels(target, source))
          .build();
    }

    // both
    if (direction == MappingDirection.BOTH) {
      List<MappingModel> sourceToTargetMappings = generateMappingModels(source, target);
      List<MappingModel> targetToSourceMappings = generateMappingModels(target, source);

      if (sourceToTargetMappings == null && targetToSourceMappings == null) return null;

      return MapperModel.builder()
          .className()
          .packageName()
          .qualifiedName()
          .sourceToTargetMappings(sourceToTargetMappings)
          .targetToSourceMappings(targetToSourceMappings)
          .build();
    }

    return null;
  }

  private @Nullable List<MappingModel> generateMappingModels(
      @NotNull List<FieldModel> source, @NotNull List<FieldModel> target) {
    MappingParser mappingParser = new MappingParser(project);

    List<MappingModel> mappings = new ArrayList<>();

    //pair the fields
    //if a field doesnt have a pair, pair it with null.
    //this accounts for both ways




    if (mappings.isEmpty()) return null;
    return mappings;
  }

  public enum MappingDirection {
    SOURCE_TO_TARGET,
    TARGET_TO_SOURCE,
    BOTH
  }
}
