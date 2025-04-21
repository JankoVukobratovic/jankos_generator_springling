package org.jankos.springling.parsers;

import com.intellij.openapi.project.Project;
import lombok.RequiredArgsConstructor;
import org.jankos.springling.models.FieldModel;
import org.jankos.springling.models.MappingModel;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RequiredArgsConstructor
public class MappingParser {
  private final Project project;

  /**
   * Generate a MappingModel from the given source and target fields
   *
   * @param source The source field or null if the target field does not exist
   * @param target The target field or null if the source field does not exist
   * @return A MappingModel representing the mapping between the source and target fields, or null
   *     if both fields are null references.
   */
  public @Nullable MappingModel parse(@Nullable FieldModel source, @Nullable FieldModel target) {

    if (source == null && target == null) {
      return null;
    }

    if (source == null) {
      return MappingModel.builder()
          .source(null)
          .target(target)
          .mappingArguments(List.of("target = \"" + target.getName() + "\"", "ignore = true"))
          .build();
    }

    if (target == null) {

      return MappingModel.builder()
          .source(source)
          .target(null)
          .mappingArguments(List.of("target = \"" + source.getName() + "\"", "ignore = true"))
          .build();
    }

    // if the names are the same, and the types are the same, we can just assume there is no special
    // mapping
    if (source.getName().equals(target.getName())
        && source.getTypeName().equals(target.getTypeName())) {
      return MappingModel.builder().source(source).target(target).build();
    }

    // TODO implement resolving id fields

    if (target.getName().endsWith("Id")) {
      String baseName = target.getName().substring(0, target.getName().length() - 2); // remove 'Id'
      if (source.getName().equalsIgnoreCase(baseName)) {
        return MappingModel.builder()
            .source(source)
            .target(target)
            .mappingArguments(
                List.of(
                    "target = \"" + target.getName() + "\"",
                    "source = \"" + source.getName() + ".id\""))
            .build();
      }
    }

    if (source.getName().endsWith("Id")) {
      String baseName = source.getName().substring(0, source.getName().length() - 2); // remove 'Id'
      if (target.getName().equalsIgnoreCase(baseName)) {

        //TODO figure this out or do an altarnate way with dependency injection (injecting the service into the mapper class)
        String serviceName = "";
        String methodCall = "";



        return MappingModel.builder()
            .source(source)
            .target(target)
            .mappingArguments(
                List.of(
                    "target = \"" + target.getName() + ".id\"",
                    "expression = \"java(" + methodCall + ")\""))
                .requiredService(target.getTypeName() + "Service")
            .build();
      }
    }


    return null;
  }
}
