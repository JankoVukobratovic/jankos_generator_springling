package org.jankos.springling.generator;

import com.intellij.psi.PsiElement;
import freemarker.template.Configuration;
import com.intellij.openapi.project.Project;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.NonNull;
import org.jankos.springling.model.Field;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/// This class is responsible for generating Data Transfer Objects (DTOs) based on the given fields
/// It generates the file and puts it in the correct package. This is the end of the generation
/// To avoid confusion, the DTO generation is separated from the Mapper generation
/// For better code readability, settings for the DTO generation are in "builder" form.
/// process.
public class DTOGenerator {

  @NonNull private final Project project;
  @NonNull private final PsiElement context;
  private final Configuration freemarkerConfig;

  private String packageName;
  private String className;
  private List<Field> fields;

  /**
   * Constructor for DTOGenerator.
   *
   * @param project The current project.
   * @param context The context (file) in which the generator is used.
   * @throws IOException If an error occurs while initializing the FreeMarker configuration.
   */
  public DTOGenerator(@NonNull Project project, @NonNull PsiElement context) throws IOException {
    this.project = project;
    this.context = context;
    this.freemarkerConfig = new Configuration(Configuration.VERSION_2_3_31);
    freemarkerConfig.setDirectoryForTemplateLoading(
        new File("D:\\Sorted\\Programming\\Projects\\springling\\src\\main\\resources\\templates"));
    freemarkerConfig.setDefaultEncoding("UTF-8");
  }

  /**
   * Generates the DTO file based on the provided settings. This method is responsible for creating
   * the DTO file and placing it in the specified package.
   */
  public void generateDTO() throws IOException {
    ImportGenerator importCollector = new ImportGenerator(project, context);

    String importBlock = importCollector.generate(fields.stream().map(Field::getType).toList());

    Map<String, Object> dataModel = new HashMap<>();

    dataModel.put("package", packageName);
    dataModel.put("className", className);

    List<String> fieldBlock = generateFieldDeclarations(fields);
    dataModel.put("fields", fieldBlock);

    System.out.println(importBlock);
    dataModel.put("imports", importBlock);

    Template template = freemarkerConfig.getTemplate("DTO_TEMPLATE.ftl");

    File outputFile = new File(getDestinationPath(), className + ".java");

    outputFile.getParentFile().mkdirs();

    try (FileWriter writer = new FileWriter(outputFile)) {
      template.process(dataModel, writer);
    } catch (TemplateException | IOException e) {
      throw new RuntimeException(e);
    }

    System.out.println("DTO generated: " + outputFile.getAbsolutePath());
  }

  /**
   * Generates field declarations for the DTO class based on the provided fields. If you're
   * wondering why this is not a single string like imports, but rather a list of strings, it's in
   * case something heeds to be annotated, like @JsonProperty or @JsonIgnore
   *
   * @param fields The list of fields to generate declarations for.
   * @return A list of field declaration strings.
   */
  private List<String> generateFieldDeclarations(List<Field> fields) {
    List<String> fieldDeclarations = new ArrayList<>();
    for (Field field : fields) {
      fieldDeclarations.add(String.format("private %s %s;%n", field.getType(), field.getName()));
    }

    return fieldDeclarations;
  }

  /**
   * Generates the destination path for the DTO file based on the package name. Example:
   * "com.example.dto" will be converted to "src/main/java/com/example/dto".
   *
   * @return The destination path for the DTO file.
   */
  private String getDestinationPath() {
    String srcDirPath = project.getBasePath() + "/src/main/java";
    String packagePath = packageName.replace(".", "/");
    return srcDirPath + "/" + packagePath;
  }

  // region Setters
  /**
   * Sets the package name for the DTO.
   *
   * @param packageName The package name to set.
   * @return The current instance of DtoGenerator for method chaining.
   */
  public DTOGenerator setPackageName(String packageName) {
    this.packageName = packageName;
    return this;
  }

  /**
   * Sets the class name for the DTO.
   *
   * @param className The class name to set.
   * @return The current instance of DtoGenerator for method chaining.
   */
  public DTOGenerator setClassName(String className) {
    this.className = className;
    return this;
  }

  /**
   * Sets the fields for the DTO.
   *
   * @param fields The list of fields to set.
   * @return The current instance of DtoGenerator for method chaining.
   */
  public DTOGenerator setFields(List<Field> fields) {
    this.fields = fields;
    return this;
  }
  // endregion
}
