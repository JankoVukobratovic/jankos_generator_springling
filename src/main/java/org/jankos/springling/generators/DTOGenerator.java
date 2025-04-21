package org.jankos.springling.generators;

import com.intellij.openapi.project.Project;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jankos.springling.models.DTOModel;
import org.jankos.springling.models.FieldModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DTOGenerator {
    private final Project project;

    public DTOGenerator(Project project) {
        this.project = project;
    }

    /**
     * Generates a DTO file from a given DTO model using a Freemarker template.
     *
     * @param dtoModel The DTO model to generate the file from.
     * @throws IOException           if an I/O error occurs while writing the file.
     * @throws TemplateException     if a Freemarker error occurs during template processing.
     */
    public void generateDtoFile(DTOModel dtoModel) throws IOException, TemplateException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setClassForTemplateLoading(DTOGenerator.class, "/templates");

        Template template = cfg.getTemplate("dto.ftl");

        Map<String, Object> dataModel = prepareDataModel(dtoModel);

        String outputDir = getOutputDirectory(dtoModel.getPackageName());
        File outputFile = new File(outputDir, dtoModel.getClassName() + "DTO.java");

        // Ensure the output directory exists
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        // Write the generated DTO to a file
        try (FileWriter writer = new FileWriter(outputFile)) {
            template.process(dataModel, writer);
        }
    }

    /**
     * Prepares the data model for the Freemarker template.
     *
     * @param dtoModel The DTO model to prepare the data for.
     * @return A map containing the data for the template.
     */
    private Map<String, Object> prepareDataModel(DTOModel dtoModel) {
        List<String> imports = new ArrayList<>();
        for (FieldModel field : dtoModel.getFields()) {
            String fullyQualifiedTypeName = field.getFqnTypeName();

            if (isPrimitiveType(fullyQualifiedTypeName)) {
                continue;
            }

            if (!imports.contains(fullyQualifiedTypeName)) {
                imports.add(fullyQualifiedTypeName);
            }
        }

        return Map.of(
                "package", dtoModel.getPackageName(),
                "className", dtoModel.getClassName(),
                "imports", imports,
                "fields", dtoModel.getFields()
        );
    }

    private boolean isPrimitiveType(String type) {
        return type.equals("boolean") || type.equals("byte") || type.equals("char") || type.equals("short")
                || type.equals("int") || type.equals("long") || type.equals("float") || type.equals("double")
                || type.equals("void");
    }

    /**
     * Gets the output directory path for the DTO based on the package name.
     *
     * @param packageName The package name for the DTO.
     * @return The output directory path.
     */
    private String getOutputDirectory(String packageName) {
        return project.getBasePath() + "/src/main/java/" + packageName.replace('.', '/');
    }
}
