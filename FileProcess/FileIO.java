package FileProcess;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;


/**
 * methods and constant Magic Numbers for FileI/O
 */
public class FileIO {
    /**
     * first byte magic number to check whether the file is compressed by us
     */
    public static final int HEADER_MAGIC_1 = 127;
    /**
     * second byte magic number to check whether the file is compressed by us
     */
    public static final int HEADER_MAGIC_2 = 155;
    /**
     * header before a single file
     */
    public static final int SINGLE_FILE_MAGIC = 188;
    /**
     * magic number to mark the end of file structure header
     */
    public static final int HEADER_END_SIGN = 183;
    /**
     * header before a directory
     */
    public static final int DIRECTORY_MAGIC = 177;
    /**
     * magic number to mark the end of a string
     */
    public static final int STRING_END_SIGN = 0;


    /**
     * input a path of a file or a folder
     * for file, change extension to .huff
     * for folder, add extension .huff
     */
    public static String changeFileExtension(String filePath) {
        String newExtension = "huff";
        int lastDotIndex = filePath.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return filePath.substring(0, lastDotIndex + 1) + newExtension;
        } else {
            return filePath + "." + newExtension;
        }
    }


    /**
     * get the directory of a file
     *
     * @param filePath the path of a file, not a directory
     */
    public static String getPath(String filePath) {
        int lastSeparatorIndex = filePath.lastIndexOf(File.separator);
        return filePath.substring(0, lastSeparatorIndex + 1);
    }

    /**
     * compute the relative of a path under another path
     *
     * @param root     root path
     * @param current, must be under root
     * @return a string start without file separator
     */
    public static String getRelativePathStr(File root, File current) {
        Path rootPath = root.toPath();
        Path currentPath = current.toPath();
        return rootPath.relativize(currentPath).toString();
    }

    /**
     * read a string ended with STRING_END_SIGN
     *
     * @param in InputStream
     */
    public static String readString(InputStream in) {
        try {
            StringBuilder sb = new StringBuilder();
            int data;
            while ((data = in.read()) != STRING_END_SIGN) {
                sb.append((char) data);
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("ERROR: fail to read file or directory name");
            return null;
        }
    }


    /**
     * Write a String to OutputStream, ending with {@code FileIO.STRING_END_SIGN}
     */
    public static void writeString(OutputStream out, String fileString) throws IOException {
        // write a string
        for (char c : fileString.toCharArray()) {
            out.write(c);
        }
        out.write(FileIO.STRING_END_SIGN);
    }


    public static void checkHeader(InputStream in) throws IOException {
        int magic1 = in.read();
        int magic2 = in.read();
        if (magic1 == HEADER_MAGIC_1 && magic2 == HEADER_MAGIC_2) {
            return;
        }
        System.err.println("ERROR: magic number NOT match");
        System.err.println("This file was NOT created by me");
        System.exit(1);
    }

    public static void skipHeader(InputStream in) throws IOException{
        while(in.read()!=HEADER_END_SIGN){
            /* empty */
        }
    }
}
