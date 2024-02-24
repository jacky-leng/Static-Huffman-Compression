import FileProcess.Decompression;
import FileProcess.StopWatch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * Class for command line interface of decompression
 * <p>
 * Usage: java xf InputFile OutputPath(optional)
 */
public final class xf {
    // Command line main application function.
    public static void main(String[] args) throws IOException {
        StopWatch stopWatch = new StopWatch();

        if (args.length == 0 || args.length > 2) {
            System.err.println("Usage: java xf <source> [destination](optional)");
            System.exit(1);
        }
        File inputFile = new File(args[0]);

        if (!inputFile.isFile()) {
            System.err.println("Usage: java xf <source> [dest](optional)");
            System.err.println("<source> must be a file");
            System.exit(1);
        }
        File outputRoot;
        // Handle command line arguments
        if (args.length == 1) {
            outputRoot = inputFile.getParentFile();
        } else {
            outputRoot = new File(args[1]);
        }

        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(inputFile))) {
            Decompression.decompress(outputRoot, is);
        }

        System.out.println("Decompression finished in " + stopWatch.getRunningSeconds() + " seconds");
    }


}
