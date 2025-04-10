package org.jankos.springling.generator;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * This class is responsible for collecting and generating import statements for a list of fields.
 * It resolves field types to their fully qualified names and generates the necessary import block.
 */
@RequiredArgsConstructor
public class ImportGenerator {
  @NonNull private final Project project;
  @NonNull private final PsiElement context;
  private static final Set<String> PRIMITIVE_TYPES =
      Set.of("int", "long", "short", "byte", "float", "double", "boolean", "char");

  private static final Map<String, String> JAVA_LANG_TYPES =
      Map.ofEntries(
          Map.entry("String", "java.lang.String"),
          Map.entry("Object", "java.lang.Object"),
          Map.entry("Integer", "java.lang.Integer"),
          Map.entry("Long", "java.lang.Long"),
          Map.entry("Short", "java.lang.Short"),
          Map.entry("Byte", "java.lang.Byte"),
          Map.entry("Float", "java.lang.Float"),
          Map.entry("Double", "java.lang.Double"),
          Map.entry("Boolean", "java.lang.Boolean"),
          Map.entry("Character", "java.lang.Character"),
          Map.entry("Void", "java.lang.Void"));

  /**
   * Generates an import block for the given field types. The block is fully formated, ready to be
   * inserted into a Java file.
   *
   * @param fieldTypes A list of simple or fully qualified field types.
   * @return A string containing the formatted import block.
   */
  public String generate(List<String> fieldTypes) {
    Set<String> imports = collectImports(fieldTypes);
    return generateImportBlock(imports);
  }

  /**
   * Collect all necessary imports for the given fields.
   *
   * @param fieldTypes A list of simple or fully qualified field types.
   * @return A set of fully qualified imports needed for the fields.
   */
  private Set<String> collectImports(List<String> fieldTypes) {
    Set<String> imports = new HashSet<>();

    for (String fieldType : fieldTypes) {
      String fullyQualifiedType = resolveType(fieldType);

      if (fullyQualifiedType == null) {
        imports.add("// TODO: Resolve import for " + fieldType);
      } else if (!isPrimitive(fullyQualifiedType)) {
        imports.add(fullyQualifiedType);
      }
    }

    return imports;
  }

  /**
   * Resolves a field type to its fully qualified name.
   *
   * @param type A simple or fully qualified type name.
   * @return The fully qualified type name, or null if unresolved.
   */
  private String resolveType(String type) {
    if (JAVA_LANG_TYPES.containsKey(type)) {
      return JAVA_LANG_TYPES.get(type);
    }
    if (JAVA_LANG_TYPES.containsValue(type)) {
      return type;
    }

    PsiClass psiClass =
        JavaPsiFacade.getInstance(project).findClass(type, GlobalSearchScope.allScope(project));

    // If not found, try resolving in broader scopes
    if (psiClass == null) {
      psiClass =
          JavaPsiFacade.getInstance(project)
              .findClass(type, GlobalSearchScope.everythingScope(project));
    }

    // If still not found, check context-specific imports
    if (psiClass == null) {
      psiClass = resolveFromContext(type, context);
    }

    // Return fully qualified name if found, otherwise return type as-is
    return psiClass != null ? psiClass.getQualifiedName() : type;
  }

  /** Helper method to determine if a type is primitive. */
  private boolean isPrimitive(String type) {
    return PRIMITIVE_TYPES.contains(type);
  }

  /**
   * Resolves a type from the context of the source file. The smart way. Why try dynamically resolve
   * a type when it's served on a silver plate?
   *
   * @param type The type to resolve.
   * @param context The context in which the fully qualified name resides.
   * @return The fully qualified class name, or null if not found.
   */
  private PsiClass resolveFromContext(String type, PsiElement context) {
    PsiFile psiFile = context.getContainingFile();
    if (psiFile instanceof PsiJavaFile javaFile) {
        // Check imports in the current file
      for (PsiImportStatement importStatement :
          Objects.requireNonNull(javaFile.getImportList()).getImportStatements()) {
        if (importStatement.getQualifiedName() != null
            && importStatement.getQualifiedName().endsWith("." + type)) {
          return JavaPsiFacade.getInstance(project)
              .findClass(importStatement.getQualifiedName(), GlobalSearchScope.allScope(project));
        }
      }
      // Handle wildcard imports
      for (PsiImportStatement importStatement : javaFile.getImportList().getImportStatements()) {
        if (importStatement.isOnDemand()) {
          String packageName = importStatement.getQualifiedName();
          PsiClass potentialClass =
              JavaPsiFacade.getInstance(project)
                  .findClass(packageName + "." + type, GlobalSearchScope.allScope(project));
          if (potentialClass != null) {
            return potentialClass;
          }
        }
      }
    }
    return null;
  }

  /**
   * Generates a formatted import block from the collected imports.
   *
   * @param imports A set of fully qualified import statements.
   * @return A string containing the formatted import block.
   */
  private String generateImportBlock(Set<String> imports) {
    StringBuilder importBlock = new StringBuilder();
    for (String importStatement : imports) {
      if (importStatement.startsWith("// TODO")) {
        importBlock.append(importStatement).append("\n");
      } else {
        importBlock.append("import ").append(importStatement).append(";\n");
      }
    }
    return importBlock.toString();
  }
}
