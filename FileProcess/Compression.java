package FileProcess;

import BitwiseStream.BitOutputStream;
import Huffman.HuffmanTree;
import Huffman.TreeCanonization;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class Compression {

    /**
     * Main Api for compression
     *
     * @return total size of original files in BYTES
     */
    public static long compress(File root, File src, BufferedOutputStream dest) throws IOException {
        writeHeader(dest, src, root);

        // use a stack to handle multiple files in a directory
        // in a DFS order
        Stack<File> folderStack = new Stack<>();
        long original_size = 0;
        folderStack.push(src);
        while (!folderStack.isEmpty()) {
            File f = folderStack.pop();
            if (f.isFile()) {
                original_size += f.length();
                compressSingleFile(root, f, dest);
            } else if (f.isDirectory()) {
                writeDirectoryName(root, f, dest);
                for (File subfile : Objects.requireNonNull(f.listFiles())) {
                    folderStack.push(subfile);
                }
            }
        }

        return original_size;
    }

    /**
     * Compress a single file
     *
     * @param root the root path of source file(s)
     * @param src  current compressing file
     * @param dest the archived file
     */
    private static void compressSingleFile(File root, File src, BufferedOutputStream dest) throws IOException {
        // Read input file the first time to compute symbol frequencies.
        FrequencyTable freqTable = getFrequencies(src);
        freqTable.increment(256);  // EOF symbol gets a frequency of 1
        HuffmanTree huffmanTree = new HuffmanTree(freqTable);
        TreeCanonization canonCode = new TreeCanonization(huffmanTree);

        List<List<Integer>> codesList = canonCode.getHuffmanTree().getCodesList();

        // Read input file again, cf with Huffman coding, and write output file
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src))) {
            // Magic Number
            dest.write(FileIO.SINGLE_FILE_MAGIC);

            // write file name not encoded
            String pathStr = FileIO.getRelativePathStr(root, src);
            FileIO.writeString(dest, pathStr);

            // instantiate bitOutput to write lengthTable and compressed data
            writeLengthsTable(dest, canonCode.getLengthsTable());
            BitOutputStream out = new BitOutputStream(dest);
            writeEncodedBits(codesList, in, out);
        }
    }


    /**
     * Write the directoryName directly without encoding
     *
     * @param root the root path of source file(s)
     * @param src  current processing directory
     * @param dest the archived file
     */
    private static void writeDirectoryName(File root, File src, BufferedOutputStream dest) throws IOException {
        dest.write(FileIO.DIRECTORY_MAGIC);
        // write file name not encoded
        String pathStr = FileIO.getRelativePathStr(root, src);
        FileIO.writeString(dest, pathStr);
    }


    /**
     * Read through a single file and count its byte frequencies
     * <p>
     * Also contains an extra entry for symbol 256, whose frequency is set to 0.
     *
     * @return a frequency table based on the bytes in the given file.
     */
    private static FrequencyTable getFrequencies(File file) throws IOException {
        if (!file.isFile()) {
            throw new IllegalArgumentException("getFrequencies must receive a singleFile argument");
        }
        FrequencyTable freqTable = new FrequencyTable();
        try (InputStream input = new BufferedInputStream(new FileInputStream(file))) {
            while (true) {
                int b = input.read();
                if (b == -1) break;
                freqTable.increment(b);
            }
        }
        return freqTable;
    }


    /**
     * Write the lengthsTable into archived file
     * @param dest the archived file
     * @param lengthsTable lengthsTable
     */
    private static void writeLengthsTable(OutputStream dest, int[] lengthsTable) throws IOException {
        for (int i = 0; i < FrequencyTable.BYTE_PATTERNS_NUM; i++) {
            dest.write(lengthsTable[i]);
        }
    }


    /**
     * Write encoded data into the archived file
     */
    private static void writeEncodedBits(List<List<Integer>> codesList, InputStream in, BitOutputStream out) throws IOException {
        Encoder enc = new Encoder(out, codesList);
        while (true) {
            int b = in.read();
            if (b == -1) break;
            enc.write(b);
        }
        enc.write(256);  // EOF
        out.close();
    }

    /**
     * Traverse all file in source path.
     * <p>
     * Write relative path as Strings into header.
     * <p>
     * This method writes: Magic Numbers (2 bytes),
     * Strings end with {@code FileIO.STRING_END_SIGN},
     * {@code HEADER_END_SIGN}
     */
    private static void writeHeader(OutputStream out, File src, File root) throws IOException {
        out.write(FileIO.HEADER_MAGIC_1);
        out.write(FileIO.HEADER_MAGIC_2);
        Stack<File> folderStack = new Stack<>();
        folderStack.push(src);
        while (!folderStack.isEmpty()) {
            File f = folderStack.pop();
            FileIO.writeString(out, FileIO.getRelativePathStr(root, f));
            if (f.isDirectory()) {
                folderStack.addAll(List.of(Objects.requireNonNull(f.listFiles())));
            }
        }
        out.write(FileIO.HEADER_END_SIGN);
    }

}
