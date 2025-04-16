package org.jankos.springling.models;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class FieldModel {
    String name;
    String typeName; // e.g., "String", "Long"
    String qualifiedTypeName; // e.g., "java.lang.String", "java.lang.Long"
    boolean isIdField;
    boolean isNullable; // Determined from annotations like @NotNull, @Column(nullable=...)

    // Optional reference back
    PsiField sourcePsiField;
    PsiType sourcePsiType;


}