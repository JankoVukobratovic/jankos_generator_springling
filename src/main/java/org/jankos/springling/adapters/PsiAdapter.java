package org.jankos.springling.adapters;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
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
   * @param psiClass The PsiClass to parse
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
  @NotNull
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


  /**
   * Check if the field is a reference to another entity.
   * This method checks if the field is of a class type and whether
   * it has annotations indicating relationships like @ManyToOne, @OneToOne, etc.
   * @param field The field to check
   * @return true if the field is an entity reference, false otherwise
   */
  public static boolean isEntityReference(@NotNull FieldModel field) {
    PsiField psiField = field.getSourcePsiField();
    PsiType fieldType = psiField.getType();

    // Check if the field is a class (non-primitive, non-collection, etc.)
    if (fieldType instanceof com.intellij.psi.PsiClassType) {
      // Check for relationship annotations
        return psiField.hasAnnotation("jakarta.persistence.ManyToOne") ||
                psiField.hasAnnotation("javax.persistence.ManyToOne") ||
                psiField.hasAnnotation("jakarta.persistence.OneToOne") ||
                psiField.hasAnnotation("javax.persistence.OneToOne") ||
                psiField.hasAnnotation("jakarta.persistence.OneToMany") ||
                psiField.hasAnnotation("javax.persistence.OneToMany") ||
                psiField.hasAnnotation("jakarta.persistence.ManyToMany") ||
                psiField.hasAnnotation("javax.persistence.ManyToMany");
    }

    return false;
  }

  /**
   * Extract the type of the ID field from the entity.
   * @param psiClass The PsiClass representing the entity
   * @return The type of the ID field, or null if no ID field is found
   */
  @Nullable
  public static PsiType getIdFieldType(@NotNull PsiClass psiClass) {
    for (PsiField field : psiClass.getAllFields()) {
      if (field.hasAnnotation("jakarta.persistence.Id") || field.hasAnnotation("javax.persistence.Id")) {
        // Return the type of the ID field
        return field.getType();
      }
    }
    return null; // No ID field found
  }

  /**
   * Resolve a PsiClass from the fully qualified type name.
   * @param project The current project
   * @param fullyQualifiedName The fully qualified name of the class
   * @return The PsiClass if found, or null if not found
   */
  @Nullable
  public static  PsiClass resolvePsiClassFromFQN(@NotNull Project project,@NotNull String fullyQualifiedName) {
    JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
    PsiElementFactory psiElementFactory = psiFacade.getElementFactory();

    // Attempt to resolve the class by fully qualified name
    PsiClass psiClass;
    try {
      psiClass = psiFacade.findClass(fullyQualifiedName, GlobalSearchScope.allScope(project));
    } catch (Exception e) {
      return null;
    }

    return psiClass;
  }
}
