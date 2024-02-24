package FileProcess;

import BitwiseStream.BitInputStream;
import Huffman.InternalNode;
import Huffman.Leaf;
import Huffman.Node;

import java.io.IOException;
import java.util.Objects;


/**
 * Reads from a Huffman-coded bit stream and decodes
 * <p>
 * Use the tree form
 */
public final class Decoder {
    private final BitInputStream input;
    /**
     * The code tree to use in the next read() operation. Must be given a non-{@code null}
     * value before calling read(). The tree can be changed after each symbol decoded, as long
     * as the encoder and decoder have the same tree at the same point in the code stream.
     */
    private final InternalNode root;

    /**
     * Constructs a Huffman decoder based on the specified bit input stream.
     *
     * @param in the bit input stream to read from
     * @throws NullPointerException if the input stream is {@code null}
     */
    public Decoder(BitInputStream in, InternalNode root) {
        input = Objects.requireNonNull(in);
        this.root = root;
    }

    /**
     * Reads from the input stream to decode the next Huffman-coded symbol.
     *
     * @return the next symbol in the stream, which is non-negative
     */
    public int read() throws IOException {
        InternalNode currentNode = root;
        while (true) {
            int temp = input.read();
            Node nextNode;
            if (temp == 0) nextNode = currentNode.leftChild();
            else if (temp == 1) nextNode = currentNode.rightChild();
            else throw new AssertionError("Invalid value from BitInputStream");

            if (nextNode instanceof Leaf l) return l.symbol();
            else currentNode = (InternalNode) nextNode;
        }
    }

}
