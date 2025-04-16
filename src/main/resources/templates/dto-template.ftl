<#-- Package declaration -->
package ${package};

<#-- Imports -->
<#list imports as import>
    import ${import};
</#list>

<#-- Class declaration -->
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ${className}DTO {

<#-- Fields -->
<#list fields as field>
    private ${field.typeName} ${field.name};
</#list>
}