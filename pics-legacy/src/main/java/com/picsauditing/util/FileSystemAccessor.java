package com.picsauditing.util;

import java.io.File;
import java.util.Set;

/**
 * Wrapper around the FileUtils class
 */
public class FileSystemAccessor {

    public static final String FILE_SYSTEM_PATH_SEPARATOR = System.getProperty("file.separator");

    public void moveFile(final File file, final String ftpDirectory, final String partialPath, final String fileName,
                         final String extension, final boolean deleteDuplicates) throws Exception {
        FileUtils.moveFile(file, ftpDirectory, partialPath, fileName, extension, deleteDuplicates);
    }

    public File getFile(final String filePath) {
        return new File(filePath);
    }

    public String thousandize(final int id) {
        return FileUtils.thousandize(id);
    }

    public boolean checkExtentions(final String extension, final Set<String> validExtentions) {
        return FileUtils.checkFileExtension(extension, validExtentions.toArray(new String[0]));
    }

    public boolean deleteFile(File file) {
        return FileUtils.deleteFile(file);
    }
}
