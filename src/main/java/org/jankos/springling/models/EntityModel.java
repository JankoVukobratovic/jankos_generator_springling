package org.jankos.springling.models;

import com.intellij.psi.PsiClass;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class EntityModel {
    String className;
    String packageName;
    String qualifiedName;
    List<FieldModel> fields;
    FieldModel idField; // Convenience access to the ID field
    String tableName; // Extracted from @Table if present

    // Optional reference back to the source PSI element
    PsiClass sourcePsiClass;

    public String getFqnTypeName() {
        return packageName + "." + className;
    }
}
