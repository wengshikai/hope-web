package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by weng on 15-10-25.
 */

public class FileTool {
    public static byte[] getFileContent(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static void putFileContent(String path, byte[] content)
            throws IOException {
        Files.write(Paths.get(path), content);
    }

    public static void putFileContentP(String path, byte[] content)
            throws IOException {
        Path parent = Paths.get(path).getParent();
        if(!Files.exists(parent)){
            Files.createDirectories(parent);
        }
        putFileContent(path, content);
    }

    public static boolean createDirP(String path) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public static boolean createDir(String path){
        try {
            Files.createDirectory(Paths.get(path));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean delete(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(fileName));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static String getFileDir(String fileName) {
        Path parent = Paths.get(fileName).getParent();
        return parent.toString();
    }

    public static boolean isDirExist(String fileName){
        return Files.isDirectory(Paths.get(fileName));
    }



    public static boolean deleteDirectory(String dir) {
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = delete(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            return false;
        }

        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }



    public static boolean isDirectory(String path){
        return Files.isDirectory(Paths.get(path));
    }

    public static boolean createDirRecursive(String path) {
        try {
            Files.createDirectories(Paths.get(path));
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    public static boolean createDirRecursiveIfNotExists(String path) {
        try {
            if(!isDirectory(path)){
                Files.createDirectories(Paths.get(path));
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param dest 文件名或者目录名
     */
    public static void createDestDirectoryIfNotExists(String dest) {
        if (dest.endsWith(File.separator)) {
            createDirRecursiveIfNotExists(dest);
        } else {
            createDirRecursiveIfNotExists(Paths.get(dest).getParent().toString());
        }
    }

    public static List<String> getFileListInDirectory(String directory){
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                fileNames.add(path.toString());
            }
        } catch (IOException ex) {}
        return fileNames;
    }

    public static List<String> getFileListInDirectoryWithoutDot(String directory){
        List<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                if(!path.getFileName().toString().startsWith(".")){
                    fileNames.add(path.toString());
                }
            }
        } catch (IOException ex) {}
        return fileNames;
    }

    public static void  main(String[] arvs){
        System.out.println(getFileDir("/tmp/weng1/weng2/weng3/weng4/weng5"));
    }
}