package nju.pa.experiment.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-25
 */
public class IOUtil {

    private IOUtil() {}

    public static String suffixOf(String filePath) {
        return suffixOf(new File(filePath));
    }

    public static String suffixOf(File file) {
        if(file.isDirectory()) {
            return "";
        }
        String fileName = file.getName();
        int loc = fileName.lastIndexOf('.');
        return fileName.substring(loc);
    }

    public static String simpleName(String filePath) {
        return simpleName(new File(filePath));
    }

    public static String simpleName(File file) {
        if(file.isDirectory()) {
            return file.getName();
        }
        return file.getName().replace(suffixOf(file), "");
    }

    public static List<File> listFilesOrEmpty(String dirPath) throws NotDirectoryException {
        return listFilesOrEmpty(new File(dirPath));
    }

    public static List<File> listFilesOrEmpty(File dir) throws NotDirectoryException {
        if(!dir.isDirectory())
            throw new NotDirectoryException(dir.getAbsolutePath());

        File[] files = dir.listFiles();
        if(files == null)
            return new ArrayList<>();
        return Arrays.asList(files);
    }

}
