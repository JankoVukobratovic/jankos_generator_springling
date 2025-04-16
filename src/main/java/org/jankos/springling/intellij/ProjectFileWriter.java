package org.jankos.springling.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.lang.java.JavaLanguage; // Import JavaLanguage
import com.intellij.util.IncorrectOperationException;

import java.io.IOException;

public class ProjectFileWriter {

  /**
   * Creates a new Java file in the specified package and opens it in the editor.
   *
   * @param project The current project
   * @param targetPackage The target package name (e.g., "com.example.myapp")
   * @param className The name of the class to create (without .java extension)
   * @param fileContent The content to write into the file
   * @throws IOException If an error occurs while creating or opening the file
   */
  public static void createAndOpenFile(
      Project project, String targetPackage, String className, String fileContent)
      throws IOException {
    final PsiDirectory targetDirectory = findOrCreateDirectory(project, targetPackage);
    if (targetDirectory == null) {
      throw new IOException(
          "Could not find or create target directory for package: " + targetPackage);
    }

    // Ensure we run this on the dispatch thread if it modifies PSI directly
    ApplicationManager.getApplication()
        .runWriteAction(
            () -> {
              // Create the PSI file in memory
              // Use JavaLanguage.INSTANCE here
              final PsiFile psiFile =
                  PsiFileFactory.getInstance(project)
                      .createFileFromText(className + ".java", JavaLanguage.INSTANCE, fileContent);

              // Reformat the code according to project style
              CodeStyleManager.getInstance(project).reformat(psiFile);

              // Add the file to the directory (physical creation)
              PsiFile existingFile = targetDirectory.findFile(psiFile.getName());
              if (existingFile != null) {
                // Handle existing file (overwrite? ask user?) - For now, overwrite
                existingFile.delete();
              }
              PsiFile addedFile = (PsiFile) targetDirectory.add(psiFile);

              // Optional: Open the newly created file in the editor
              VirtualFile virtualFile = addedFile.getVirtualFile();
              if (virtualFile != null) {
                FileEditorManager.getInstance(project).openFile(virtualFile, true);
              }
            });
  }

  /**
   * Find or create a directory for the given package name. This method will navigate through the
   * source roots of the project and create the necessary directories if they do not exist.
   *
   * @param project The current project
   * @param packageName The package name (e.g., "com.example.myapp")
   * @return The PsiDirectory representing the package directory
   */
  private static PsiDirectory findOrCreateDirectory(Project project, String packageName) {
      PsiManager psiManager = PsiManager.getInstance(project);

      // Get the source root for the current project/module
      VirtualFile sourceRoot = ProjectRootManager.getInstance(project).getContentSourceRoots()[0];
      if (sourceRoot == null) {
          throw new IllegalStateException("Source root not found for the project.");
      }

      PsiDirectory baseDirectory = psiManager.findDirectory(sourceRoot);
      if (baseDirectory == null) {
          throw new IllegalStateException("Base directory not found for the source root.");
      }

      String[] packageParts = packageName.split("\\.");
      PsiDirectory currentDirectory = baseDirectory;

      for (String part : packageParts) {
          PsiDirectory finalCurrentDirectory = currentDirectory; // Effectively final for lambda
          PsiDirectory subDirectory = finalCurrentDirectory.findSubdirectory(part);

          if (subDirectory == null) {
              // Create subdirectory if it doesn't exist
              subDirectory = WriteCommandAction.runWriteCommandAction(project, (ThrowableComputable<PsiDirectory, RuntimeException>) () -> {
                  try {
                      return finalCurrentDirectory.createSubdirectory(part);
                  } catch (IncorrectOperationException e) {
                      throw new IllegalStateException("Failed to create directory for package part: " + part, e);
                  }
              });
          }

          currentDirectory = subDirectory;
      }

      return currentDirectory;
  }
}
