<#-- Package declaration -->
package ${package};

<#-- Imports -->
${imports}

<#-- Class declaration -->
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ${className} {

<#-- Fields -->
<#list fields as field>
    ${field}
</#list>
}
