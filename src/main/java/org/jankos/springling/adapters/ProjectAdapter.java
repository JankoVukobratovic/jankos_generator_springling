package org.jankos.springling.adapters;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Utility class for handling project-related operations in IntelliJ IDEA. Is it that necessary to
 * have this class? No. Do i want to? Yes. Why? Because I can. (And im lazy to remember all the
 * methods, so keeping just the useful ones is prettier) Also if i ever need to change the
 * implementation of this class, i can do it in one place.
 */
public class ProjectAdapter {


  /**
   * Finds a file in a given directory by its relative path. The path is split by '/' and each
   * part is used to navigate through the directory structure.
   *
   * @param directory The directory to search in
   * @param relativePath The relative path to the file, e.g., "src/main/java/com/example/MyClass.java"
   * @return The VirtualFile if found, or null if it cannot be found
   */
  public static VirtualFile findFileInDirectory(
      @NotNull VirtualFile directory, @NotNull String relativePath) {

    String[] pathParts = relativePath.split("/");
    VirtualFile current = directory;

    for (String part : pathParts) {
      if (current == null || !current.isDirectory()) {
        return null;
      }
      current = current.findChild(part);
    }

    return current != null && current.exists() ? current : null;
  }

  /**
   * Deletes a file or directory. If the file is a directory, it will be deleted recursively.
   *
   * @param file The file or directory to delete
   * @return true if the deletion was successful, false otherwise
   */
  public static boolean deleteFileOrDirectory(@Nullable VirtualFile file) {
    if (file == null || !file.exists()) {
      return false;
    }
    try {
      file.delete(null);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Creates a new directory in the specified parent directory with the given name.
   *
   * @param parentDir The parent directory where the new directory will be created
   * @param name The name of the directory to be created
   * @return The created directory as a VirtualFile, or null if the directory could not be created
   */
  @Nullable
  public static VirtualFile createDirectory(@NotNull VirtualFile parentDir, @NotNull String name) {
    try {
      return parentDir.createChildDirectory(null, name);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Creates a new file in the specified parent directory with the given name.
   *
   * @param parentDir The parent directory where the file will be created
   * @param name The name of the file to be created
   * @return The created file as a VirtualFile, or null if the file could not be created
   */
  @Nullable
  public static VirtualFile createFile(@NotNull VirtualFile parentDir, @NotNull String name) {
    try {
      return parentDir.createChildData(null, name);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Retrieves all child files.
   *
   * @param directory The directory to list child files from
   * @return An array of child files, or null if the directory is not a directory
   */
  @Nullable
  public static VirtualFile[] getChildren(@NotNull VirtualFile directory) {
    return directory.isDirectory() ? directory.getChildren() : null;
  }

  /**
   * Checks if a given file exists.
   *
   * @param file The file to check
   * @return true if the file exists, false otherwise
   */
  public static boolean fileExists(@Nullable VirtualFile file) {
    return file != null && file.exists();
  }

  /**
   * Returns a subdirectory of the given parent directory with the specified name. If the
   * subdirectory does not exist or is not a directory, null is returned.
   *
   * @param parentDir The parent directory to search in
   * @param subDirName The name of the subdirectory to find
   * @return The subdirectory as a VirtualFile, or null if it does not exist or is not a directory
   */
  @Nullable
  public static VirtualFile findSubdirectory(
      @NotNull VirtualFile parentDir, @NotNull String subDirName) {
    VirtualFile subDir = parentDir.findChild(subDirName);
    if (subDir == null || !subDir.isDirectory()) {
      return null;
    }
    return subDir;
  }

  /**
   * Finds a file by its relative path within the project's base directory.
   *
   * @param project The IntelliJ Project instance
   * @param relativePath The relative path to a file
   * @return The VirtualFile if found, or null if it cannot be found
   */
  @Nullable
  public static VirtualFile findFileByRelativePath(
      @NotNull Project project, @NotNull String relativePath) {
    VirtualFile baseDir = getBaseDir(project);
    return baseDir != null ? baseDir.findFileByRelativePath(relativePath) : null;
  }

  /**
   * Retrieves the base directory of the project as a VirtualFile.
   *
   * @param project The IntelliJ Project instance
   * @return The base directory as a VirtualFile, or null if it cannot be found
   */
  @Nullable
  public static VirtualFile getBaseDir(@NotNull Project project) {
    String basePath = project.getBasePath();
    if (basePath == null) {
      return null;
    }
      return LocalFileSystem.getInstance().findFileByPath(basePath);
  }

  /**
   * Retrieves the VirtualFile representing the 'src/main/java' directory
   *
   * @param project The IntelliJ Project instance
   * @return The VirtualFile for 'src/main/java', or null if it cannot be found
   */
  @Nullable
  public static VirtualFile getSrcMainJavaDirectory(@NotNull Project project) {
    VirtualFile baseDir = getBaseDir(project);

    VirtualFile srcDir = Objects.requireNonNull(baseDir).findChild("src");
    if (srcDir == null) {
      return null;
    }
    VirtualFile mainDir = srcDir.findChild("main");
    if (mainDir == null) {
      return null;
    }
    return mainDir.findChild("java");
  }
}
