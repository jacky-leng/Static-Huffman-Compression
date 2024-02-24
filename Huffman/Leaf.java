package Huffman;

/**
 * A leaf node in a code tree. It has a symbol value. Immutable.
 */
public record Leaf(int symbol) implements Node {
    public Leaf {
        if (symbol < 0) throw new IllegalArgumentException("Symbol value must be non-negative");
    }
}
