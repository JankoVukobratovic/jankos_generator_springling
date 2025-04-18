package org.jankos.springling.adapters;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class FileAdapter {
    /**
     * Reads the content of a file as a String.
     *
     * @param file The VirtualFile to read from.
     * @return The content of the file as a String, or null if the file is invalid.
     */
    @Nullable
    public static String readFileContent(@NotNull VirtualFile file) {
        if (!file.exists() || file.isDirectory()) {
            return null;
        }
        try {
            return new String(file.contentsToByteArray(), file.getCharset());
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * Writes content to a file, overwriting any existing content.
     *
     * @param file    The VirtualFile to write to.
     * @param content The content to write into the file.
     * @return True if the operation was successful, false otherwise.
     */
    public static boolean writeFileContent(@NotNull VirtualFile file, @NotNull String content) {
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        try {
            file.setBinaryContent(content.getBytes(file.getCharset()));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Appends content to the end of a file.
     *
     * @param file    The VirtualFile to append to.
     * @param content The content to append.
     * @return True if the operation was successful, false otherwise.
     */
    public static boolean appendToFile(@NotNull VirtualFile file, @NotNull String content) {
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        try {
            String existingContent = readFileContent(file);
            if (existingContent == null) {
                return false;
            }
            file.setBinaryContent((existingContent + content).getBytes(file.getCharset()));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Clears the content of a file.
     *
     * @param file The VirtualFile to clear.
     * @return True if the operation was successful, false otherwise.
     */
    public static boolean clearFileContent(@NotNull VirtualFile file) {
        return writeFileContent(file, "");
    }

}
