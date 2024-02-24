package FileProcess;

import BitwiseStream.BitInputStream;
import Huffman.HuffmanTree;
import Huffman.InternalNode;
import Huffman.TreeCanonization;

import java.io.*;

public class Decompression {
    /**
     * Main api for decompression
     *
     * @param outputRoot a directory, where to store archived file(s)
     * @param in         byte-wise input stream
     */
    public static void decompress(File outputRoot, BufferedInputStream in) throws IOException {
        FileIO.checkHeader(in);
        FileIO.skipHeader(in);

        int mode;
        OverwriteProtector protector = new OverwriteProtector();
        while ((mode = in.read()) != -1) {
            if (mode != FileIO.SINGLE_FILE_MAGIC && mode != FileIO.DIRECTORY_MAGIC) {
                System.err.println("ERROR: inner magic number NOT match");
                System.exit(1);
                return;
            }
            String fileName = FileIO.readString(in);
            assert fileName != null;
            if (mode == FileIO.DIRECTORY_MAGIC) {
                File directory = new File(outputRoot, fileName);
                if (directory.exists()) {
                    protector.askUser(directory);
                } else {
                    if (!directory.mkdir()) {
                        throw new IOException();
                    }
                }
            } else {
                File singleFile = new File(outputRoot, fileName);
                if (singleFile.exists()) {
                    protector.askUser(singleFile);
                }
                try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(singleFile))) {
                    TreeCanonization canonCode = new TreeCanonization(readLengthsTable(in));
                    try (BitInputStream bitIn = new BitInputStream(in)) {
                        HuffmanTree HuffmanTree = canonCode.getHuffmanTree();
                        writeDecodedBytes(HuffmanTree.getRoot(), bitIn, os);
                        os.close();
                    } catch (Exception e) {
                        System.err.println("ERROR: fail in bitInputStream");
//                        e.printStackTrace();
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Write decoded path to out
     *
     * @param treeRoot use for construct a decoder
     * @param in       bitWise inputStream
     * @param out      byteWise outputStream
     */
    private static void writeDecodedBytes(InternalNode treeRoot, BitInputStream in, OutputStream out) throws IOException {
        Decoder decoder = new Decoder(in, treeRoot);
        while (true) {
            int symbol = decoder.read();
            if (symbol == 256)  // EOF symbol
                break;
            out.write(symbol);
        }
    }

    /**
     * ByteWise
     * <p>
     * Read LengthTable before encoded bits.
     *
     * @param in byteWise input stream
     * @return LengthsTable
     */
    private static int[] readLengthsTable(InputStream in) throws IOException {
        int[] codeLengths = new int[FrequencyTable.BYTE_PATTERNS_NUM];
        for (int i = 0; i < codeLengths.length; i++) {
            codeLengths[i] = in.read();
        }
        return codeLengths;
    }
}
