import FileProcess.Compression;
import FileProcess.FileIO;
import FileProcess.OverwriteProtector;
import FileProcess.StopWatch;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Usage: Usage: java cf InputFile OutputFile(optional).
 * <p>
 * Class for command line interface.
 * <p>
 * OutputFile Structure:
 * <p>
 * 1. header: MagicNumber(2 bytes)--fileStructureHeader(Strings of file path ending with STRING_END_SIGN)--HEADER_END_SIGN
 * <p>
 * 2. continuous FileBlocks or DirectoryBlocks, one for a path in source file.
 * <p>
 * FileBocks: SINGLE_FILE_MAGIC--StringOfPath--STRING_END_SIGN--CanonicalCodeTable--encodedBits(ending with special EOF)
 * <p>
 * DirectoryBlocks: DIRECTORY_MAGIC--StringOfPath--STRING_END_SIGN
 * <p>
 * PS:
 * String is stored in file in C style instead of Java style. String = chars + STRING_END_SIGN
 */
public final class cf {
    // Command line main application function.
    public static void main(String[] args) throws IOException {
        StopWatch stopWatch = new StopWatch();
        long original_size;

        // Handle command line arguments
        // set output file
        File outputFile;
        if (args.length == 1) {
            outputFile = new File(FileIO.changeFileExtension(args[0]));
        } else if (args.length == 2) {
            outputFile = new File(args[1]);
        } else {
            System.err.println("Usage: java cf <InputFile> [OutputFile](optional)");
            System.exit(1);
            return;
        }
        // set input file
        File inputFile = new File(args[0]);

        if (inputFile.equals(outputFile)) {
            System.err.println("InputPath equals outputPath. Program terminated.");
            System.exit(1);
        }
        // Overwrite Protection
        OverwriteProtector protector = new OverwriteProtector();
        if (outputFile.exists()) {
            protector.askUser(outputFile);
        }


        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile))) {
            original_size = Compression.compress(inputFile.getParentFile(), inputFile, os);
        }


        // Print Summary
        long compressed_size = outputFile.length();
        double compression_percentage = Math.round((((double) compressed_size / original_size) * 10000)) / 100.0;
        System.out.println("Compression finished");
        if (original_size == 0) {
            System.out.println("Compression ratio: No non-empty file");
        } else {
            System.out.println("Compression ratio: " + compression_percentage + "%");
        }
        System.out.println("Time Usage:      " + stopWatch.getRunningSeconds() + " seconds");
        System.out.println("Original Size:   " + original_size / 1000.0 + "KB");
        System.out.println("Compressed Size: " + compressed_size / 1000.0 + "KB");
    }

}
