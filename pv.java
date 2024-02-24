import FileProcess.FileIO;
import FileProcess.PathTreePrint;
import FileProcess.StopWatch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Preview file Structure in compressed file
 * <p>
 * Print out in tree style
 */
public class pv {
    public static void main(String[] args) throws IOException {
        StopWatch stopWatch = new StopWatch();

        if (args.length != 1) {
            System.err.println("Usage: java pv <source>");
            System.exit(1);
        }

        File inputFile = new File(args[0]);
        if(!inputFile.isFile()){
            System.err.println("Usage: java pv <source>");
            System.err.println("<source> must be a file");
            System.exit(1);
        }
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(inputFile))) {
            List<String> paths = previewHelper(is);
            PathTreePrint.printTree(paths);
        }

        System.out.println("Preview finished in " + stopWatch.getRunningSeconds() + " seconds");
    }

    public static List<String> previewHelper(BufferedInputStream in) throws IOException {
        List<String> paths = new ArrayList<>();
        FileIO.checkHeader(in);
        // read a char first, check FileIO.HEADER_END_SIGN
        int firstByte;
        while ((firstByte = in.read()) != FileIO.HEADER_END_SIGN) {
            String restBytes = FileIO.readString(in);
            char firstChar = (char) firstByte;
            paths.add(firstChar + restBytes);
        }
        return paths;
    }

}
