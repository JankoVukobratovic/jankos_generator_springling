package org.jankos.springling.parsers;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import org.jankos.springling.intellij.IdeMessage;
import org.jankos.springling.intellij.PsiAdapter;
import org.jankos.springling.models.DTOModel;
import org.jankos.springling.models.EntityModel;
import org.jankos.springling.models.FieldModel;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class DTOParser {
  private final Project project;

  public DTOParser(Project project) {
    this.project = project;
  }

  /**
   * Generates a DTO model from the given EntityModel. This method excludes the ID field and
   * replaces entity references with their ID types.
   *
   * @param entityModel The EntityModel to generate the DTO from
   * @return The generated DTOModel
   */
  @NotNull
  public DTOModel generateDtoFromEntity(EntityModel entityModel) {
    // Step 1: Exclude the ID field from the DTO
    List<FieldModel> dtoFields =
        entityModel.getFields().stream()
            .filter(field -> !field.isIdField()) // Exclude the ID field
            .map(this::replaceEntityReferencesWithIds)
            .collect(Collectors.toList());

    // Step 2: Create and return the DTOModel
    return DTOModel.builder()
        .className(entityModel.getClassName() + "DTO")
        .packageName(entityModel.getPackageName())
        .qualifiedName(entityModel.getQualifiedName() + "DTO")
        .fields(dtoFields)
        .build();
  }

  /**
   * Replaces entity references in the field with their ID types. If the field is an entity
   * reference, it will be replaced with a new FieldModel where the name is <originalName>Id and the
   * type is the ID type of the referenced entity.
   * If the field is not an entity reference, it will be returned unchanged.
   * If the field is an entity reference but the ID type cannot be resolved, it will return a default java.lang.Object field type.
   * @param field The field to check and possibly replace
   * @return The modified FieldModel with the ID type if it was an entity reference, or the original field
   *
   */
  private FieldModel replaceEntityReferencesWithIds(FieldModel field) {
    if (PsiAdapter.isEntityReference(field)) {

      PsiClass entityPsiClass =
          PsiAdapter.resolvePsiClassFromFQN(project, field.getQualifiedTypeName());

      // Ensure that we successfully retrieved the PsiClass
      if (entityPsiClass == null) {
        IdeMessage.showWarningNotification(
            "Could not resolve entity class: " + field.getQualifiedTypeName(), project);
        return field; // Return the original field if we can't resolve the entity
      }


      // Retrieve the ID field of the entity
      PsiType idFieldPsiType = PsiAdapter.getIdFieldType(entityPsiClass);


      if (idFieldPsiType != null) {
        // Replace the field with a new FieldModel where the name is <originalName>Id
        return FieldModel.builder()
                .name(field.getName() + "Id") // Append "Id" to the field name
                .typeName(idFieldPsiType.getPresentableText()) // ID field type (e.g., Long, Integer)
                .qualifiedTypeName(idFieldPsiType.getCanonicalText()) // Fully qualified ID type name
                .sourcePsiType(idFieldPsiType) // The PsiType of the ID field
                .isIdField(false) // It is a reference, not the actual ID field
                .isNullable(field.isNullable()) // Inherit the nullable flag
                .build();
      } else {
        // If we cannot find an ID field for the referenced entity, return a default field
        return FieldModel.builder()
            .name(field.getName() + "Id")
            .typeName("java.lang.Object") // Fallback to Object if no ID field is found
            .qualifiedTypeName("java.lang.Object")
            .sourcePsiType(null)
            .isIdField(false)
            .isNullable(field.isNullable())
            .build();
      }
    }

    return field;
  }
}
