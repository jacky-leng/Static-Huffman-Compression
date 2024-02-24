package FileProcess;

import BitwiseStream.BitOutputStream;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


/**
 * Encodes symbols and writes to a Huffman-coded bit stream.
 */
public final class Encoder {
    private final BitOutputStream bitOutputStream;
    private final List<List<Integer>> codesList;

    /**
     * Constructs a Huffman encoder based on the specified bitOutputStream stream.
     */
    public Encoder(BitOutputStream out, List<List<Integer>> codesList) {
        bitOutputStream = Objects.requireNonNull(out);
        this.codesList = Objects.requireNonNull(codesList);
    }

    public void write(int bytePattern) throws IOException {
        List<Integer> bits = codesList.get(bytePattern);
        for (int b : bits)
            bitOutputStream.write(b);
    }

}
