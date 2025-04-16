package org.jankos.springling.intellij;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jankos.springling.exceptions.NotAnEntityException;
import org.jankos.springling.models.EntityModel;
import org.jankos.springling.models.FieldModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PsiAdapter {


  /**
   * Get the PsiClass from the context of the action event.
   * @param e The action event
   * @return The PsiClass if found, otherwise null
   */
  public static PsiClass getPsiClassFromContext(@NotNull AnActionEvent e) {
    PsiElement element = e.getDataContext().getData(com.intellij.openapi.actionSystem.CommonDataKeys.PSI_ELEMENT);
    if (element instanceof PsiClass) {
      return (PsiClass) element;
    }
    return PsiTreeUtil.getParentOfType(element, PsiClass.class);
  }


  /**
   * Check if the PsiClass is an entity class.
   * This checks for the presence of the @Entity annotation.
   * @param psiClass The PsiClass to check
   * @return true if the class is an entity, false otherwise
   */
  public static boolean isEntity(PsiClass psiClass) {
    if (psiClass == null) {
      return false;
    }
    return psiClass.hasAnnotation("jakarta.persistence.Entity") || psiClass.hasAnnotation("javax.persistence.Entity");
  }


  /**
   * Parse the entity class and extract its fields.
   * This method will also check for the presence of the @Entity annotation.
   * If the class is not an entity, it will return null.
   * @param psiClass
   * @return EntityModel or null
   * @throws NotAnEntityException if the class is not an entity
   */
  public static @Nullable EntityModel parseEntity(@NotNull PsiClass psiClass) {
    if(!isEntity(psiClass)) {
      throw new NotAnEntityException();
    }

    List<FieldModel> fields = new ArrayList<>();
    FieldModel idField = null;
    String packageName = null;
    if (psiClass.getContainingFile() instanceof PsiJavaFile) {
      packageName = ((PsiJavaFile) psiClass.getContainingFile()).getPackageName();
    }

    for (PsiField field : psiClass.getAllFields()) { // Use getAllFields to include inherited
      if (field.hasModifierProperty(PsiModifier.STATIC)) {
        continue;
      }

      FieldModel fieldModel = parseField(field);
      fields.add(fieldModel);
      if (fieldModel.isIdField()) {
        idField = fieldModel;
      }
    }

    return EntityModel.builder()
        .className(psiClass.getName())
        .packageName(packageName)
        .qualifiedName(psiClass.getQualifiedName())
        .fields(fields)
        .idField(idField)
        .sourcePsiClass(psiClass)
        .build();
  }

  /**
   * Parse a field and extract its properties.
   * This method checks for the presence of the @Id annotation to determine if the field is an ID field.
   * It also checks for annotations like @NotNull or @Column(nullable=false) to determine if the field is nullable.
   * @param field The PsiField to parse
   * @return FieldModel
   */
  private static FieldModel parseField(@NotNull PsiField field) {
    PsiType type = field.getType();
    boolean isId =
        field.hasAnnotation("jakarta.persistence.Id")
            || field.hasAnnotation("javax.persistence.Id");
    // check annotations like @NotNull
    boolean isNullable =
        !(field.hasAnnotation("jakarta.validation.constraints.NotNull")
            || field.hasAnnotation("javax.validation.constraints.NotNull"));

    // check for @Column(nullable=false)
    PsiAnnotation columnAnnotation = field.getAnnotation("jakarta.persistence.Column");
    if (columnAnnotation == null) {
      columnAnnotation = field.getAnnotation("javax.persistence.Column");
    }

    if (columnAnnotation != null) {
      PsiAnnotationMemberValue nullableValue = columnAnnotation.findAttributeValue("nullable");
      if (nullableValue != null && "false".equals(nullableValue.getText())) {
        isNullable = false;
      }
    }

    return FieldModel.builder()
        .name(field.getName())
        .typeName(type.getPresentableText())
        .qualifiedTypeName(type.getCanonicalText())
        .isIdField(isId)
        .isNullable(isNullable)
        .sourcePsiField(field)
        .sourcePsiType(type)
        .build();
  }
}
