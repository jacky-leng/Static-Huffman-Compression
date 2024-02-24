package Huffman;

import java.util.Objects;

/**
 * An internal node in a code tree. It has two nodes as children. Immutable.
 */
public record InternalNode(Node leftChild, Node rightChild) implements Node {
    public InternalNode {
        Objects.requireNonNull(leftChild);
        Objects.requireNonNull(rightChild);
    }
}
